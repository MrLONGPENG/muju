package com.lveqia.cloud.zuul.sql;

import com.lveqia.cloud.zuul.model.SysMenuRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

/**
 * ,SQL语句组装类
 * 类名:SysMenuRoleSqlProvider
 * @author leolaurel
 * 创建时间:20180725
 */
public class SysMenuRoleSqlProvider {

    public String insert(SysMenuRole sysMenuRole){
        return new SQL(){{
            INSERT_INTO("t_sys_menu_role");
            if(sysMenuRole.getId()!= null) VALUES("`id`", "#{id}");
            if(sysMenuRole.getMid()!= null) VALUES("`mid`", "#{mid}");
            if(sysMenuRole.getRid()!= null) VALUES("`rid`", "#{rid}");
        }}.toString();
    }



    public String update(SysMenuRole sysMenuRole){
        return new SQL(){{
            UPDATE("t_sys_menu_role");
            if(sysMenuRole.getId()!= null) SET("`id` = #{id}");
            if(sysMenuRole.getMid()!= null) SET("`mid` = #{mid}");
            if(sysMenuRole.getRid()!= null) SET("`rid` = #{rid}");
            WHERE("id = #{id}");
        }}.toString();
    }

    @SuppressWarnings("unused")
    public String addMidRid(@Param("rid")int rid, @Param("ids") int[] ids){
        return new SQL(){{
            INSERT_INTO("t_sys_menu_role");
            INTO_COLUMNS("`mid`","`rid`");
            StringBuffer sb = new StringBuffer();
            for (int mid:ids) {
                if(sb.length() != 0) sb.append("), (");
                sb.append(mid).append(", ").append(rid);
            }
            INTO_VALUES(new String(sb));
        }}.toString();
    }

}
