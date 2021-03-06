package com.mujugroup.lock.service.task;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lveqia.cloud.common.config.Constant;
import com.lveqia.cloud.common.objeck.to.InfoTo;
import com.mujugroup.lock.model.*;
import com.mujugroup.lock.service.*;
import com.mujugroup.lock.service.feign.ModuleCoreService;
import com.mujugroup.lock.service.feign.ModuleWxService;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ReceiveTask {
    private final LockDidService lockDidService;
    private final LockInfoService lockInfoService;
    private final LockFailService lockFailService;
    private final LockSwitchService lockSwitchService;
    private final LockRecordService lockRecordService;

    private final ModuleWxService moduleWxService;
    private final ModuleCoreService moduleCoreService;

    private BoundMapperFacade<LockInfo, LockRecord> mapperFacade;
    private final Logger logger = LoggerFactory.getLogger(ReceiveTask.class);
    @Autowired
    public ReceiveTask(MapperFactory mapperFactory, LockDidService lockDidService, LockInfoService lockInfoService
            , LockFailService lockFailService, LockSwitchService lockSwitchService, LockRecordService lockRecordService
            , ModuleWxService moduleWxService, ModuleCoreService moduleCoreService) {
        this.lockDidService = lockDidService;
        this.lockInfoService = lockInfoService;
        this.lockFailService = lockFailService;
        this.lockSwitchService = lockSwitchService;
        this.lockRecordService = lockRecordService;
        this.moduleWxService = moduleWxService;
        this.moduleCoreService = moduleCoreService;
        mapperFactory.classMap(LockInfo.class, LockRecord.class).exclude("id").byDefault().register();
        this.mapperFacade =  mapperFactory.getMapperFacade(LockInfo.class, LockRecord.class);
    }


    @Async(value = "taskExecutor")
    public void doReceiveData(String info) {
        long t1 = 0,t2 = 0,t3;
        try {
            t1 = System.currentTimeMillis();
            JsonObject json = new JsonParser().parse(info).getAsJsonObject();
            if(!json.has("result")) return;
            LockInfo lockInfo = getLockInfo(json.getAsJsonObject("result"));
            LockDid lockDid = lockDidService.getLockDidByBid(String.valueOf(lockInfo.getLockId()));
            t2 = System.currentTimeMillis();
            switch (json.get("msgType").getAsInt()) {
                case 200:
                    updateLockInfo(lockInfo);
                    if(lockDid == null)  return;
                    insertLockSwitch(lockDid.getDid(), lockInfo);
                    insertLockOffLineFail(lockDid.getDid(), lockInfo);//记录离线数据
                    moduleWxService.usingNotify(lockDid.getDid(), lockInfo.getLockStatus());
                    break;//开关锁
                case 201:
                    break;//定位信息上报
                case 400:
                    if(lockDid == null)  return;
                    insertSwitchLockFail(lockDid.getDid(), lockInfo);// 记录开关锁机械异常
                    break;//故障信息上报
                case 401:
                    break;//其他信息上报
                case 1000:
                    updateLockInfo(lockInfo);
                    if(lockDid == null)  return;
                    insertLockRecord(lockDid.getDid(), lockInfo);
                    break;//普通上报消息（普通锁）
                case 2000:
                    break;//普通上报消息（助力锁）
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            t3 = System.currentTimeMillis();
            logger.debug("时间段1:{}  时间段2:{} 总时间：{}", t2 - t1, t3 - t2, t3 - t1);
        }
    }

    private LockInfo getLockInfo(JsonObject info) {
        LockInfo lockInfo = new LockInfo();
        lockInfo.setBrand(1); // 设置品牌连旅
        if (info.has("id")) {
            lockInfo.setLockId(info.get("id").getAsLong());
        }
        if (info.has("mac")) {
            lockInfo.setMac(info.get("mac").getAsString());
        }
        if (info.has("key")) {
            lockInfo.setKey(info.get("key").getAsString());
        }
        if (info.has("firmwareVersion")) {
            lockInfo.setFVersion(info.get("firmwareVersion").getAsInt());
        }
        if (info.has("hdwareVer")) {
            lockInfo.setHVersion(info.get("hdwareVer").getAsInt());
        }
        if (info.has("longitude")) {
            lockInfo.setLongitude(info.get("longitude").getAsBigDecimal());
        }
        if (info.has("latitude")) {
            lockInfo.setLatitude(info.get("latitude").getAsBigDecimal());
        }
        if (info.has("csq")) {
            lockInfo.setCsq(info.get("csq").getAsInt());
        }
        if (info.has("temp")) {
            lockInfo.setTemp(info.get("temp").getAsInt());
        }
        if (info.has("vbus")) {
            lockInfo.setCharge(info.get("vbus").getAsInt());
        }
        if (info.has("vbattery")) {
            lockInfo.setVoltage(info.get("vbattery").getAsInt());
        }
        if (info.has("iCharge")) {
            lockInfo.setElectric(info.get("iCharge").getAsInt());
        }
        if (info.has("upgradeFlag")) {
            lockInfo.setUpgrade(info.get("upgradeFlag").getAsInt());
        }
        if (info.has("batteryStat")) {
            lockInfo.setBatteryStat(info.get("batteryStat").getAsInt());
        }
        if (info.has("lockStatus")) {
            lockInfo.setLockStatus(info.get("lockStatus").getAsInt());
        }
        long lastRefresh;
        Date date = new Date();
        if (info.has("lastRefresh") && Math.abs((lastRefresh = info.get("lastRefresh").getAsLong())
                - date.getTime())< Constant.TIMESTAMP_HOUR_HALF*1000) {
            lockInfo.setLastRefresh(new Date(lastRefresh));
        }else {
            lockInfo.setLastRefresh(date);
        }
        //获取400状态下的fault属性
        if (info.has("fault")) {
            //开锁机械故障
            if (info.get("fault").getAsInt() == 4) {
                lockInfo.setLockStatus(4);
            }
            if (info.get("fault").getAsInt() == 5) {
                //关锁机械故障
                lockInfo.setLockStatus(5);
            }
        }
        //获取200状态下的online属性
        if (info.has("online")) {
            if (info.get("online").getAsInt() == 0) {
                lockInfo.setCsq(-1);
            }
        }
        return lockInfo;
    }



    private void updateLockInfo(LockInfo info) {
        LockInfo lockInfo = lockInfoService.getLockInfoByBid(String.valueOf(info.getLockId()));
        if (lockInfo == null) {
            lockInfoService.insert(info);
        } else {
            info.setId(lockInfo.getId());
            lockInfoService.update(info);
        }
    }

    private void insertLockSwitch(long did, LockInfo lockInfo) {
        LockSwitch lockSwitch = new LockSwitch();
        lockSwitch.setLockId(lockInfo.getLockId());   //设置bid(锁编号)
        lockSwitch.setDid(did);        //设置did
        lockSwitch.setLocalTime(new Date());         //保存到服务器的时间
        lockSwitch.setReceiveTime(lockInfo.getLastRefresh() == null ? new Date() : lockInfo.getLastRefresh());
        lockSwitch.setLockStatus(lockInfo.getLockStatus() == null ? 0 : lockInfo.getLockStatus());
        lockSwitchService.add(lockSwitch);
    }


    private void insertLockRecord(long did, LockInfo lockInfo) {
        LockRecord lockRecord = mapperFacade.map(lockInfo);
        lockRecord.setDid(did);
        lockRecord.setCrtTime(new Date());         //设置创建时间
        lockRecordService.add(lockRecord);
    }

    /**
     * 开关锁机械故障
     *
     */
    private void insertSwitchLockFail(long did, LockInfo lockInfo) {
        InfoTo infoTo = moduleCoreService.getDeviceInfo(String.valueOf(did), "");
        if (infoTo != null) {
            //开锁机械故障
            if (lockInfo.getLockStatus().equals(4)) {
                LockFail lockFailOpen = lockFailService.getFailInfoByDid(infoTo.getDid(),LockFail.ErrorType.TYPE_SWITCH_OPEN);
                if (lockFailOpen == null) {
                    lockFailOpen = new LockFail();
                    lockFailService.getModel(lockFailOpen, LockFail.ErrorType.TYPE_SWITCH_OPEN, infoTo, null);
                    lockFailService.insert(lockFailOpen);
                } else if (isChange(infoTo, lockFailOpen)) {
                    lockFailService.modifyModel(lockFailOpen, infoTo.getAid(), infoTo.getHid(), infoTo.getOid(), new Date());
                }
            }
            //关锁机械故障
            if (lockInfo.getLockStatus().equals(5)) {
                LockFail lockFailClose = lockFailService.getFailInfoByDid(infoTo.getDid(), LockFail.ErrorType.TYPE_SWITCH_CLOSE);
                if (lockFailClose == null) {
                    lockFailClose = new LockFail();
                    lockFailService.getModel(lockFailClose, LockFail.ErrorType.TYPE_SWITCH_CLOSE, infoTo, null);
                    lockFailService.insert(lockFailClose);
                } else if (isChange(infoTo, lockFailClose)) {
                    lockFailService.modifyModel(lockFailClose, infoTo.getAid(), infoTo.getHid(), infoTo.getOid(), new Date());
                }
            }
        }
    }

    private void insertLockOffLineFail(long did, LockInfo lockInfo) {
            //当前设备无信号
           if (lockInfo.getCsq()!= null && lockInfo.getCsq()== -1) {
               InfoTo infoTo = moduleCoreService.getDeviceInfo(String.valueOf(did), "");
               if (infoTo != null) {
               LockFail lockFailOffline = lockFailService.getFailInfoByDid(infoTo.getDid(), LockFail.ErrorType.TYPE_SIGNAL_OFFLINE);
               if (lockFailOffline == null) {
                   lockFailOffline = new LockFail();
                   lockFailService.getModel(lockFailOffline, LockFail.ErrorType.TYPE_SIGNAL_OFFLINE, infoTo, null);
                   lockFailService.insert(lockFailOffline);
               } else if (isChange(infoTo, lockFailOffline)) {
                   lockFailService.modifyModel(lockFailOffline, infoTo.getAid(), infoTo.getHid(), infoTo.getOid(), new Date());
               }}
            }

    }


    /**
     * 代理商/医院/科室 关系改变
     */
    private boolean isChange(InfoTo info, LockFail fail) {
        return !info.getAid().equals(String.valueOf(fail.getAid()))
                || !info.getHid().equals(String.valueOf(fail.getHid()))
                || !info.getOid().equals(String.valueOf(fail.getOid()));
    }
}
