package com.lveqia.cloud.zuul.sql;

import com.lveqia.cloud.zuul.model.SysUser;
import org.apache.ibatis.jdbc.SQL;

/**
 * ,SQL语句组装类
 * 类名:SysUserSqlProvider
 * @author leolaurel
 * 创建时间:20180725
 */
public class SysUserSqlProvider {

    public String insert(SysUser sysUser){
        return new SQL(){{
            INSERT_INTO("t_sys_user");
            if(sysUser.getId()!= null) VALUES("`id`", "#{id}");
            if(sysUser.getName()!= null) VALUES("`name`", "#{name}");
            if(sysUser.getPhone()!= null) VALUES("`phone`", "#{phone}");
            if(sysUser.getTelephone()!= null) VALUES("`telephone`", "#{telephone}");
            if(sysUser.getAddress()!= null) VALUES("`address`", "#{address}");
            if(sysUser.isEnabled()) VALUES("`enabled`", "#{enabled}");
            if(sysUser.getUsername()!= null) VALUES("`username`", "#{username}");
            if(sysUser.getPassword()!= null) VALUES("`password`", "#{password}");
            if(sysUser.getAvatarUrl()!= null) VALUES("`avatar_url`", "#{avatarUrl}");
            if(sysUser.getRemark()!= null) VALUES("`remark`", "#{remark}");
        }}.toString();
    }



    public String update(SysUser sysUser){
        return new SQL(){{
            UPDATE("t_sys_user");
            if(sysUser.getId()!= null) SET("`id` = #{id}");
            if(sysUser.getName()!= null) SET("`name` = #{name}");
            if(sysUser.getPhone()!= null) SET("`phone` = #{phone}");
            if(sysUser.getTelephone()!= null) SET("`telephone` = #{telephone}");
            if(sysUser.getAddress()!= null) SET("`address` = #{address}");
            if(sysUser.isEnabled()) SET("`enabled` = #{enabled}");
            if(sysUser.getUsername()!= null) SET("`username` = #{username}");
            if(sysUser.getPassword()!= null) SET("`password` = #{password}");
            if(sysUser.getAvatarUrl()!= null) SET("`avatar_url` = #{avatarUrl}");
            if(sysUser.getRemark()!= null) SET("`remark` = #{remark}");
            WHERE("id = #{id}");
        }}.toString();
    }
}