package com.mujugroup.wx.service.impl;

import com.lveqia.cloud.common.exception.BaseException;
import com.lveqia.cloud.common.exception.ParamException;
import com.lveqia.cloud.common.util.StringUtil;
import com.mujugroup.wx.config.MyConfig;
import com.mujugroup.wx.mapper.WxDepositMapper;
import com.mujugroup.wx.model.WxDeposit;
import com.mujugroup.wx.model.WxOrder;
import com.mujugroup.wx.model.WxRecordMain;
import com.mujugroup.wx.model.WxRefundRecord;
import com.mujugroup.wx.objeck.vo.deposit.InfoListVo;
import com.mujugroup.wx.objeck.vo.deposit.PutVo;
import com.mujugroup.wx.service.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author leolaurel
 */
@Service("wxDepositService")
public class WxDepositServiceImpl implements WxDepositService {


    private final WxDepositMapper wxDepositMapper;
    private final WxRecordMainService wxRecordMainService;
    private final SessionService sessionService;
    private final WxOrderService wxOrderService;
    private final PayApiService payApiService;
    private final WxRefundRecordService wxRefundRecordService;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(WxDepositServiceImpl.class);

    @Autowired
    public WxDepositServiceImpl(WxDepositMapper wxDepositMapper, WxRecordMainService wxRecordMainService, SessionService sessionService, WxOrderService wxOrderService, PayApiService payApiService, WxRefundRecordService wxRefundRecordService) {
        this.wxDepositMapper = wxDepositMapper;
        this.wxRecordMainService = wxRecordMainService;
        this.sessionService = sessionService;
        this.wxOrderService = wxOrderService;
        this.payApiService = payApiService;
        this.wxRefundRecordService = wxRefundRecordService;
    }

    @Override
    public boolean insert(WxDeposit wxDeposit) {
        return wxDepositMapper.insert(wxDeposit);
    }


    @Override
    public WxDeposit getDepositInfo(String sessionThirdKey) {
        String openId = sessionService.getOpenId(sessionThirdKey);
        return wxDepositMapper.getFinishDeposit(openId);
    }

    @Transactional
    @Override
    /**
     * 修改押金表,支付主表中的相关状态
     */
    //TODO 此处暂时进行全部一次性退款,后期再进行版本迭代以支持多次退款
    public boolean modifyRecordStatus(PutVo infoVo) throws BaseException {
        WxDeposit wxDeposit = wxDepositMapper.getRefundingWxDepositById(infoVo.getId());
        if (wxDeposit == null) throw new ParamException("该记录不存在,请重新选择!");
        WxOrder order = wxOrderService.getOrderByOpenidAndTradeNo(wxDeposit.getOpenId(), WxOrder.TYPE_PAY_SUCCESS, wxDeposit.getTradeNo());
        if (order == null) throw new ParamException("无此订单记录!");
        if (order.getEndTime() > System.currentTimeMillis() / 1000) throw new ParamException("当前订单仍在有效期内，暂无法退款");
        WxRecordMain wxRecordMain = wxRecordMainService.getFinishPayRecordByNo(wxDeposit.getTradeNo(), wxDeposit.getOpenId());
        if (wxRecordMain == null) throw new ParamException("该记录不存在!");
        if (wxRecordMain.getRefundCount() > 9) throw new ParamException("当前退款次数已超过10次,无法进行退款操作");
        if (wxDeposit.getDeposit() == 0) throw new ParamException("您的押金为0元,请先缴纳押金!");
        logger.debug("开始调用微信退款接口");
        //调用微信退款接口
        Map<String, String> map = payApiService.refund(wxRecordMain.getRefundCount() + 1, wxDeposit.getTradeNo()
                , wxRecordMain.getTotalPrice().longValue(), wxDeposit.getDeposit().longValue()
                , infoVo.getRefundDesc(), MyConfig.REFUND_SOURCE_UNSETTLED_FUNDS);
        logger.debug("微信输出{}", map);
        if (map != null && MyConfig.SUCCESS.equals(map.get("return_code")) && MyConfig.SUCCESS.equals(map.get("result_code"))) {
            wxRecordMain.setRefundPrice(wxDeposit.getDeposit());//设置支付记录主表的退款金额
            wxRecordMain.setTotalPrice(wxRecordMain.getTotalPrice() - wxRecordMain.getRefundPrice());//设置总金额
            wxRecordMain.setRefundCount(wxRecordMain.getRefundCount() + 1);//设置当前退款次数
            wxDeposit.setStatus(WxDeposit.PASS_AUDIT);//设置当前押金表的状态为审核通过
            wxDeposit.setUpdTime(new Date());
            wxDeposit.setDeposit(WxDeposit.NO_MONEY);//设置当前押金金额为0
            boolean isTrue = wxRecordMainService.update(wxRecordMain);
            isTrue &= wxDepositMapper.update(wxDeposit);
            String wxRefundNo = wxDeposit.getTradeNo() + wxRecordMain.getRefundCount();
            WxRefundRecord wxRefundRecord = bindWxRefundRecord(wxDeposit.getOpenId(), wxDeposit.getTradeNo()
                    , wxRefundNo, wxRecordMain.getRefundCount(), wxRecordMain.getRefundPrice()
                    , wxRecordMain.getTotalPrice(), WxRefundRecord.PAY_SUCCESS
                    , StringUtil.isEmpty(infoVo.getRefundDesc()) == true ? "微信退款" : infoVo.getRefundDesc());
            isTrue &= wxRefundRecordService.insert(wxRefundRecord);
            return isTrue;

        }
        return false;
    }

