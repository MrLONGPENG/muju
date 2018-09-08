package com.mujugroup.core.service.impl;

import com.mujugroup.core.mapper.HospitalMapper;
import com.mujugroup.core.objeck.vo.SelectVO;
import com.mujugroup.core.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author leolaurel
 */
@Service("hospitalService")
public class HospitalServiceImpl implements HospitalService {


    private final HospitalMapper hospitalMapper;

    @Autowired
    public HospitalServiceImpl(HospitalMapper hospitalMapper) {
        this.hospitalMapper = hospitalMapper;
    }



    @Override
    public List<SelectVO> getHospitalList(int aid, String name) {
        return hospitalMapper.getHospitalList(aid, name);
    }
}
