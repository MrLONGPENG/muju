package com.mujugroup.wx.service.impl;

import com.lveqia.cloud.common.util.StringUtil;
import com.lveqia.cloud.common.config.Constant;
import com.lveqia.cloud.common.objeck.DBMap;
import com.mujugroup.wx.model.WxGoods;
import com.mujugroup.wx.model.WxOrder;
import com.mujugroup.wx.service.MergeService;
import com.mujugroup.wx.service.WxGoodsService;
import com.mujugroup.wx.service.WxOrderService;
import com.mujugroup.wx.service.WxUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("mergeService")
public class MergeServiceImpl implements MergeService {

    private final WxUserService wxUserService;
    private final WxOrderService wxOrderService;
    private final WxGoodsService wxGoodsService;
    private final Logger logger = LoggerFactory.getLogger(MergeServiceImpl.class);


    public MergeServiceImpl(WxUserService wxUserService, WxOrderService wxOrderService
            , WxGoodsService wxGoodsService) {
        this.wxUserService = wxUserService;
        this.wxOrderService = wxOrderService;
        this.wxGoodsService = wxGoodsService;

    }

    /**
     * 询最近24小时的支付数量
     *
     * @param param aid 或 aid&hid
     * @return key:did或hid value:count
     */
    @RequestMapping(value = "/getPayCount", method = RequestMethod.POST)
    public Map<String, String> getPayCount(String param) {
        logger.warn("order-getPayCount[{}]", param);
        String[] ids = param.split(Constant.SIGN_AND);
        HashMap<String, String> hashMap = new HashMap<>();
        if (ids.length == 1) {
            List<DBMap> list = wxOrderService.getPayCountByAid(ids[0]);
            list.forEach(dbMap -> dbMap.addTo(hashMap));
        } else if (ids.length == 2) {
            List<DBMap> list = wxOrderService.getPayCountByHid(ids[0], ids[1]);
            list.forEach(dbMap -> dbMap.addTo(hashMap));
        }
        logger.debug(hashMap.toString());
        return hashMap;
    }

    /**
     * 获取最后一次支付的信息
     *
     * @param param did (ps:多个数据分号分割)\
     * @return key:did value:DID;订单号;支付金额(分);支付时间;到期时间(秒)
     */
    @RequestMapping(value = "/getPaymentInfo", method = RequestMethod.POST)
    public Map<String, String> getPaymentInfo(String param) {
        HashMap<String, String> hashMap = new HashMap<>();
        String[] array = param.split(Constant.SIGN_FEN_HAO);
        WxOrder wxOrder;
        StringBuilder sb;
        for (String did : array) {
            if (StringUtil.isEmpty(did)) continue;
            wxOrder = wxOrderService.findLastOrderByDid(did);
            if (wxOrder != null) {
                // DID;订单号;支付金额(分);支付时间;到期时间(秒)
                sb = new StringBuilder(StringUtil.autoFillDid(wxOrder.getDid()));
                sb.append(Constant.SIGN_FEN_HAO).append(wxOrder.getTradeNo());
                sb.append(Constant.SIGN_FEN_HAO).append(wxOrder.getPayPrice());
                sb.append(Constant.SIGN_FEN_HAO).append(wxOrder.getPayTime());
                sb.append(Constant.SIGN_FEN_HAO).append(wxOrder.getEndTime());
                hashMap.put(did, new String(sb));
            }
        }
        return hashMap;
    }

    /*
        获取最后一次订单的到期时间
     */
    @RequestMapping(value = "/getOrderEndTimeByDid", method = RequestMethod.POST)
    public Map<String, String> getOrderEndTimeByDid(String param) {
        HashMap<String, String> hashMap = new HashMap<>();
        String[] array = param.split(Constant.SIGN_FEN_HAO);
        for (String did : array) {
            if (StringUtil.isEmpty(did)) continue;
            String endTime = wxOrderService.getOrderEndTimeByDid(did);
            if (!StringUtil.isEmpty(endTime)) {
                //到期时间(秒)
                hashMap.put(did, endTime);
            }
        }
        return hashMap;
    }


