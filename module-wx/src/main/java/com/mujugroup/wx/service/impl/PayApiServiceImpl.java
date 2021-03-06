package com.mujugroup.wx.service.impl;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayUtil;
import com.lveqia.cloud.common.config.Constant;
import com.lveqia.cloud.common.exception.BaseException;
import com.lveqia.cloud.common.exception.ParamException;
import com.lveqia.cloud.common.exception.TokenException;
import com.lveqia.cloud.common.util.DateUtil;
import com.lveqia.cloud.common.util.ResultUtil;
import com.lveqia.cloud.common.util.StringUtil;
import com.lveqia.cloud.common.cache.ILocalCache;
import com.mujugroup.wx.config.MyConfig;
import com.mujugroup.wx.model.*;
import com.mujugroup.wx.service.*;
import com.mujugroup.wx.service.feign.ModuleLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@RefreshScope
@Service("payApiService")
public class PayApiServiceImpl implements PayApiService {
    private final Logger logger = LoggerFactory.getLogger(PayApiServiceImpl.class);
    private final WXPayConfig wxPayConfig = new MyConfig();
    private final WXPay wxPay = new WXPay(wxPayConfig);
    private final UsingApiService usingApiService;
    private final WxGoodsService wxGoodsService;
    private final ModuleLockService moduleLockService;
    private final WxRecordMainService wxRecordMainService;


    @Value("${wx_url_notify}")
    String notifyUrl;


    @Value("${spring.profiles.active}")
    private String model;

    @Resource(name = "endTimeCache")
    private ILocalCache<String, Long> endTimeCache;

    @Autowired
    public PayApiServiceImpl(UsingApiService usingApiService, WxGoodsService wxGoodsService
            , ModuleLockService moduleLockService, WxRecordMainService wxRecordMainService) {
        this.wxGoodsService = wxGoodsService;
        this.usingApiService = usingApiService;
        this.moduleLockService = moduleLockService;
        this.wxRecordMainService = wxRecordMainService;
    }


    @Override
    public Map<String, String> requestPay(String sessionThirdKey, String code, String strInfo
            , String ip) throws BaseException {
        logger.info("wx-requestPay-ip:" + ip);
        if (ip == null || ip.startsWith("0:")) ip = "116.62.228.47";
        String[] arr = usingApiService.parseCode(sessionThirdKey, code);
        String orderNo = DateUtil.dateToString(DateUtil.TYPE_DATETIME_14)
                + StringUtil.getRandomString(6, true);
        List<WxRecordAssist> wxRecordAssists = parseAssistInfo(strInfo);
        //任意一个商品无法查出或商品数据无法匹配,即说明商品信息有误
        if (wxRecordAssists.size() == 0 || wxRecordAssists.stream().anyMatch(
                s -> s.hasError(wxGoodsService.findById(s.getGid())))) {
            throw new ParamException(ParamException.GOODS_INFO_ERROR);
        }
        try {
            String[] mainInfo = getMainInfo(wxRecordAssists);
            Map<String, String> map = wxPay.unifiedOrder(getParamsMap(ip, arr[0], orderNo, mainInfo));
            map.put("out_trade_no", orderNo);
            logger.info("统一下单成功,NO:{}", orderNo);
            wxRecordMainService.insertRecord(bindRecordMain(arr[1], arr[0], arr[2], arr[3], arr[4], orderNo
                    , mainInfo[1]) , wxRecordAssists); // 统一事务执行
            return map;
        } catch (Exception e) {
            logger.warn("统一下单错误", e);
            throw new BaseException(ResultUtil.CODE_UNKNOWN_ERROR);
        }
    }

    private Map<String, String> getParamsMap(String ip, String value, String orderNo, String[] mainInfo) {
        Map<String, String> params = new HashMap<>();
        params.put("body", "木巨支付-单天计费");//商品描述
        params.put("attach", "上海木巨健康科技");
        params.put("detail", mainInfo[0]);//所选商品类型
        params.put("openid", value);//openid
        params.put("total_fee", mainInfo[1]);//总金额
        params.put("out_trade_no", orderNo);//商户订单号
        params.put("notify_url", notifyUrl);
        params.put("spbill_create_ip", ip);//订单生成的机器 IP
        params.put("trade_type", "JSAPI");
        return params;
    }

    private String[] getMainInfo(List<WxRecordAssist> wxRecordAssists) {
        int totalPrice = 0;
        StringBuffer sb = new StringBuffer();
        for (WxRecordAssist assist : wxRecordAssists) {
            if (sb.length() != 0) sb.append(Constant.SIGN_PLUS);
            if (assist.getType() == 1) sb.append("押金");
            else sb.append(assist.getName());
            totalPrice += assist.getPrice();
        }
        return new String[]{new String(sb), String.valueOf(totalPrice)};
    }

