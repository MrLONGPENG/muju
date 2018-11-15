package com.mujugroup.lock.service.impl;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lveqia.cloud.common.objeck.to.InfoTo;
import com.mujugroup.lock.model.*;
import com.mujugroup.lock.service.*;
import com.mujugroup.lock.service.feign.ModuleCoreService;
import com.mujugroup.lock.service.feign.ModuleWxService;
import com.mujugroup.lock.service.task.FailTask;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final LockInfoService lockInfoService;
    private final ModuleWxService moduleWxService;
    private final LockSwitchService lockSwitchService;
    private final LockDidService lockDidService;
    private final LockRecordService lockRecordService;
    private final MapperFactory mapperFactory;
    private final LockFailService lockFailService;
    private final ModuleCoreService moduleCoreService;

    @Autowired
    public ConsumerServiceImpl(LockInfoService lockInfoService, ModuleWxService moduleWxService
            , LockSwitchService lockSwitchService, LockDidService lockDidService
            , LockRecordService lockRecordService, MapperFactory mapperFactory, LockFailService lockFailService, ModuleCoreService moduleCoreService) {
        this.lockInfoService = lockInfoService;
        this.moduleWxService = moduleWxService;
        this.lockSwitchService = lockSwitchService;
        this.lockDidService = lockDidService;
        this.lockRecordService = lockRecordService;
        this.mapperFactory = mapperFactory;
        this.lockFailService = lockFailService;
        this.moduleCoreService = moduleCoreService;
    }

    @Override
    @JmsListener(destination = "record")
    public String getInfo(String info) {
        try {
            JsonObject json = new JsonParser().parse(info).getAsJsonObject();
            LockInfo lockInfo = getLockInfo(json.getAsJsonObject("result"));
            LockDid lockDid = lockDidService.getLockDidByBid(String.valueOf(lockInfo.getLockId()));

            switch (json.get("msgType").getAsInt()) {
                case 200:
                    switchLock(lockInfo, true);
                    if(lockDid == null)  return null;
                    insertLockSwitch(lockDid.getDid(), lockInfo);
                    insertLockRecord(lockDid.getDid(), lockInfo);
                    insertLockOffLineFail(lockDid.getDid(), lockInfo);//记录离线数据
                    break;//开关锁
                case 201:
                    break;//定位信息上报
                case 400:
                    if(lockDid == null)  return null;
                    insertSwitchLockFail(lockDid.getDid(), lockInfo);// 记录开关锁机械异常
                    break;//故障信息上报
                case 401:
                    break;//其他信息上报
                case 1000:
                    switchLock(lockInfo, false);
                    if(lockDid == null)  return null;
                    insertLockRecord(lockDid.getDid(), lockInfo);
                    break;//普通上报消息（普通锁）
                case 2000:
                    break;//普通上报消息（助力锁）
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        if (info.has("lastRefresh")) {
            lockInfo.setLastRefresh(new Date(info.get("lastRefresh").getAsLong()));
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

    private LockInfo formatLockInfo(LockInfo info, LockInfo lockInfo) {
        ClassMapBuilder<?, LockInfo> temp = mapperFactory.classMap(LockInfo.class, LockInfo.class);
        temp.mapNulls(false); // NULL值不映射
        temp.byDefault().register();
        mapperFactory.getMapperFacade().map(info, lockInfo);
        return lockInfo;
    }


    private void switchLock(LockInfo info, boolean isNotify) {
        LockInfo lockInfo = lockInfoService.getLockInfoByBid(String.valueOf(info.getLockId()));
        if (lockInfo == null) {
            lockInfoService.insert(formatLockInfo(info, lockInfo = new LockInfo()));
        } else {
            lockInfoService.update(formatLockInfo(info, lockInfo));
        }
        if (isNotify) {
            moduleWxService.usingNotify(String.valueOf(lockInfo.getLockId()), String.valueOf(lockInfo.getLockStatus()));
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
        mapperFactory.classMap(LockInfo.class, LockRecord.class).byDefault().register();
        LockRecord lockRecord = mapperFactory.getMapperFacade().map(lockInfo, LockRecord.class);
        lockRecord.setDid(did);
        lockRecord.setCrtTime(new Date());         //设置创建时间
        lockRecordService.add(lockRecord);
    }

    /**
     * 开关锁机械故障
     *
     * @param did
     * @param lockInfo
     */
    public void insertSwitchLockFail(long did, LockInfo lockInfo) {
        InfoTo infoTo = moduleCoreService.getDeviceInfo(String.valueOf(did), "");
        if (infoTo != null) {
            //校验时间差是否超过30分钟
            if (new Date().getTime() - lockInfo.getLastRefresh().getTime() < FailTask.TIME_SPAN) {
                //开锁机械故障
                if (lockInfo.getLockStatus().equals(4)) {
                    LockFail lockFailOpen = lockFailService.getFailInfoByDid(infoTo.getDid(), LockFail.FailType.TYPE_SWITCH,LockFail.FE_SW_OPEN);
                    if (lockFailOpen == null) {
                        lockFailOpen = new LockFail();
                        lockFailService.getModel(lockFailOpen, Integer.parseInt(infoTo.getAid()), Integer.parseInt(infoTo.getHid())
                                , Integer.parseInt(infoTo.getOid()), Long.parseLong(infoTo.getDid()), LockFail.FailType.TYPE_SWITCH
                                , LockFail.FE_SW_OPEN, lockInfo.getLastRefresh(), Long.parseLong(infoTo.getBid()));
                        lockFailService.insert(lockFailOpen);
                    } else if (!infoTo.getAid().equals(lockFailOpen.getAid()) || !infoTo.getHid().equals(lockFailOpen.getHid())
                            || !infoTo.getOid().equals(lockFailOpen.getOid())) {
                        lockFailService.modifyModel(lockFailOpen, infoTo.getAid(), infoTo.getHid(), infoTo.getOid(), new Date());
                    }
                }
            }
            //关锁机械故障
            if (lockInfo.getLockStatus().equals(5)) {
                LockFail lockFailClose = lockFailService.getFailInfoByDid(infoTo.getDid(), LockFail.FailType.TYPE_SWITCH, LockFail.FE_SW_CLOSE);
                if (lockFailClose == null) {
                    lockFailClose = new LockFail();
                    lockFailService.getModel(lockFailClose, Integer.parseInt(infoTo.getAid()), Integer.parseInt(infoTo.getHid())
                            , Integer.parseInt(infoTo.getOid()), Long.parseLong(infoTo.getDid()), LockFail.FailType.TYPE_SWITCH
                            , LockFail.FE_SW_CLOSE, lockInfo.getLastRefresh(), Long.parseLong(infoTo.getBid()));
                    lockFailService.insert(lockFailClose);
                } else if (!infoTo.getAid().equals(lockFailClose.getAid()) || !infoTo.getHid().equals(lockFailClose.getHid())
                        || !infoTo.getOid().equals(lockFailClose.getOid())) {
                    lockFailService.modifyModel(lockFailClose, infoTo.getAid(), infoTo.getHid(), infoTo.getOid(), new Date());
                }
            }
        }
    }

    private void insertLockOffLineFail(long did, LockInfo lockInfo) {
        InfoTo infoTo = moduleCoreService.getDeviceInfo(String.valueOf(did), "");
        if (infoTo != null) {
            //校验时间差
            if (new Date().getTime() - lockInfo.getLastRefresh().getTime() < FailTask.TIME_SPAN) {
                //当前设备无信号
                if (lockInfo.getCsq().equals(-1)) {
                    LockFail lockFailOffline = lockFailService.getFailInfoByDid(infoTo.getDid(), LockFail.FailType.TYPE_SIGNAL, LockFail.FE_SG_OFFLINE);
                    if (lockFailOffline == null) {
                        lockFailOffline = new LockFail();
                        lockFailService.getModel(lockFailOffline, Integer.parseInt(infoTo.getAid()), Integer.parseInt(infoTo.getHid())
                                , Integer.parseInt(infoTo.getOid()), Long.parseLong(infoTo.getDid()),  LockFail.FailType.TYPE_SIGNAL
                                , LockFail.FE_SG_OFFLINE, lockInfo.getLastRefresh(), Long.parseLong(infoTo.getBid()));
                        lockFailService.insert(lockFailOffline);
                    } else if (!infoTo.getAid().equals(lockFailOffline.getAid()) || !infoTo.getHid().equals(lockFailOffline.getHid())
                            || !infoTo.getOid().equals(lockFailOffline.getOid())) {
                        lockFailService.modifyModel(lockFailOffline, infoTo.getAid(), infoTo.getHid(), infoTo.getOid(), new Date());
                    }
                }
            }
        }
    }

}