    /**
     * 根据条件获取指定时间类的使用数量
     *
     * @param param 代理商ID&医院ID&科室ID&日期字符(代理商、医院皆可使用多个查询，逗号分隔)
     *              日期字符串以"no"开头，不使用缓存
     * @return key:aid&hid&oid&date value:count
     */
    @Override
    public Map<String, String> getUsageCount(String param) {
        logger.debug("getUsageCount->{}", param);
        Map<String, String> map = new HashMap<>();
        String[] array = param.split(Constant.SIGN_FEN_HAO);
        for (String key : array) {
            if (StringUtil.isEmpty(key)) continue;
            String[] keys = key.split(Constant.SIGN_AND);
            keys[1] = StringUtil.formatIds(keys[1]);
            if (keys.length != 4) {
                logger.error("使用数查询接口改为按日期字符查询");
                break;
            }
            if(keys[3].startsWith("no")){
                map.put(key, wxOrderService.getUsageCountFromDb(keys[0], keys[1], keys[2], keys[3].substring(2)));
            }else{
                map.put(key, wxOrderService.getUsageCount(keys[0], keys[1], keys[2], keys[3]));
            }

        }
        return map;
    }


    /**
     * 根据条件获取指定时间类的使用率（子循环有采用缓存，请注意查询参数date）
     *
     * @param param 代理商ID&医院ID&科室ID&日期字符
     * @return key:aid&hid&oid&date value:count
     */
    @Override
    public Map<String, String> getUsageRate(String param) {
        logger.debug("getUsageRate->{}", param);
        Map<String, String> map = new HashMap<>();
        String[] keys, array = param.split(Constant.SIGN_FEN_HAO);
        for (String key : array) {
            if (StringUtil.isEmpty(key)) continue;
            keys = key.split(Constant.SIGN_AND);
            map.put(key, wxOrderService.getUsageRate(keys[0], StringUtil.formatIds(keys[1]), keys[2], keys[3]));
        }
        return map;
    }


    /**
     * 获取指定时间内、指定条件下的利润总和
     *
     * @param param 代理商ID&医院ID&科室ID&开始时间戳&结束时间戳&日期字符 (ps:日期字符可能为空)
     * @return key:aid&hid&oid&start&end&date value:profit(单位分)
     */
    @Override
    public Map<String, String> getTotalProfit(String param) {
        logger.debug("getTotalProfit->{}", param);
        Map<String, String> map = new HashMap<>();
        String[] keys, array = param.split(Constant.SIGN_FEN_HAO);
        for (String key : array) {
            if (StringUtil.isEmpty(key)) continue;
            keys = key.split(Constant.SIGN_AND);
            keys[1] = StringUtil.formatIds(keys[1]);
            if (keys.length == 6) { // date 格式 yyyyMM yyyyMMdd yyyyMMdd-yyyyMMdd
                map.put(key, wxOrderService.getTotalProfitByDate(keys[0], keys[1], keys[2], keys[5]));
            } else {
                map.put(key, wxOrderService.getTotalProfit(keys[0], keys[1], keys[2], keys[3], keys[4]));
            }
        }
        return map;
    }


    /**
     * 根据条件获取全部的用户（子循环有采用缓存，请主要查询时间参数）
     *
     * @param param 开始时间戳&截止时间戳（0等于除去该条件）
     * @return key:start&end value:count
     */
    @Override
    public Map<String, String> getUserCount(String param) {
        logger.debug("getTotalUserCount->{}", param);
        Map<String, String> map = new HashMap<>();
        String[] array = param.split(Constant.SIGN_FEN_HAO);
        for (String key : array) {
            if (StringUtil.isEmpty(key)) continue;
            String[] keys = key.split(Constant.SIGN_AND);
            map.put(key, wxUserService.getTotalUserCount(keys[0], keys[1]));
        }
        return map;
    }

}
