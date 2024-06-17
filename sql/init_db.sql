CREATE database if NOT EXISTS `init_db` default character set utf8mb4 collate utf8mb4_general_ci;
use
    `init_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS `t_log`;
CREATE TABLE `t_log`
(
    `log_id`             bigint                                                         NOT NULL COMMENT '日志ID',
    `log_uri`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '日志接口URI',
    `log_description`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '日志操作描述',
    `log_operator`       int                                                            NULL     DEFAULT NULL COMMENT '日志操作类型（0其他1增2删3查4改5导入6导出）',
    `log_request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '日志请求方法（RESTFul风格）',
    `log_method`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '日志方法名称',
    `log_user_id`        bigint                                                         NULL     DEFAULT NULL COMMENT '日志操作用户ID',
    `log_ip`             varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '日志操作用户IP',
    `log_location`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '日志操作用户地点',
    `log_param`          varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '日志操作参数',
    `log_result`         int                                                            NULL     DEFAULT NULL COMMENT '日志操作结果（0正常1异常）',
    `log_json`           varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '日志响应内容',
    `log_time`           bigint                                                         NULL     DEFAULT NULL COMMENT '日志接口访问耗时',
    `create_time`        timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`         tinyint                                                        NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
    PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_log
-- ----------------------------

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
    `user_state`    tinyint                                                       NOT NULL COMMENT '用户状态（0表示启用，1表示禁用）',
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
VALUES (1900, 'admin', 'FBWzA2jdoO9tjpiK3rKTbw==', 'AntonyCheng', NULL, 'admin', 0, '2024-03-27 22:01:16',
        '2024-06-07 00:05:18', 0);
INSERT INTO `t_user`
VALUES (1901, 'user', 'FBWzA2jdoO9tjpiK3rKTbw==', 'AntonyCoding', NULL, 'user', 0, '2024-03-27 22:01:17',
        '2024-06-07 00:05:24', 0);
INSERT INTO `t_user`
VALUES (1902, 'user1', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony1', NULL, 'user', 0, '2024-04-01 23:40:55',
        '2024-06-06 11:39:37', 0);
INSERT INTO `t_user`
VALUES (1903, 'user2', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony2', NULL, 'user', 0, '2024-04-01 23:40:55',
        '2024-05-09 23:47:01', 0);
INSERT INTO `t_user`
VALUES (1904, 'user3', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony3', NULL, 'user', 0, '2024-04-01 23:40:55',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1905, 'user4', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony4', NULL, 'user', 0, '2024-04-01 23:40:55',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1906, 'user5', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony5', NULL, 'user', 0, '2024-04-01 23:40:55',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1907, 'user6', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony6', NULL, 'user', 0, '2024-04-01 23:40:55',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1908, 'user7', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony7', NULL, 'user', 0, '2024-04-01 23:40:55',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1909, 'user8', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony8', NULL, 'user', 0, '2024-04-01 23:40:55',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1910, 'user9', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony9', NULL, 'user', 0, '2024-04-01 23:40:55',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775113809426817025, 'user10', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony10', NULL, 'user', 0, '2024-04-02 18:51:27',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775115510955286530, 'user11', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony11', NULL, 'user', 0, '2024-04-02 18:58:13',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775135430363455490, 'user12', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony12', NULL, 'user', 0, '2024-04-02 20:17:22',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775144682826113025, 'user13', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony13', NULL, 'user', 0, '2024-04-02 20:54:08',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775145271509262337, 'user14', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony14', NULL, 'user', 0, '2024-04-02 20:56:28',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775146054472572929, 'user15', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony15', NULL, 'user', 0, '2024-04-02 20:59:35',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775146105840214017, 'user16', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony16', NULL, 'user', 0, '2024-04-02 20:59:47',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775146158390648834, 'user17', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony17', NULL, 'user', 0, '2024-04-02 20:59:59',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775146210676842498, 'user18', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony18', NULL, 'user', 0, '2024-04-02 21:00:12',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775146297222111233, 'user19', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony19', NULL, 'user', 0, '2024-04-02 21:00:33',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775365730125459458, 'user20', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony20', NULL, 'user', 0, '2024-04-03 11:32:29',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775365807405510658, 'user21', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony21', NULL, 'user', 0, '2024-04-03 11:32:48',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775366194225197057, 'user22', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony22', NULL, 'user', 0, '2024-04-03 11:34:20',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775366255906631682, 'user23', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony23', NULL, 'user', 0, '2024-04-03 11:34:35',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775366338987405314, 'user24', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony24', NULL, 'user', 0, '2024-04-03 11:34:55',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775366388358557698, 'user25', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony25', NULL, 'user', 0, '2024-04-03 11:35:06',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775366594307272705, 'user26', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony26', NULL, 'user', 0, '2024-04-03 11:35:55',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775366755221745665, 'user27', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony27', NULL, 'user', 0, '2024-04-03 11:36:34',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775366808783007746, 'user28', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony28', NULL, 'user', 0, '2024-04-03 11:36:47',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775366952316284930, 'user29', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony29', NULL, 'user', 0, '2024-04-03 11:37:21',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1775393764199084034, 'user30', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony30', NULL, 'user', 0, '2024-04-03 13:23:53',
        '2024-05-07 00:00:35', 0);
INSERT INTO `t_user`
VALUES (1792074787028615169, 'user31', 'FBWzA2jdoO9tjpiK3rKTbw==', 'Antony31', NULL, 'user', 0, '2024-05-19 14:08:19',
        '2024-05-19 14:08:19', 0);

SET FOREIGN_KEY_CHECKS = 1;