    private List<WxRecordAssist> parseAssistInfo(String strInfo) {
        WxRecordAssist wxRecordAssist;
        List<WxRecordAssist> list = new ArrayList<>();
        //兼容旧版本
        if (StringUtil.isNumeric(strInfo)) {
            WxGoods wxGoods = wxGoodsService.findById(Integer.parseInt(strInfo));
            if (wxGoods != null) {
                wxRecordAssist = new WxRecordAssist();
                wxRecordAssist.setType(wxGoods.getType());
                wxRecordAssist.setGid(wxGoods.getId());
                wxRecordAssist.setPrice(wxGoods.getPrice());
                list.add(wxRecordAssist);
            }
        }else{
            String[] strArr = strInfo.split(Constant.SIGN_AND);
            for (String assist : strArr) {
                String[] arrItem = assist.split(Constant.SIGN_LINE);
                wxRecordAssist = new WxRecordAssist();
                wxRecordAssist.setType(Integer.parseInt(arrItem[0]));
                wxRecordAssist.setGid(Integer.parseInt(arrItem[1]));
                wxRecordAssist.setPrice(Integer.parseInt(arrItem[2]));
                list.add(wxRecordAssist);
            }
        }

        return list;
    }

    @Override
    public String completePay(String notifyXml) throws Exception {
        logger.debug("微信支付回调接收数据:{}", notifyXml);
        Map<String, String> map = WXPayUtil.xmlToMap(notifyXml);
        if (Constant.MODEL_DEV.equals(model) || WXPayUtil.isSignatureValid(map, wxPayConfig.getKey())
                && MyConfig.SUCCESS.equals(map.get("result_code"))
                && MyConfig.SUCCESS.equals(map.get("return_code"))) {
            String orderNo = map.get("out_trade_no");
            String transactionId = map.get("transaction_id");
            String openId = map.get("openid");
            //更新统一支付状态
            WxRecordMain wxRecordMain = wxRecordMainService.findMainRecordByNo(orderNo);
            if (wxRecordMain != null) {
                wxRecordMain.setTransactionId(transactionId);
                wxRecordMain.setPayStatus(WxRecordMain.FINISH_PAY);
                List<WxRecordAssist> list = wxRecordMain.getAssistList();
                List<WxBase> wxBaseList = new ArrayList<>();
                if (list != null) {
                    for (WxRecordAssist assist : list) {
                        if (assist.getType() == 1) { // 押金
                            wxBaseList.add(bindWxDeposit(assist.getGid(), openId, assist.getPrice(), orderNo));
                        } else if (assist.getType() == 2 || assist.getType() == 3) { //兼容旧版本
                            long payTime = System.currentTimeMillis() / 1000;
                            long endTime = endTimeCache.get(wxRecordMain.getKey(assist.getGid()));
                            //将普通商品套餐记录到订单表中
                            wxBaseList.add(bindWxOrder(wxRecordMain.getDid(), openId, wxRecordMain.getAid()
                                    , wxRecordMain.getHid(), wxRecordMain.getOid(), orderNo, assist.getType()
                                    , payTime, endTime, assist.getGid(), assist.getPrice(), transactionId));
                            wxBaseList.add(bindWxUsing(openId, wxRecordMain.getDid(),assist.getPrice(), payTime
                                    , endTime, Constant.LOCK_OPEN.equals(moduleLockService.getLockStatus(String
                                            .valueOf(wxRecordMain.getDid())))));
                        } else {
                            logger.warn("不支持类型:{}, 待处理", assist.getType());
                        }
                    }
                } else {
                    logger.warn("支付完成辅表无记录,要进行处理!!!");
                    wxRecordMain.setPayStatus(WxRecordMain.PAY_ERROR);
                }
                wxBaseList.add(wxRecordMain);
                usingApiService.paymentCompleted(wxBaseList); // 统一执行事务
            }
        } else {
            logger.warn("验证微信数据错误 result_code:{} return_code:{} out_trade_no:{}"
                    , map.get("result_code"), map.get("return_code"), map.get("out_trade_no"));
        }
        Map<String, String> data = new HashMap<>();
        data.put("return_code", MyConfig.SUCCESS);
        data.put("return_msg", "OK");
        return WXPayUtil.mapToXml(data);
    }

