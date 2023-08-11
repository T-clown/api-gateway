/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : localhost:3306
 Source Schema         : spring-boot-demo

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 03/01/2020 10:54:01
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `springboot_order`;
CREATE TABLE `springboot_order`
(
    `id`          bigint(11) NOT NULL COMMENT "主键",
    `order_no`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
    `user_id`     bigint(11) NOT NULL COMMENT "用户id",
    `product_id`  bigint(11) NOT NULL COMMENT '商品id',
    `count`       int(11) NOT NULL DEFAULT 0 COMMENT '商品数量',
    `amount`      decimal(10, 2) NOT NULL DEFAULT '0.00' COMMENT '订单金额',
    `status`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
    `create_time` datetime(0) NOT NULL DEFAULT NOW(),
    `update_time` datetime(0) NOT NULL DEFAULT NOW(),
    `is_deleted`  smallint(5) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


