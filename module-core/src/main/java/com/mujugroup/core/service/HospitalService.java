package com.mujugroup.core.service;

import com.lveqia.cloud.common.exception.DataException;
import com.lveqia.cloud.common.exception.ParamException;
import com.mujugroup.core.model.Hospital;
import com.mujugroup.core.objeck.bo.HospitalBO;
import com.mujugroup.core.objeck.vo.hospital.AddVo;
import com.mujugroup.core.objeck.vo.hospital.ListVo;
import com.mujugroup.core.objeck.vo.hospital.PutVo;
import com.mujugroup.core.objeck.vo.SelectVO;

import java.util.List;
import java.util.Map;

/**
 * @author leolaurel
 */
public interface HospitalService {

    List<SelectVO> getHospitalList(int aid, String name);

    List<SelectVO> getHospitalListByUid(String type, long uid);

    List<SelectVO> getAgentHospitalListByUid(String type, long uid);

    List<Hospital> findListByAid(String aid);

    List<Hospital> getHospitalByRegion(String pid, String cid);

    List<HospitalBO> getHospitalBoByIds(String[] array);

    String getHospitalName(int id);

    boolean add(int uid, AddVo addVo) throws ParamException, DataException;

    boolean modify(int uid, PutVo hospitalPutVo) throws ParamException, DataException;

    boolean remove(int uid, String hid) throws ParamException, DataException;

    Integer getProvinceCity(int cid, int pid);

    Integer isExitsName(String name);

    List<ListVo> findAll(Map<String,String> map,int uid, String aid, String name, int provinceId, int cityId, int enable) throws DataException;

    List<SelectVO> selectAll();

    /**
     * 根据AID查询HID，多个逗号分隔
     */
    String getHidByAid(String aid);
}