    @Override
    public Map<String, String> refund(int index, String orderNo, Long totalFee, Long refundFee
            , String refundDesc, String refundAccount) {
        SortedMap<String, String> params = new TreeMap<>();
        //商户订单号和微信订单号二选一
        params.put("out_trade_no", orderNo);
        params.put("out_refund_no", orderNo + index);
        params.put("total_fee", String.valueOf(totalFee));
        params.put("refund_fee", String.valueOf(refundFee));
        params.put("refund_desc", refundDesc);
        params.put("refund_account", refundAccount);
        try {
            Map<String, String> map = wxPay.refund(params); //加入微信支付日志
            logger.debug("微信消息-传入参数{};微信输出{}", params, map);
            if (map != null && MyConfig.SUCCESS.equals(map.get("result_code"))
                    && MyConfig.SUCCESS.equals(map.get("return_code"))) {//插入数据库
                logger.debug("out_trade_no {} out_refund_no{}", orderNo, orderNo + index);
                logger.debug("refund_id {} {}", map.get("refund_id"), map.get("err_code"));
            }
            if (map != null && MyConfig.FAIL.equals(map.get("result_code"))
                    && MyConfig.SUCCESS.equals(map.get("return_code"))
                    && MyConfig.REFUND_NOT_ENOUGH_MONEY.equals(map.get("err_code"))
                    && MyConfig.REFUND_SOURCE_UNSETTLED_FUNDS.equals(refundAccount)) {
                logger.debug("未结算金额不足  使用余额退款");
                map = refund(index, orderNo, totalFee, refundFee, refundDesc, MyConfig.REFUND_SOURCE_RECHARGE_FUNDS);
            }
            return map;
        } catch (Exception e) {//微信退款接口异常
            logger.debug("微信消息-传入参数{};异常信息-{}", params, e.getMessage());
        }
        return null;
    }

    private WxRecordMain bindRecordMain(String did, String openId, String aid, String hid, String oid
            , String orderNo, String totalPrice) {
        WxRecordMain wxRecordMain = new WxRecordMain();
        wxRecordMain.setOpenId(openId);
        wxRecordMain.setAid(Integer.parseInt(aid));
        wxRecordMain.setDid(Long.parseLong(did));
        wxRecordMain.setHid(Integer.parseInt(hid));
        wxRecordMain.setOid(Integer.parseInt(oid));
        wxRecordMain.setTradeNo(orderNo);
        wxRecordMain.setTotalPrice(Integer.valueOf(totalPrice));
        wxRecordMain.setCrtTime(new Date());
        //设置当前支付状态为统一下单
        wxRecordMain.setPayStatus(WxRecordMain.UNIFIED_ORDER);
        wxRecordMain.setRefundCount(0);
        wxRecordMain.setRefundPrice(0);
        return wxRecordMain;
    }

    private WxOrder bindWxOrder(Long did, String openId, Integer aid, Integer hid, Integer oid, String orderNo
            , Integer goodsType, long payTime, long endTime, Integer gid, Integer price, String transactionId) {
        WxOrder wxOrder = new WxOrder();
        wxOrder.setDid(did);
        wxOrder.setAid(aid);
        wxOrder.setHid(hid);
        wxOrder.setOid(oid);
        wxOrder.setOpenId(openId);
        wxOrder.setPayStatus(WxOrder.TYPE_PAY_SUCCESS);
        wxOrder.setPayTime(payTime);
        wxOrder.setEndTime(endTime);
        wxOrder.setGid(gid);
        wxOrder.setOrderType(goodsType == WxGoods.TYPE_MIDDAY ? WxOrder.ORDER_TYPE_MIDDAY
                : goodsType == WxGoods.TYPE_NIGHT ? WxOrder.ORDER_TYPE_NIGHT : 0);
        wxOrder.setPayPrice(price);
        wxOrder.setTradeNo(orderNo);
        wxOrder.setTransactionId(transactionId);
        wxOrder.setCrtTime(new Date());
        return wxOrder;
    }

    private WxUsing bindWxUsing(String openId, Long did, Integer price, long payTime, long endTime, boolean isUsing) {
        WxUsing wxUsing = new WxUsing();
        wxUsing.setOpenId(openId);
        wxUsing.setDid(did);
        wxUsing.setPayCost(price);
        wxUsing.setPayTime(payTime);
        wxUsing.setEndTime(endTime);
        wxUsing.setUsing(isUsing);
        return wxUsing;
    }

    private WxDeposit bindWxDeposit(Integer gid, String openId, Integer deposit, String tradeNo) {
        WxDeposit wxDeposit = new WxDeposit();
        wxDeposit.setGid(gid);
        wxDeposit.setOpenId(openId);
        wxDeposit.setStatus(WxDeposit.PAY_FINISH);
        wxDeposit.setDeposit(deposit);
        wxDeposit.setTradeNo(tradeNo);
        wxDeposit.setCrtTime(new Date());
        return wxDeposit;
    }
}
