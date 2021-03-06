package com.mujugroup.core.service;


import java.util.Map;

public interface MergeService {

    Map<String, String> getHidMapByAid(String param);

    Map<String, String> getOidMapByHid(String param);

    Map<String, String> getNewlyActiveCount(String param);

    Map<String, String> getTotalActiveCount(String param);

    Map<String,String> getAgentById(String param);

    Map<String,String> getHospitalById(String param);

    Map<String,String> getProvinceByHid(String param);

    Map<String,String> getCityByHid(String param);

    Map<String,String> getDepartmentById(String param);

    Map<String,String> getBedInfoByDid(String param);

    Map<String, String> getAuthTree(String param);


}
