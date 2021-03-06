package com.mujugroup.data.objeck.bo.sta;

import com.github.wxiaoqi.merge.annonation.MergeField;
import com.lveqia.cloud.common.util.StringUtil;
import com.lveqia.cloud.common.config.Constant;
import com.mujugroup.data.service.feign.ModuleWxService;

import java.io.Serializable;

/**
 * 图表-收益统计接口
 */
public class StaProfit implements Serializable {

    private String refDate;

    @MergeField(feign = ModuleWxService.class, method = "getTotalProfit"
            , isValueNeedMerge = true, defaultValue = Constant.DIGIT_ZERO)
    private String profit;


    public StaProfit(String refDate, String[] ids) {
        this.refDate = refDate;
        this.profit = StringUtil.toLinkByAnd(ids[0], ids[1], ids[2], 0, 0, refDate);
    }

    public String getRefDate() {
        return refDate;
    }

    public void setRefDate(String refDate) {
        this.refDate = refDate;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }
}
