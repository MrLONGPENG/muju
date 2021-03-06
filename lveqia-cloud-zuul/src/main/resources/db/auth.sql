﻿DROP TABLE IF EXISTS `t_sys_user_role`;
-- ----------------------------
-- Table structure for t_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user`;
CREATE TABLE `t_sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'UID',
  `name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `phone` varchar(16) DEFAULT NULL UNIQUE COMMENT '手机号码',
  `email` varchar(32) DEFAULT NULL UNIQUE COMMENT '电子邮箱',
  `address` varchar(64) DEFAULT NULL COMMENT '联系地址',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `username` varchar(64) DEFAULT NULL UNIQUE COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '加盐密码',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `crt_id` int(11) DEFAULT NULL COMMENT '创建者',
  `crt_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user
-- ----------------------------
INSERT INTO `t_sys_user` VALUES ('1', '系统管理员', '18521308791', 'admin@muju.com', '上海浦东金桥', '1', 'admin', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', 'http://cdn.duitang.com/uploads/item/201508/30/20150830105732_nZCLV.jpeg', null, null, now() );
INSERT INTO `t_sys_user` VALUES ('2', '木巨开发者', '18508429187', 'developer@muju.com', '上海浦东张江', '1', 'developer', '$2a$10$GGTGc.50tOA6VsHUstz9EeVD2WDnH68g3IHJIPLrb12.5B4tSurny', null, null, null, now());



DROP TABLE IF EXISTS `t_sys_menu_role`;
-- ----------------------------
-- Table structure for `t_sys_menu`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_menu`;
CREATE TABLE `t_sys_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(64) DEFAULT NULL,
  `path` varchar(64) DEFAULT NULL,
  `component` varchar(64) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `iconCls` varchar(64) DEFAULT NULL,
  `keepAlive` tinyint(1) DEFAULT NULL,
  `requireAuth` tinyint(1) DEFAULT NULL,
  `parentId` int(11) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT '1',
  `sort` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `parentId` (`parentId`),
  CONSTRAINT `menu_ibfk_1` FOREIGN KEY (`parentId`) REFERENCES `t_sys_menu` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_menu
-- ----------------------------
INSERT INTO `t_sys_menu` VALUES ('1', '/', '/home', 'Home', '数据概览', 'fa fa-dashboard fa-fw fa-lg', null, '0', null, '1', '0');
INSERT INTO `t_sys_menu` VALUES ('2', '/', '/home', 'Home', '订单管理', 'fa fa-th-list fa-fw fa-lg', null, '0', null, '1', '0');
INSERT INTO `t_sys_menu` VALUES ('3', '/', '/home', 'Home', '系统管理', 'fa fa-cog fa-fw fa-lg', null, '0', null, '1', '0');
INSERT INTO `t_sys_menu` VALUES ('4', '/', '/home', 'Home', '运维管理', 'fa fa-cogs fa-fw fa-lg', null, '0', null, '1', '0');
INSERT INTO `t_sys_menu` VALUES ('5', '/', '/home', 'Home', '设备管理', 'fa fa-cubes fa-fw fa-lg', null, '0', null, '1', '0');
INSERT INTO `t_sys_menu` VALUES ('6', '/', '/home', 'Home', '信息配置', 'fa fa-hospital-o fa-fw fa-lg', null, '0', null, '1', '0');
INSERT INTO `t_sys_menu` VALUES ('7', '/', '/home', 'Home', '业务配置', 'fa fa-tasks fa-fw fa-lg', null, '0', null, '1', '0');
INSERT INTO `t_sys_menu` VALUES ('8', '/', '/home', 'Home', '预留管理2', 'fa fa-user-circle-o', null, '0', null, '1', '0');
INSERT INTO `t_sys_menu` VALUES ('9', '/', '/home', 'Home', '预留管理3', 'fa fa-user-circle-o', null, '0', null, '1', '0');
INSERT INTO `t_sys_menu` VALUES ('10', '/data/overview/*', '/data/usage', 'DataUsage', '使用数据', 'fa fa-bar-chart fa-fw', null, '1', '1', '1', '0');
-- 预留十个一次菜单（其实没有作用，后面照样可以加一次菜单，只是完美主义作怪）
INSERT INTO `t_sys_menu` VALUES ('11', '/data/overview*', '/data/profit', 'DataProfit', '收益数据', 'fa fa-line-chart fa-fw', null, '1', '1', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('12', '/data/statistics/table', '/data/export', 'DataExport', '数据下载', 'fa fa-download fa-fw', null, '1', '1', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('13', '/data/order/list', '/order/list', 'OrderList', '订单统计', 'fa fa-moon-o fa-fw', null, '0', '2', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('14', '/', '/order/list/mid', 'OrderList', '订单统计-午休', 'fa fa-sun-o fa-fw', null, '1', '13', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('15', '/sys/*', '/sys/user', 'SysUser', '系统账号', 'fa fa-user-circle-o fa-fw', null, '1', '3', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('16', '/sys/*', '/sys/role', 'SysRole', '系统角色', 'fa fa-users fa-fw', null, '1', '3', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('17', '/sys/*', '/sys/menu', 'SysMenu', '系统菜单', 'fa fa-tasks fa-fw', null, '1', '3', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('18', '/sys/*', '/sys/data', 'SysData', '运营账号', 'fa fa-user-md fa-fw', null, '1', '3', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('19', '/rule/*', '/rule/goods', 'RuleGoods', '商品配置', 'fa fa-cart-plus fa-fw', null, '1', '7', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('20', '/rule/*', '/rule/uptime', 'RuleUptime', '时间配置', 'fa fa-clock-o fa-fw', null, '1', '7', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('21', '/device/*', '/device/list', 'DeviceList', '木巨柜管理', 'fa fa-cubes fa-fw', null, '1', '5', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('22', '/info/*', '/info/hospital', 'InfoHospital', '医院管理', 'fa fa-hospital-o fa-fw', null, '1', '6', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('23', '/info/*', '/info/department', 'InfoDepartment', '科室管理', 'fa fa-hospital-o fa-fw', null, '1', '6', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('24', '/device/*', '/device/lock', 'DeviceLock', '锁设备管理', 'fa fa-cubes fa-fw', null, '1', '5', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('25', '/device/*', '/device/state', 'DeviceState', '柜状态记录', 'fa fa-cubes fa-fw', null, '1', '5', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('26', '/device/*', '/device/fault', 'DeviceFault', '柜故障记录', 'fa fa-cubes fa-fw', null, '1', '5', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('27', '/device/*', '/device/switch', 'DeviceSwitch', '开关锁记录', 'fa fa-cubes fa-fw', null, '1', '5', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('28', '/wx/order/refund', '/order/list/refund', 'OrderList', '订单统计-退款', 'fa fa-cubes fa-fw', null, '1', '13', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('29', '/wx/audit/list', '/order/deposit', 'OrderDeposit', '押金管理', 'fa fa-money fa-fw', null, '1', '2', '1', '0');
INSERT INTO `t_sys_menu` VALUES ('30', '/data/order/list', '/order/list', 'OrderList', '订单统计-查询', 'fa fa-moon-o fa-fw', null, '1', '13', '1', '0');

-- ----------------------------
-- Table structure for t_sys_role
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role`;
CREATE TABLE `t_sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL COMMENT '角色英文名',
  `desc` varchar(64) DEFAULT NULL COMMENT '角色中文名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_role
-- ----------------------------
INSERT INTO `t_sys_role` VALUES ('1', 'ROLE_admin', '系统管理员');
INSERT INTO `t_sys_role` VALUES ('2', 'ROLE_developer', '木巨开发者');
INSERT INTO `t_sys_role` VALUES ('3', 'ROLE_manager', '木巨管理');
INSERT INTO `t_sys_role` VALUES ('4', 'ROLE_captain', '运营主管');
INSERT INTO `t_sys_role` VALUES ('5', 'ROLE_operate', '普通运营');
INSERT INTO `t_sys_role` VALUES ('6', 'ROLE_readers', '普通账号');


-- ----------------------------
-- Table structure for t_sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_role`;
CREATE TABLE `t_sys_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) DEFAULT NULL,
  `rid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `rid` (`rid`),
  KEY `user_role_ibfk_1` (`uid`),
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `t_sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`rid`) REFERENCES `t_sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ----------------------------
-- Records of t_sys_user_role
-- ----------------------------
INSERT INTO `t_sys_user_role` VALUES ('1', '1', '1');
INSERT INTO `t_sys_user_role` VALUES ('2', '2', '2');
INSERT INTO `t_sys_user_role` VALUES ('3', '2', '3');
INSERT INTO `t_sys_user_role` VALUES ('4', '2', '4');




-- ----------------------------
-- Table structure for t_sys_menu_role
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_menu_role`;
CREATE TABLE `t_sys_menu_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mid` int(11) DEFAULT NULL,
  `rid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mid` (`mid`),
  KEY `rid` (`rid`),
  CONSTRAINT `menu_role_ibfk_1` FOREIGN KEY (`mid`) REFERENCES `t_sys_menu` (`id`),
  CONSTRAINT `menu_role_ibfk_2` FOREIGN KEY (`rid`) REFERENCES `t_sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Records of t_sys_menu_role
-- ----------------------------
INSERT INTO `t_sys_menu_role` VALUES (null, '10', '2');
INSERT INTO `t_sys_menu_role` VALUES (null, '11', '2');
INSERT INTO `t_sys_menu_role` VALUES (null, '12', '2');
INSERT INTO `t_sys_menu_role` VALUES (null, '13', '2');
INSERT INTO `t_sys_menu_role` VALUES (null, '14', '2');
INSERT INTO `t_sys_menu_role` VALUES (null, '15', '3');
INSERT INTO `t_sys_menu_role` VALUES (null, '16', '3');
INSERT INTO `t_sys_menu_role` VALUES (null, '17', '3');
INSERT INTO `t_sys_menu_role` VALUES (null, '18', '3');


