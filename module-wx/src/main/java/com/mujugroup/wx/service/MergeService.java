package com.mujugroup.wx.service;


import java.util.Map;

public interface MergeService {

    Map<String, String> getPayCount(String param);

    Map<String, String> getPaymentInfo(String param);

    Map<String, String> getUserCount(String param);

    Map<String, String> getUsageCount(String param);

    Map<String, String> getUsageRate(String param);

    Map<String, String> getTotalProfit(String param);

    Map<String, String> getOrderEndTimeByDid(String param);
}
