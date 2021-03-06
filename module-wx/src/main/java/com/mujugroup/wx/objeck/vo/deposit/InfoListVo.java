package com.mujugroup.wx.objeck.vo.deposit;

import com.mujugroup.wx.model.WxOrder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class InfoListVo implements Serializable {

    private Long id;
    private Integer gid;

    //微信对外唯一ID
    private String openId;

    //内部订单号
    private String tradeNo;

    //押金金额
    private Integer deposit;

    // 押金状态 1.已支付  2.退款中 4.审核通过
    private Integer status;
    //押金创建时间
    private Date crtTime;
    //押金状态更新时间
    private Date updTime;
    //用户手机号
    private String phone;
    //微信昵称
    private String nickName;
    //用户性别 1 男 2 女
    private Integer gender;
    //用户语言
    private String language;
    //用户国家
    private String country;
    //用户省份
    private String province;
    //用户城市
    private String city;
    //微信临时凭证
    private String sessionKey;
    //订单数据
    private WxOrder wxOrder;
}
