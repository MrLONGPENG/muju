package com.mujugroup.lock.controller;

import com.lveqia.cloud.common.util.ResultUtil;
import com.lveqia.cloud.common.util.StringUtil;
import com.mujugroup.lock.model.LockDid;
import com.mujugroup.lock.service.LockDidService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/did")
@Api(description="锁模块锁ID关系接口")
public class LockDidController {

    private final LockDidService lockDidService;

    private final Logger logger = LoggerFactory.getLogger(LockDidController.class);
    @Autowired
    public LockDidController(LockDidService lockDidService) {
        this.lockDidService = lockDidService;
    }

    @RequestMapping(value = "/getDeviceId", method = RequestMethod.GET)
    @ApiOperation(value="锁模块查询接口", notes="根据业务DID查询")
    public String getDeviceId(@ApiParam(value = "业务ID") String did){
        logger.debug("getDeviceId:{}", did);
        return query(did, null);
    }


    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ApiOperation(value="锁模块查询接口", notes="根据业务DID查询")
    public String query(@ApiParam(value = "业务ID") String did, @ApiParam(value = "锁ID") String bid){
        logger.debug("query did:{} bid:{}", did ,bid);
        if(did == null && bid ==null) return ResultUtil.error(ResultUtil.CODE_PARAMETER_MISS);
        LockDid lockDid;
        if(did!=null){
            if(did.length()>9) return ResultUtil.error(ResultUtil.CODE_REQUEST_FORMAT);
            lockDid = lockDidService.getLockDidByDid(StringUtil.autoFillDid(did));
        }else {
            lockDid = lockDidService.getLockDidByBid(bid);
        }
        if(lockDid == null) return ResultUtil.error(ResultUtil.CODE_NOT_FIND_DATA);
        return ResultUtil.success(lockDid);
    }


    @RequestMapping(value = "/delete")
    public String delete(String did){
        logger.debug("delete:{}", did);
        if(!StringUtil.isNumeric(did)) return ResultUtil.error(ResultUtil.CODE_REQUEST_FORMAT);
        LockDid lockDid = lockDidService.getLockDidByDid(StringUtil.autoFillDid(did));

        if(lockDid!=null){
            lockDidService.deleteByDid(StringUtil.autoFillDid(lockDid.getDid()));
            lockDidService.deleteByBid(String.valueOf(lockDid.getLockId()));
            return ResultUtil.success();
        }else{
            return ResultUtil.error(ResultUtil.CODE_NOT_FIND_DATA, "无数据可删除");
        }
    }



    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public String onImport(@RequestParam("file")MultipartFile file
            , @RequestParam(name="brand", required=false, defaultValue="1")int brand
            , @RequestParam(name="didCell", required=false, defaultValue="1")int didCell
            , @RequestParam(name="bidCell", required=false, defaultValue="3")int bidCell
            , @RequestParam(name="isHex", required=false, defaultValue="false")boolean isHex) {
        List<LockDid> list;
        try {
            list = lockDidService.readExcel(file, file.getOriginalFilename(), brand, didCell, bidCell, isHex);
        } catch (Exception e) {
            return ResultUtil.error(ResultUtil.CODE_REQUEST_FORMAT, "Excel文件格式错误");
        }
        return onBatchInsert(list);
    }


    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    public String onBatch(@RequestParam(name="did") String did, @RequestParam(name="bid") String bid
            , @RequestParam(name="brand", required=false, defaultValue="1")int brand
            , @RequestParam(name="count", required=false, defaultValue="1")int count
            , @RequestParam(name="isHex", required=false, defaultValue="false")boolean isHex) {
        return onBatchInsert(lockDidService.onBatch(did, bid, brand, count, isHex));
    }


    private String onBatchInsert(List<LockDid> list) {
        try {
            if(lockDidService.batchInsert(list)){
                return ResultUtil.success();
            }else {
                return ResultUtil.error(ResultUtil.CODE_DB_STORAGE_FAIL, "数据为空");
            }
        } catch (Exception e) {
            return ResultUtil.error(ResultUtil.CODE_DB_STORAGE_FAIL, "事务回滚");
        }
    }


}
