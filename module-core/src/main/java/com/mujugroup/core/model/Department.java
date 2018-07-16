package com.mujugroup.core.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 科室表
 * 类名:Department
 * 创建人:LEOLAUREL
 * 创建时间:20180627
 */
@SuppressWarnings("serial")
@Table(name = "t_department")
public class Department implements Serializable {

    /**
     * 主键
     * 
     * 表字段 : t_department.id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * 显示状态：1显示;0删除;
     * 表字段 : t_department.status
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 医院ID
     * 表字段 : t_department.hospital_id
     */
    @Column(name = "hospital_id")
    private Integer hospitalId;

    /**
     * 科室名称
     * 表字段 : t_department.name
     */
    @Column(name = "name")
    private String name;

    /**
     * 爱汇科室Id 对应t_aihui_department表
     * 表字段 : t_department.aihui_depart_id
     */
    @Column(name = "aihui_depart_id")
    private Integer aihuiDepartId;

    /**
     * 科室描述
     * 表字段 : t_department.remark
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 排序
     * 表字段 : t_department.sort
     */
    @Column(name = "sort")
    private Integer sort;

    /**
     * 创建时间
     * 表字段 : t_department.create_date
     */
    @Column(name = "create_date")
    private Date createDate;



    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

    public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public Integer getAihuiDepartId() {
		return aihuiDepartId;
	}

	public void setAihuiDepartId(Integer aihuiDepartId) {
		this.aihuiDepartId = aihuiDepartId;
	}

    public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

    public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

    public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}