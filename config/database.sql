/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50716
Source Host           : 127.0.0.1:3306
Source Database       : stockmonitor

Target Server Type    : MYSQL
Target Server Version : 50716
File Encoding         : 65001

Date: 2017-09-07 15:39:05
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for order_book
-- ----------------------------
DROP TABLE IF EXISTS `order_book`;
CREATE TABLE `order_book` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `plat_id` int(11) NOT NULL,
  `code` char(10) NOT NULL,
  `trade_flag` int(1) NOT NULL COMMENT '0-buy 1-sell',
  `order_stat` int(11) NOT NULL DEFAULT '0' COMMENT '0-未知 1-创建完成 2-已下单等待结果 3-下单成功 4-下单失败 5-订单查状态询中 6-已成交 7-部分成交 8-待撤单 9-正在请求撤单 10-已撤单 11-撤单失败 12-已经提交撤单等待结果',
  `order_price` float(10,0) NOT NULL,
  `order_count` int(11) NOT NULL,
  `plat_order_id` char(64) DEFAULT NULL,
  `deal_price` float(10,2) DEFAULT '0.00',
  `deal_count` int(11) DEFAULT '0',
  `datetime` char(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for policy_step
-- ----------------------------
DROP TABLE IF EXISTS `policy_step`;
CREATE TABLE `policy_step` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `code` char(6) NOT NULL,
  `policy_stat` int(11) NOT NULL COMMENT '0-未开启\r\n1-待初始化\r\n2-区间执行\r\n3-已完成',
  `price_last` float NOT NULL,
  `price_init` float NOT NULL,
  `count_init` int(11) NOT NULL,
  `price_unit` float NOT NULL,
  `step_unit` int(10) unsigned NOT NULL,
  `buy_offset` float NOT NULL,
  `sell_offset` float NOT NULL,
  `min_price` float NOT NULL,
  `max_price` float NOT NULL,
  `buyorder_id` char(64) DEFAULT NULL,
  `buylast_price` float DEFAULT NULL,
  `buyorder_date` char(8) DEFAULT NULL,
  `sellorder_id` char(64) DEFAULT NULL,
  `selllast_price` float DEFAULT NULL,
  `sellorder_date` char(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for trade_book
-- ----------------------------
DROP TABLE IF EXISTS `trade_book`;
CREATE TABLE `trade_book` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` char(32) NOT NULL,
  `plat_id` int(11) NOT NULL,
  `plat_order_id` char(64) DEFAULT NULL,
  `stat` int(10) unsigned NOT NULL,
  `code` char(10) NOT NULL,
  `trade_flag` int(1) unsigned NOT NULL COMMENT '0-buy 1-sell',
  `order_price` float NOT NULL,
  `deal_price` float DEFAULT NULL,
  `count` int(10) unsigned NOT NULL,
  `counter_fee` float unsigned NOT NULL,
  `transfer_fee` float unsigned NOT NULL,
  `stamp_tax` float unsigned NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=311 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for userinfo
-- ----------------------------
DROP TABLE IF EXISTS `userinfo`;
CREATE TABLE `userinfo` (
  `id` int(11) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `plat_id` int(10) unsigned NOT NULL,
  `stat` int(10) unsigned NOT NULL DEFAULT '1',
  `plat_acct` tinytext NOT NULL,
  `plat_psw` tinytext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
