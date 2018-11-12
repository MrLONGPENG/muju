package com.mujugroup.lock.service;

import com.lveqia.cloud.common.objeck.to.LockTo;

import java.util.List;

public interface FeignService {
    LockTo getLockInfo(String bid);

    List<String> getFailNameByDid(String did);
}