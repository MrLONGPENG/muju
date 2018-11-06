package com.lveqia.cloud.common.objeck.to;


import lombok.Data;

/**
 * Service Transfer Object
 * 业务信息传输对象
 */
@Data
public class InfoTo {
    private String did;
    private String bid;
    private String aid;
    private String hid;
    private String oid;
    private String bed;
    private String hospital;
    private String department;
    private String address;
}
