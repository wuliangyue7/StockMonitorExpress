/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50716
Source Host           : localhost:3306
Source Database       : stockmonitorexpress

Target Server Type    : MYSQL
Target Server Version : 50716
File Encoding         : 65001

Date: 2017-09-17 20:45:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `stragegy_order`
-- ----------------------------
DROP TABLE IF EXISTS `stragegy_order`;
CREATE TABLE `stragegy_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `plat_id` int(11) NOT NULL,
  `code` char(10) NOT NULL,
  `stragegy_stat` int(4) NOT NULL DEFAULT '0' COMMENT '0-关闭 1-打开',
  `trade_flag` int(4) NOT NULL DEFAULT '0' COMMENT '0-buy 1-sell',
  `price_trade` float(11,2) NOT NULL,
  `count_trade` int(11) NOT NULL,
  `order_id` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of stragegy_order
-- ----------------------------
INSERT INTO `stragegy_order` VALUES ('3', '1', '1', '000963', '1', '0', '43.01', '1400', '0');
INSERT INTO `stragegy_order` VALUES ('4', '1', '1', '002146', '1', '0', '10.31', '1200', '0');
INSERT INTO `stragegy_order` VALUES ('5', '1', '1', '002146', '1', '1', '11.14', '1100', '4');
INSERT INTO `stragegy_order` VALUES ('6', '1', '1', '002317', '1', '0', '11.87', '2300', '0');
