﻿-- ----------------------------
-- Table structure for t_wx_user
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_user`;
CREATE TABLE `t_wx_user` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`phone` varchar(16) DEFAULT NULL COMMENT '用户绑定手机号',
`open_id` varchar(32) DEFAULT NULL COMMENT '微信对外唯一ID',
`union_id` varchar(32) DEFAULT NULL COMMENT '微信业务唯一ID',
`nick_name` varchar(32) DEFAULT NULL COMMENT '微信昵称',
`gender` tinyint(4) DEFAULT NULL COMMENT '用户性别 1 男 2 女',
`language` varchar(16) DEFAULT 'zh_CN' COMMENT '用户语言',
`country` varchar(32) DEFAULT 'China' COMMENT '用户国家',
`province` varchar(32) DEFAULT NULL COMMENT '用户省份',
`city` varchar(64) DEFAULT NULL COMMENT '用户城市',
`avatar_url` varchar(255) DEFAULT NULL COMMENT '微信头像地址',
`session_key` varchar(32) DEFAULT NULL COMMENT '微信临时凭证',
`crtTime` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
UNIQUE KEY `index_id` (`open_id`, `union_id`) COMMENT '唯一业务ID索引',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='微信用户基础信息表';



-- ----------------------------
-- Table structure for t_wx_using
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_using`;
CREATE TABLE `t_wx_using` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`did` bigint(20) DEFAULT NULL COMMENT '唯一业务ID',
`open_id` varchar(32) DEFAULT NULL COMMENT '支付者',
`pay_cost` int(11) DEFAULT NULL COMMENT '支付金额',
`pay_time` bigint(20) DEFAULT NULL COMMENT '支付时10位时间戳',
`end_time` bigint(20) DEFAULT NULL COMMENT '结束时10位时间戳',
`unlock_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '开锁时间',
`using` tinyint(1) default 0 COMMENT '是否使用中',
`deleted` tinyint(1) default 0 COMMENT '软删除标记',
INDEX `index_did` (`did`) COMMENT '按设备索引',
INDEX `index_user` (`open_id`) COMMENT '按用戶索引',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='使用支付记录表';


-- ----------------------------
-- Table structure for t_wx_order
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_order`;
CREATE TABLE `t_wx_order` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`did` bigint(20) DEFAULT NULL COMMENT '唯一业务ID',
`aid` int(11) DEFAULT NULL COMMENT '代理ID',
`hid` int(11) DEFAULT NULL COMMENT '医院ID',
`oid` int(11) DEFAULT NULL COMMENT '科室ID',
`gid` int(11) DEFAULT NULL COMMENT '商品ID',
`open_id` varchar(128) DEFAULT NULL COMMENT '微信对外唯一ID',
`trade_no` varchar(32) DEFAULT NULL COMMENT '内部订单号，如20180626123456',
`transaction_id` varchar(32) DEFAULT NULL COMMENT '微信订单号，如20180626123456',
`order_type` tinyint(4) DEFAULT NULL COMMENT '支付类型 1:晚休 2:午休',
`pay_price` int(11) DEFAULT NULL COMMENT '实际支付价格',
`pay_status`  tinyint(4) DEFAULT NULL COMMENT '实际支付状态 1.统一下单 2.支付完成 4.已退款',
`pay_time` bigint(20) DEFAULT NULL COMMENT '支付时10位时间戳',
`end_time` bigint(20) DEFAULT NULL COMMENT '结束时10位时间戳',
`crtTime` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
INDEX `index_union` (`aid`,`hid`,`oid`) COMMENT '订单索引',
INDEX `index_order` (`trade_no`) COMMENT '订单索引',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单信息表';
-- ----------------------------
-- Table structure for t_wx_repair
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_repair`;
CREATE TABLE `t_wx_repair` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`did` bigint(20) DEFAULT NULL COMMENT '唯一业务ID',
`open_id` varchar(128) DEFAULT NULL COMMENT '微信对外唯一ID',
`fault_cause` varchar(128) DEFAULT NULL COMMENT '损坏的部位',
`fault_describe` text DEFAULT NULL COMMENT '损坏的描述',
`restorer` varchar(128) DEFAULT NULL COMMENT '修复人信息',
`repair_status` tinyint(4) DEFAULT NULL COMMENT '报修状态 1.待修复 2.修复中 3.修复完',
`crtTime` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`updTime` datetime DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='保修信息表';


-- ----------------------------
-- Table structure for t_wx_images
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_images`;
CREATE TABLE `t_wx_images` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`pid` int(11) DEFAULT NULL COMMENT '图片类型主键',
`type` tinyint(4) DEFAULT NULL COMMENT '图片类型 1.保修图片 2.待定',
`image_url` varchar(255) DEFAULT NULL COMMENT '图片地址',
`crtTime` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='保修图片表';


