package com.mujugroup.core.objeck.bean;

import com.github.wxiaoqi.merge.annonation.MergeField;
import com.lveqia.cloud.common.config.Constant;
import com.mujugroup.core.service.MergeService;
import com.mujugroup.core.service.feign.ModuleWxService;

import java.io.Serializable;

public class StatusHidBean implements Serializable {

    private int oid;

    private int hid;

    private int aid;

    private int actCount;

    @MergeField(defaultValue = "未知", feign = MergeService.class, method = "getOidMapByHid", isQueryByParam = true)
    private String department;

    @MergeField(defaultValue = Constant.DIGIT_ZERO, feign = ModuleWxService.class, method = "getPayCount"
            , isQueryByParam = true)
    private String payCount;

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getHid() {
        return hid;
    }

    public void setHid(int hid) {
        this.hid = hid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getActCount() {
        return actCount;
    }

    public void setActCount(int actCount) {
        this.actCount = actCount;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPayCount() {
        return payCount;
    }

    public void setPayCount(String payCount) {
        this.payCount = payCount;
    }
}

