/*
 Navicat Premium Data Transfer

 Target Server Type    : MySQL
 Target Server Version : 80028 (8.0.28)
 File Encoding         : 65001

 Date: 30/10/2023 16:25:18
*/
CREATE database if NOT EXISTS `init_db` default character set utf8mb4 collate utf8mb4_unicode_ci;
use
    `init_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `user_id`       bigint                                                        NOT NULL COMMENT '用户ID',
    `user_account`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '用户账号',
    `user_password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '用户密码',
    `user_name`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '用户昵称',
    `user_avatar`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '用户头像',
    `user_role`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '用户角色（admin/user）',
    `create_time`   timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`    tinyint                                                       NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user`
VALUES (1709543990343753729, 'admin', '3042d7230c565a47d72a21d39759cb3d', NULL, NULL, 'admin', '2023-10-04 20:20:24',
        '2023-10-04 12:20:34', 0);

SET FOREIGN_KEY_CHECKS = 1;