-- ----------------------------
-- Table structure for t_wx_goods
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_goods`;
CREATE TABLE `t_wx_goods` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(32) DEFAULT NULL COMMENT '商品套餐名字',
`price` int(11) DEFAULT NULL COMMENT '商品套餐价格',
`fee_type` varchar(16) DEFAULT 'CNY' COMMENT '标价币种',
`days`  smallint(6) DEFAULT NULL COMMENT '商品套餐的使用天数',
`type`  tinyint(4) DEFAULT NULL COMMENT '商品类型(1:押金；2:套餐；3:午休 4:被子；)',
`state` tinyint(4) DEFAULT NULL COMMENT '套餐状态(1:可用；2:禁用；-1:删除)',
`explain` varchar(64) DEFAULT NULL COMMENT '此属性使用说明',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品套餐详情表';


INSERT INTO `t_wx_goods` VALUES (1, "限免", 0, "CNY", 0, 1, 1, "默认数据");
INSERT INTO `t_wx_goods` VALUES (2, "10元/天(限时五折)", 1, "CNY", 1, 2, 1, "默认数据");
INSERT INTO `t_wx_goods` VALUES (3, "午休壹元体验", 100, "CNY", 0, 3, 1, "默认数据");


-- ----------------------------
-- Table structure for t_wx_uptime
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_uptime`;
CREATE TABLE `t_wx_uptime` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`start_time` int(11) DEFAULT NULL COMMENT '开锁最早时间(秒,不含日期)',
`stop_time` int(11) DEFAULT NULL COMMENT '关锁最晚时间(秒,不含日期)',
`start_desc` varchar(16) DEFAULT NULL COMMENT '开锁最早时间描述',
`stop_desc` varchar(16) DEFAULT NULL COMMENT '关锁最晚时间描述',
`explain` varchar(64) DEFAULT NULL COMMENT '此属性使用说明',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='木巨柜运行时间表';


INSERT INTO `t_wx_uptime` VALUES (1, 55800, 21600, "15:30", "6:00", "默认数据");
INSERT INTO `t_wx_uptime` VALUES (2, 54000, 28800, "15:00", "8:00", "上海木巨医院");