    @Override
    public WxDeposit getRefundingWxDepositById(Long id) {
        return wxDepositMapper.getRefundingWxDepositById(id);
    }

    @Override
    public List<InfoListVo> getInfoList() {
        return wxDepositMapper.getInfoList();
    }

    @Override
    public boolean modifyStatus(String sessionThirdKey, long id) throws BaseException {
        String openId = sessionService.getOpenId(sessionThirdKey);
        WxDeposit wxDeposit = wxDepositMapper.findDepositById(openId, id);
        if (wxDeposit == null) throw new ParamException("该用户暂未缴纳押金");
        WxOrder order = wxOrderService.getOrderByOpenidAndTradeNo(wxDeposit.getOpenId(), WxOrder.TYPE_PAY_SUCCESS, wxDeposit.getTradeNo());
        if (order == null) throw new ParamException("无此订单记录!");
        if (order.getEndTime() > System.currentTimeMillis() / 1000) throw new ParamException("当前订单仍在有效期内，暂无法退款");
        wxDeposit.setStatus(WxDeposit.REFUNDING_MONEY);//设置当前押金状态为退款中
        wxDeposit.setUpdTime(new Date());
        return wxDepositMapper.update(wxDeposit);
    }

    @Override
    public WxDeposit getFinishDeposit(String openId) {
        return wxDepositMapper.getFinishDeposit(openId);
    }

    @Override
    public boolean update(WxDeposit wxDeposit) {
        return wxDepositMapper.update(wxDeposit);
    }

    /**
     * 绑定退款实体模型
     *
     * @param openId
     * @param tradeNo
     * @param refundNo
     * @param refundCount
     * @param refundPrice
     * @param totalPrice
     * @param refundStatus
     * @return
     */
    private WxRefundRecord bindWxRefundRecord(String openId, String tradeNo, String refundNo, int refundCount
            , int refundPrice, int totalPrice, int refundStatus, String desc) {
        WxRefundRecord wxRefundRecord = new WxRefundRecord();
        wxRefundRecord.setOpenId(openId);
        wxRefundRecord.setTradeNo(tradeNo);
        wxRefundRecord.setRefundNo(refundNo);
        wxRefundRecord.setRefundCount(refundCount);
        wxRefundRecord.setRefundPrice(refundPrice);
        wxRefundRecord.setTotalPrice(totalPrice);
        wxRefundRecord.setRefundStatus(refundStatus);
        wxRefundRecord.setRefundDesc(desc);
        return wxRefundRecord;
    }
}
