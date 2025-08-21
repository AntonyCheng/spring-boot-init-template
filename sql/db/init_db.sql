CREATE database if NOT EXISTS `init_db` default character set utf8mb4 collate utf8mb4_general_ci;
use
    `init_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_file
-- ----------------------------
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file`
(
    `file_id`            bigint UNSIGNED                                               NOT NULL COMMENT '文件ID',
    `file_unique_key`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件唯一摘要值',
    `file_name`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件存储名称',
    `file_original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件原名称',
    `file_suffix`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件扩展名',
    `file_size`          bigint                                                        NOT NULL COMMENT '文件大小',
    `file_url`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件地址',
    `file_oss_type`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '文件OSS类型',
    `create_time`        datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`         tinyint                                                       NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
    PRIMARY KEY (`file_id`) USING BTREE,
    INDEX `idx_deleted_create` (`is_deleted` ASC, `create_time` ASC) USING BTREE,
    INDEX `idx_deleted_suffix_create` (`is_deleted` ASC, `file_suffix` ASC, `create_time` ASC) USING BTREE,
    INDEX `idx_deleted_suffix_type_create` (`is_deleted` ASC, `file_suffix` ASC, `file_oss_type` ASC, `create_time`
                                            ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS `t_log`;
CREATE TABLE `t_log`
(
    `log_id`             bigint UNSIGNED                                                NOT NULL COMMENT '日志ID',
    `log_uri`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '日志接口URI',
    `log_description`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '日志操作描述',
    `log_operator`       int                                                            NOT NULL COMMENT '日志操作类型（0其他1增2删3查4改5导入6导出）',
    `log_request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '日志请求方法（RESTFul风格）',
    `log_method`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '日志方法名称',
    `log_user_id`        bigint                                                         NOT NULL COMMENT '日志操作用户ID',
    `log_ip`             varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '日志操作用户IP',
    `log_location`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '日志操作用户地点',
    `log_param`          varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志操作参数',
    `log_result`         int                                                            NOT NULL COMMENT '日志操作结果（0正常1异常）',
    `log_json`           varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志响应内容',
    `log_time`           bigint                                                         NOT NULL COMMENT '日志接口访问耗时',
    `create_time`        datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`         tinyint UNSIGNED                                               NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
    PRIMARY KEY (`log_id`) USING BTREE,
    INDEX `idx_deleted_create` (`is_deleted` ASC, `create_time` ASC) USING BTREE,
    INDEX `idx_deleted_operator_create` (`is_deleted` ASC, `log_operator` ASC, `create_time` ASC) USING BTREE,
    INDEX `idx_deleted_operator_request_create` (`is_deleted` ASC, `log_operator` ASC, `log_request_method` ASC,
                                                 `create_time` ASC) USING BTREE,
    INDEX `idx_deleted_operator_request_result_create` (`is_deleted` ASC, `log_operator` ASC, `log_request_method` ASC,
                                                        `log_result` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `user_id`        bigint UNSIGNED                                               NOT NULL COMMENT '用户ID',
    `user_account`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '用户账号',
    `user_password`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '用户密码',
    `user_email`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户邮箱',
    `user_login_num` int                                                           NOT NULL DEFAULT 0 COMMENT '用户连续登录失败次数',
    `user_name`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '用户昵称',
    `user_avatar_id` bigint                                                        NOT NULL COMMENT '用户头像ID',
    `user_role`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '用户角色（admin/user）',
    `user_state`     tinyint                                                       NOT NULL DEFAULT 0 COMMENT '用户状态（0表示启用，1表示禁用）',
    `create_time`    datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     tinyint                                                       NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
    PRIMARY KEY (`user_id`) USING BTREE,
    INDEX `idx_deleted_create` (`is_deleted` ASC, `create_time` ASC) USING BTREE,
    INDEX `idx_deleted_role_create` (`is_deleted` ASC, `user_role` ASC, `create_time` ASC) USING BTREE,
    INDEX `idx_deleted_role_state_create` (`is_deleted` ASC, `user_role` ASC, `user_state` ASC, `create_time` ASC) USING BTREE,
    INDEX `idx_all` (`is_deleted` ASC, `user_id` ASC, `user_account` ASC, `user_password` ASC, `user_email` ASC,
                     `user_login_num` ASC, `user_name` ASC, `user_avatar_id` ASC, `user_role` ASC, `user_state` ASC,
                     `create_time` ASC, `update_time` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user`
VALUES (1900, 'admin', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'AntonyCheng', -1, 'admin', 0,
        '2024-03-27 22:01:16', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1901, 'user', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'AntonyCoding', -1, 'user', 0,
        '2024-03-27 22:01:17', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1902, 'user1', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony1', -1, 'user', 0,
        '2024-04-01 23:40:55', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1903, 'user2', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony2', -1, 'user', 0,
        '2024-04-01 23:40:55', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1904, 'user3', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony3', -1, 'user', 0,
        '2024-04-01 23:40:55', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1905, 'user4', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony4', -1, 'user', 0,
        '2024-04-01 23:40:55', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1906, 'user5', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony5', -1, 'user', 0,
        '2024-04-01 23:40:55', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1907, 'user6', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony6', -1, 'user', 0,
        '2024-04-01 23:40:55', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1908, 'user7', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony7', -1, 'user', 0,
        '2024-04-01 23:40:55', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1909, 'user8', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony8', -1, 'user', 0,
        '2024-04-01 23:40:55', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1910, 'user9', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony9', -1, 'user', 0,
        '2024-04-01 23:40:55', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775113809426817025, 'user10', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony10', -1, 'user', 0,
        '2024-04-02 18:51:27', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775115510955286530, 'user11', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony11', -1, 'user', 0,
        '2024-04-02 18:58:13', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775135430363455490, 'user12', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony12', -1, 'user', 0,
        '2024-04-02 20:17:22', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775144682826113025, 'user13', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony13', -1, 'user', 0,
        '2024-04-02 20:54:08', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775145271509262337, 'user14', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony14', -1, 'user', 0,
        '2024-04-02 20:56:28', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775146054472572929, 'user15', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony15', -1, 'user', 0,
        '2024-04-02 20:59:35', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775146105840214017, 'user16', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony16', -1, 'user', 0,
        '2024-04-02 20:59:47', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775146158390648834, 'user17', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony17', -1, 'user', 0,
        '2024-04-02 20:59:59', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775146210676842498, 'user18', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony18', -1, 'user', 0,
        '2024-04-02 21:00:12', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775146297222111233, 'user19', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony19', -1, 'user', 0,
        '2024-04-02 21:00:33', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775365730125459458, 'user20', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony20', -1, 'user', 0,
        '2024-04-03 11:32:29', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775365807405510658, 'user21', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony21', -1, 'user', 0,
        '2024-04-03 11:32:48', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775366194225197057, 'user22', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony22', -1, 'user', 0,
        '2024-04-03 11:34:20', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775366255906631682, 'user23', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony23', -1, 'user', 0,
        '2024-04-03 11:34:35', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775366338987405314, 'user24', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony24', -1, 'user', 0,
        '2024-04-03 11:34:55', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775366388358557698, 'user25', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony25', -1, 'user', 0,
        '2024-04-03 11:35:06', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775366594307272705, 'user26', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony26', -1, 'user', 0,
        '2024-04-03 11:35:55', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775366755221745665, 'user27', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony27', -1, 'user', 0,
        '2024-04-03 11:36:34', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775366808783007746, 'user28', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony28', -1, 'user', 0,
        '2024-04-03 11:36:47', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775366952316284930, 'user29', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony29', -1, 'user', 0,
        '2024-04-03 11:37:21', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1775393764199084034, 'user30', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony30', -1, 'user', 0,
        '2024-04-03 13:23:53', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1792074787028615169, 'user31', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony31', -1, 'user', 0,
        '2024-05-19 14:08:19', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1800929601992507394, 'user32', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony32', -1, 'user', 0,
        '2024-06-13 00:34:11', '2025-08-21 15:41:34', 0);
INSERT INTO `t_user`
VALUES (1832981147241373697, 'user33', 'xAXL594fo95Auh35w1kzIA==', '1911261716@qq.com', 0, 'Antony33', -1, 'user', 0,
        '2024-09-09 11:15:35', '2025-08-21 15:41:34', 0);

SET FOREIGN_KEY_CHECKS = 1;
