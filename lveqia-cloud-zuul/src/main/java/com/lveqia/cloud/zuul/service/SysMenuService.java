package com.lveqia.cloud.zuul.service;

import com.lveqia.cloud.common.exception.BaseException;
import com.lveqia.cloud.zuul.model.SysMenu;
import com.lveqia.cloud.zuul.objeck.vo.AddMenuVo;
import com.lveqia.cloud.zuul.objeck.vo.MenuVo;
import com.lveqia.cloud.zuul.objeck.vo.ModifyMenuVo;

import java.util.List;

/**
 * @author leolaurel
 */
public interface SysMenuService {

    List<SysMenu> getAllMenuByLength();

    List<MenuVo> getMenusByUserId(Long id);

    boolean addMenu(long uid, AddMenuVo addVo) throws BaseException;

    boolean modifyMenu(long uid, ModifyMenuVo menuVo) throws BaseException;

    boolean modifyStatus(int id, long uid) throws BaseException;
}
