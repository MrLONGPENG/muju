package com.mujugroup.data.controller;

import com.lveqia.cloud.common.exception.BaseException;
import com.lveqia.cloud.common.util.ResultUtil;
import com.lveqia.cloud.common.util.StringUtil;
import com.lveqia.cloud.common.config.Constant;
import com.mujugroup.data.objeck.bo.ProfitBo;
import com.mujugroup.data.objeck.bo.UsageBo;
import com.mujugroup.data.service.OverviewService;
import com.mujugroup.data.service.StaVoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/overview")
@Api(description="数据模块概览接口")
public class OverviewController {
    private final Logger logger = LoggerFactory.getLogger(OverviewController.class);

    private final StaVoService staVOService;
    private final OverviewService overviewService;

    @Autowired
    public OverviewController(StaVoService staVOService, OverviewService overviewService) {
        this.staVOService = staVOService;
        this.overviewService = overviewService;
    }

    @ApiOperation(value="查询概览使用数据(不包含当日数据)", notes="根据条件查询概览数据(已激活数，总用户数，昨日使用数)")
    @RequestMapping(value = "/usage",method = RequestMethod.POST)
    public String usage(@ApiParam(value="代理商ID，无ID或无ID即查询全部代理商") @RequestParam(name="aid"
            , defaultValue=Constant.DIGIT_ZERO) String aid, @ApiParam(value="概览统计时间戳(秒)，时间戳为0或为空" +
            "，默认按当前时间计算") @RequestParam(name="timestamp", defaultValue=Constant.DIGIT_ZERO) long timestamp
            , @ApiParam(hidden = true) String uid){
        logger.debug("overview->usage {} {}", aid, timestamp);
        try {
            UsageBo usageBO = overviewService.usage(uid, aid, timestamp);
            if(usageBO!=null) {
                String percent = StringUtil.getPercent(usageBO.getYesterdayUsageCount(), usageBO.getTotalActive());
                usageBO.setYesterdayUsageRate(percent);
                return ResultUtil.success(usageBO);
            }
            return ResultUtil.error(ResultUtil.CODE_NOT_FIND_DATA);
        } catch (BaseException e) {
            return ResultUtil.error(e.getCode(), e.getMessage());
        }


    }


    @ApiOperation(value="查询概览收益数据(不包含当日数据)", notes="根据条件查询收益数据(总收益、昨日收益)")
    @RequestMapping(value = "/profit",method = RequestMethod.POST)
    public String profit(@ApiParam(value="代理商ID，无ID或无ID即查询全部代理商") @RequestParam(name="aid"
            , defaultValue=Constant.DIGIT_ZERO) String aid, @ApiParam(value="概览统计时间戳(秒)，时间戳为0或为空" +
            "，默认按当前时间计算") @RequestParam(name="timestamp", defaultValue=Constant.DIGIT_ZERO) long timestamp
            , @ApiParam(hidden = true) String uid){
        logger.debug("overview->profit {} {}", aid, timestamp);
        try {
            ProfitBo profitBO = overviewService.profit(uid, aid, timestamp);
            if(profitBO!=null) return ResultUtil.success(staVOService.getProfitVO(profitBO));
            return ResultUtil.error(ResultUtil.CODE_NOT_FIND_DATA);
        } catch (BaseException e) {
            return ResultUtil.error(e.getCode(), e.getMessage());
        }
    }
}
