package com.mujugroup.core.controller;

import com.mujugroup.core.service.FeignService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * 服务模块之间调用接口
 */
@RestController
@RequestMapping("/feign")
public class FeignController {


    private FeignService feignService;

    @Autowired
    public FeignController(FeignService feignService) {
        this.feignService = feignService;
    }

    /**
     * 根据代理商ID获取医院ID 以及医院名称
     * @param aid 代理商ID
     * @return key:hid value:name
     */
    @RequestMapping(value = "/getHospitalByAid",method = RequestMethod.POST)
    public Map<Integer, String> getHospitalByAid(@RequestParam(value = "aid") String aid){
        return feignService.getHospitalByAid(aid);
    }

    /**
     * 根据省份ID 以及城市ID 获取代理商医院ID和代理商ID
     * @param pid 省份ID cid 城市ID
     * @return set:hid
     */
    @RequestMapping(value = "/getHospitalByRegion",method = RequestMethod.POST)
    public Set<Integer> getHospitalByRegion(@RequestParam(value = "pid") String pid
            , @RequestParam(value = "cid") String cid){
        return feignService.getHospitalByRegion(pid, cid);
    }

    @RequestMapping(value = "/addAuthData",method = RequestMethod.POST)
    public int addAuthData(int uid, String[] authDatas){
       return feignService.addAuthData(uid,authDatas);
    }
}
