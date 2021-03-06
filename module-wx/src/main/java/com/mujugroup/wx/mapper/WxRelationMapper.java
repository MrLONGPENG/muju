package com.mujugroup.wx.mapper;

import com.mujugroup.wx.model.WxRelation;
import com.mujugroup.wx.sql.WxRelationSqlProvider;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 扩展关系数据表,数据库操作接口类
 * 类名:WxRelationMapper
 * @author leolaurel
 * 创建时间:20180712
 */
@Mapper
@Component(value ="wxRelationMapper")
public interface WxRelationMapper {

    @InsertProvider(type = WxRelationSqlProvider.class, method = "insert")
    boolean insert(WxRelation wxRelation);

    @UpdateProvider(type = WxRelationSqlProvider.class, method = "update")
    boolean update(WxRelation wxRelation);

    @Delete("delete from t_wx_relation where id= #{id}")
    boolean deleteById(int id);

    @Select("SELECT * FROM t_wx_relation WHERE id = #{id}")
    @Results(id = "wxRelation", value = {
         @Result(id=true, column="id",property="id",javaType=Integer.class)
             ,@Result(column="rid",property="rid",javaType=Integer.class)
             ,@Result(column="kid",property="kid",javaType=Integer.class)
             ,@Result(column="key",property="key",javaType=Integer.class)
             ,@Result(column="type",property="type",javaType=Integer.class)
    })
    WxRelation findById(Integer id);

    @Select("SELECT * FROM t_wx_relation")
    @ResultMap("wxRelation")
    List<WxRelation> findListAll();


    /**
     * 根据类型删除指定的外键ID(单个)
     */
    @Delete("delete from t_wx_relation where `type`= #{type} AND `rid`= #{rid}")
    boolean deleteByRid(@Param(value="type")int type, @Param(value="rid")int rid);

    /**
     * 根据类型删除指定外键类型的数据(一个或多个)
     */
    @Delete("delete from t_wx_relation where `type`= #{type} AND `key`= #{key} AND `kid`= #{kid}")
    boolean deleteByType(@Param(value="type")int type, @Param(value="key")int key, @Param(value="kid")int kid);
}