-- ----------------------------
-- Table structure for t_wx_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_relation`;
CREATE TABLE `t_wx_relation` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`rid`  int(11) DEFAULT NULL COMMENT '关系ID {商品套餐ID 运行时间ID}',
`kid`  int(11) DEFAULT NULL COMMENT '外键ID {代理商ID、医院ID、科室ID、其他ID}',
`key`  tinyint(4) DEFAULT NULL COMMENT '外键类型 0:默认数据 1:代理商 2:医院 3:科室 4:其他',
`type`  tinyint(4) DEFAULT NULL COMMENT '关系类型 1:商品套餐 2:运行时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='扩展关系数据表';

INSERT INTO `t_wx_relation` VALUES (1, 1, 0, 0, 1);
INSERT INTO `t_wx_relation` VALUES (2, 2, 0, 0, 1);
INSERT INTO `t_wx_relation` VALUES (3, 3, 0, 0, 1);
INSERT INTO `t_wx_relation` VALUES (4, 1, 0, 0, 2);
INSERT INTO `t_wx_relation` VALUES (5, 2, 1, 2, 2);


-- ----------------------------
-- Table structure for t_wx_opinion
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_opinion`;
CREATE TABLE `t_wx_opinion`(
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`did` bigint(20) DEFAULT NULL COMMENT '唯一业务ID',
`open_id` varchar(128) DEFAULT NULL COMMENT '微信对外唯一ID',
`content` text DEFAULT NULL COMMENT '反馈的内容',
`reader` varchar(128) DEFAULT NULL COMMENT '阅读人信息',
`read_status` tinyint(4) DEFAULT NULL COMMENT '建议处理状态 1.待读阅 2.已查看 3.待采用 4.已收集',
`crtTime` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`updTime` datetime DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='意见反馈表';
-- ----------------------------
-- Table structure for t_wx_deposit(押金表)
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_deposit`;
CREATE TABLE `t_wx_deposit` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `gid` INT(11) DEFAULT NULL COMMENT '商品ID',
  `open_id` VARCHAR(128) DEFAULT NULL COMMENT '微信对外唯一ID',
  `trade_no` VARCHAR(32) DEFAULT NULL COMMENT '内部订单号，如20180626123456',
  `deposit` INT(11) DEFAULT NULL COMMENT '押金金额',
  `status` TINYINT(4) DEFAULT NULL COMMENT '押金状态 1.已支付  2.退款中 4.审核通过',
  `crtTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updTime` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='押金表';
-- ----------------------------
-- Table structure for t_wx_record_main(支付记录主表)
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_record_main`;
CREATE TABLE `t_wx_record_main` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `did` BIGINT(20) DEFAULT NULL COMMENT '唯一业务ID',
  `aid` INT(11) DEFAULT NULL COMMENT '代理商ID',
  `hid` INT(11) DEFAULT NULL COMMENT '医院ID',
  `oid` INT(11) DEFAULT NULL COMMENT '科室ID',
  `open_id` VARCHAR(128) DEFAULT NULL COMMENT '微信对外唯一ID',
  `trade_no` VARCHAR(32) DEFAULT NULL COMMENT '内部订单号，如20180626123456',
  `transaction_id` VARCHAR(32) DEFAULT NULL COMMENT '微信订单号，如20180626123456',
  `total_price` INT(11) DEFAULT NULL COMMENT '支付总金额',
  `refund_count` INT(11) DEFAULT NULL COMMENT '退款次数',
  `refund_price` INT(11) DEFAULT NULL COMMENT '退款总金额',
  `pay_status` TINYINT(4) DEFAULT NULL COMMENT '实际支付状态 1.统一下单 2.支付完成 3.支付异常',
  `crtTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `index_union` (`aid`,`hid`,`oid`) USING BTREE,
  KEY `index_payRecord` (`trade_no`) USING BTREE
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='支付记录主表';
-- ----------------------------
-- Table structure for t_wx_record_assist(支付记录辅表)
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_record_assist`;
CREATE TABLE `t_wx_record_assist` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `mid` BIGINT(20) NOT NULL COMMENT '支付主表ID',
  `gid` INT(11) DEFAULT NULL COMMENT '商品ID',
  `price` INT(11) DEFAULT NULL COMMENT '金额',
  `crtTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `type` INT(11) DEFAULT NULL COMMENT '类型：1:押金 2:套餐 3:午休 4:被子',
  PRIMARY KEY (`id`),
  KEY `fk_recordMain` (`mid`),
  CONSTRAINT `fk_recordMain` FOREIGN KEY (`mid`) REFERENCES `t_wx_record_main` (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='支付记录辅表'


-- ----------------------------
-- Table structure for t_wx_refund_record(退款记录表)
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_refund_record`;
CREATE TABLE `t_wx_refund_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `open_id` VARCHAR(128) DEFAULT NULL COMMENT '微信对外唯一ID',
  `trade_no` VARCHAR(32) DEFAULT NULL COMMENT '内部订单号，如20180626123456',
  `refund_no`  VARCHAR(32) DEFAULT NULL COMMENT '退款订单号，如201806261234561',
  `refund_count` INT(11) DEFAULT NULL COMMENT '退款次数',
  `refundDesc` VARCHAR(200) DEFAULT NULL COMMENT '退款原因',
  `refund_price` INT(11) DEFAULT NULL COMMENT '退款金额',
  `total_price` INT(11) DEFAULT NULL COMMENT '总金额',
  `refund_type` TINYINT(4) DEFAULT NULL COMMENT '退款类型 1.押金退款 2.订单退款',
  `refund_status` TINYINT(4) DEFAULT NULL COMMENT '退款状态 1.退款失败 2.退款成功',
  `crtTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户申请退款时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='退款记录表';
-- ----------------------------
-- Table structure for t_wx_deduction_record(扣费记录表)
-- ----------------------------
DROP TABLE IF EXISTS `t_wx_deduction_record`;
CREATE TABLE `t_wx_deduction_record`(
   `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
   `open_id` VARCHAR(32) DEFAULT NULL COMMENT '微信对外唯一ID',
   `trade_no`VARCHAR(32) DEFAULT NULL COMMENT '内部订单号，如20180626123456',
   `did` BIGINT(20) DEFAULT NULL COMMENT '业务ID',
   `explain`VARCHAR(50) DEFAULT NULL COMMENT '扣费原因',
   `day` DATE DEFAULT NULL COMMENT '扣费记录产生日期',
   `forfeit` INT(11) DEFAULT NULL COMMENT '扣费金额',
   `timeout` INT(11) DEFAULT NULL COMMENT '超时时长',
   `type`TINYINT(4) DEFAULT NULL COMMENT '扣费类型 1:超时扣费 2:其他',
   `crtTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   UNIQUE KEY `index_key` (`open_id`,`did`,`day`) COMMENT '唯一索引',
   PRIMARY KEY (`id`)
   ) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='扣费记录表';


