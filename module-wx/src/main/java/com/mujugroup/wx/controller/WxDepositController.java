package com.mujugroup.wx.controller;

import com.lveqia.cloud.common.exception.BaseException;
import com.lveqia.cloud.common.util.ResultUtil;
import com.mujugroup.wx.objeck.vo.deposit.PutVo;
import com.mujugroup.wx.service.WxDeductionRecordService;
import com.mujugroup.wx.service.WxDepositService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author leolaurel
 */

@RestController
@Api(description = "押金接口")
public class WxDepositController {

    private final WxDepositService wxDepositService;
    private final WxDeductionRecordService wxDeductionRecordService;

    @Autowired
    public WxDepositController(WxDepositService wxDepositService, WxDeductionRecordService wxDeductionRecordService) {
        this.wxDepositService = wxDepositService;
        this.wxDeductionRecordService = wxDeductionRecordService;
    }

    @ApiOperation(value = "获取当前用户已支付状态的押金信息", notes = "获取当前用户已支付状态的押金信息")
    @RequestMapping(value = "/deposit/info", method = RequestMethod.POST)
    public String getDepositInfo(@ApiParam(value = "sessionThirdKey", required = true)
                                 @RequestParam(value = "sessionThirdKey") String sessionThirdKey) {
        return ResultUtil.success(wxDepositService.getDepositInfo(sessionThirdKey));
    }

    @ApiOperation(value = "修改押金状态", notes = "申请退款时,修改当前用户的押金状态")
    @RequestMapping(value = "/deposit/refund", method = RequestMethod.PUT)
    public String modifyDepositStatus(@ApiParam(value = "sessionThirdKey", required = true)
                                      @RequestParam(value = "sessionThirdKey") String sessionThirdKey
            , @ApiParam(value = "当前选中的押金信息ID") @RequestParam(value = "id") long id) throws BaseException {
        return ResultUtil.success(wxDepositService.modifyStatus(sessionThirdKey, id));
    }

    @ApiOperation(value = "押金列表", notes = "获取所有状态押金列表，优先显示退款中的状态")
    @RequestMapping(value = "/audit/list", method = RequestMethod.POST)
    public String getRefundingList(@ApiParam(value = "订单号") @RequestParam(value = "tradeNo", required = false, defaultValue = "") String tradeNo) {
        return ResultUtil.success(wxDepositService.getInfoList(tradeNo));
    }

    @ApiOperation(value = "修改押金状态为审核通过以及其他记录表状态,插入退款记录表"
            , notes = "修改押金状态为审核通过以及其他记录表状态,插入退款记录表")
    @RequestMapping(value = "/audit/deposit", method = RequestMethod.PUT)
    public String modifyStatus(@Validated @ModelAttribute PutVo infoVo) throws BaseException {
        return ResultUtil.success(wxDepositService.modifyRecordStatus(infoVo));
    }

    @ApiOperation(value = "扣款记录列表", notes = "扣款记录列表")
    @RequestMapping(value = "/audit/deduction", method = RequestMethod.POST)
    public String getDeductionList(@ApiParam(value = "微信唯一对外ID") @RequestParam(value = "openId") String openId) {
        return ResultUtil.success(wxDeductionRecordService.getList(openId));
    }

}
