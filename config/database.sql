/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50716
Source Host           : localhost:3306
Source Database       : stockmonitorexpress

Target Server Type    : MYSQL
Target Server Version : 50716
File Encoding         : 65001

Date: 2017-09-11 21:09:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `config_global`
-- ----------------------------
DROP TABLE IF EXISTS `config_global`;
CREATE TABLE `config_global` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `value` text COMMENT '上一次交易日，用作启动时候清理表格',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of config_global
-- ----------------------------
INSERT INTO `config_global` VALUES ('1', '2017-09-11');

-- ----------------------------
-- Table structure for `order_book`
-- ----------------------------
DROP TABLE IF EXISTS `order_book`;
CREATE TABLE `order_book` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `plat_id` int(11) NOT NULL,
  `code` char(10) NOT NULL,
  `trade_flag` int(1) NOT NULL COMMENT '0-buy 1-sell',
  `order_stat` int(11) NOT NULL DEFAULT '0' COMMENT '0-未知 1-创建完成 2-已下单等待结果 3-下单成功 4-下单失败 5-已成交 6-部分成交 7-待撤单 8-正在请求撤单 9-已撤单 10-撤单失败',
  `order_price` float(11,2) NOT NULL,
  `order_count` int(11) NOT NULL,
  `plat_order_id` char(64) DEFAULT NULL,
  `deal_price` float(11,2) DEFAULT '0.00',
  `deal_count` int(11) DEFAULT '0',
  `datetime` char(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of order_book
-- ----------------------------
INSERT INTO `order_book` VALUES ('1', '1', '0', '600056', '1', '2', '24.28', '500', '', '0.00', '0', '20170908-200653');
INSERT INTO `order_book` VALUES ('2', '1', '0', '600056', '1', '2', '24.28', '500', '', '0.00', '0', '20170910-151531');
INSERT INTO `order_book` VALUES ('3', '1', '0', '600056', '1', '1', '24.28', '500', '', '0.00', '0', '20170911-210152');

-- ----------------------------
-- Table structure for `policy_step`
-- ----------------------------
DROP TABLE IF EXISTS `policy_step`;
CREATE TABLE `policy_step` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `plat_id` int(11) NOT NULL DEFAULT '0',
  `code` char(10) NOT NULL,
  `policy_stat` int(11) NOT NULL COMMENT '0-未开启\r\n1-待初始化\r\n2-区间执行\r\n3-已完成',
  `price_last` float(11,2) NOT NULL,
  `price_init` float(11,2) NOT NULL,
  `count_init` int(11) NOT NULL,
  `price_unit` float(11,2) NOT NULL,
  `step_unit` int(10) unsigned NOT NULL,
  `buy_offset` float(11,2) NOT NULL,
  `sell_offset` float(11,2) NOT NULL,
  `min_price` float(11,2) NOT NULL,
  `max_price` float(11,2) NOT NULL,
  `buyorder_id` char(64) DEFAULT NULL,
  `sellorder_id` char(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of policy_step
-- ----------------------------
INSERT INTO `policy_step` VALUES ('1', '1', '1', '600056', '2', '23.00', '24.10', '3000', '1.20', '500', '-0.03', '0.08', '20.00', '30.00', '0', '3');

-- ----------------------------
-- Table structure for `trade_book`
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
  `order_price` float(11,2) NOT NULL,
  `deal_price` float(11,2) DEFAULT NULL,
  `count` int(10) unsigned NOT NULL,
  `counter_fee` float(11,2) unsigned NOT NULL,
  `transfer_fee` float(11,2) unsigned NOT NULL,
  `stamp_tax` float(11,2) unsigned NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of trade_book
-- ----------------------------

-- ----------------------------
-- Table structure for `userinfo`
-- ----------------------------
DROP TABLE IF EXISTS `userinfo`;
CREATE TABLE `userinfo` (
  `id` int(11) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `plat_id` int(10) unsigned NOT NULL,
  `stat` int(10) unsigned NOT NULL DEFAULT '1',
  `plat_acct` tinytext NOT NULL,
  `plat_psw` tinytext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of userinfo
-- ----------------------------
INSERT INTO `userinfo` VALUES ('00000000001', '2', '0', '540600166072', '123456');

-- ----------------------------
-- Procedure structure for `ClearStragegyOrderId`
-- ----------------------------
DROP PROCEDURE IF EXISTS `ClearStragegyOrderId`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `ClearStragegyOrderId`()
BEGIN
	#Routine body goes here...
	DECLARE lastdate Date;
	SELECT date_pre into lastdate from global_config where id = 1;
	SELECT lastdate;
END
;;
DELIMITER ;
