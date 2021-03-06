package com.mujugroup.wx.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.io.Serializable;
import javax.persistence.*;

/**
 * 使用支付记录表
 * 类名:WxUsing
 * 创建人:LEOLAUREL
 * 创建时间:20180710
 */

@Data
@SuppressWarnings("serial")
@Table(name = "t_wx_using")
@EqualsAndHashCode(callSuper = true)
public class WxUsing extends WxBase {

    /**
     * 主键
     * 
     * 表字段 : t_wx_using.id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * 唯一业务ID
     * 表字段 : t_wx_using.did
     */
    @Column(name = "did")
    private Long did;


    /**
     * 支付者
     * 表字段 : t_wx_using.open_id
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 支付金额
     * 表字段 : t_wx_using.pay_cost
     */
    @Column(name = "pay_cost")
    private Integer payCost;

    /**
     * 支付时10位时间戳
     * 表字段 : t_wx_using.pay_time
     */
    @Column(name = "pay_time")
    private Long payTime;

    /**
     * 结束时10位时间戳
     * 表字段 : t_wx_using.end_time
     */
    @Column(name = "end_time")
    private Long endTime;

    /**
     * 开锁时间
     * 表字段 : t_wx_using.unlock_time
     */
    @Column(name = "unlock_time")
    private Date unlockTime;

    /**
     * 是否使用中
     * 表字段 : t_wx_using.using
     */
    @Column(name = "using")
    private Boolean using;

    /**
     * 软删除标记
     * 表字段 : t_wx_using.deleted
     */
    @Column(name = "deleted")
    private Boolean deleted;


}