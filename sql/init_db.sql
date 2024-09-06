CREATE database if NOT EXISTS `init_db` default character set utf8mb4 collate utf8mb4_general_ci;
use
    `init_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_file
-- ----------------------------
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file`  (
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `file_unique_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件唯一摘要值',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件存储名称',
  `file_original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件原名称',
  `file_suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件扩展名',
  `file_size` bigint NOT NULL COMMENT '文件大小',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件地址',
  `file_oss_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件OSS类型',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS `t_log`;
CREATE TABLE `t_log`  (
  `log_id` bigint NOT NULL COMMENT '日志ID',
  `log_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志接口URI',
  `log_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志操作描述',
  `log_operator` int NOT NULL COMMENT '日志操作类型（0其他1增2删3查4改5导入6导出）',
  `log_request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志请求方法（RESTFul风格）',
  `log_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志方法名称',
  `log_user_id` bigint NULL DEFAULT NULL COMMENT '日志操作用户ID',
  `log_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志操作用户IP',
  `log_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志操作用户地点',
  `log_param` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志操作参数',
  `log_result` int NOT NULL COMMENT '日志操作结果（0正常1异常）',
  `log_json` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志响应内容',
  `log_time` bigint NOT NULL COMMENT '日志接口访问耗时',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_log
-- ----------------------------
INSERT INTO `t_log` VALUES (1800928335635660801, '/api/auth/login', '用户登录', 0, 'POST', 'top.sharehome.springbootinittemplate.controller.AuthController.login', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{\"account\":\"admin\"}', 0, '{\"msg\":\"登录成功\",\"code\":200,\"data\":{\"res\":{\"account\":\"admin\",\"id\":1900,\"name\":\"AntonyCheng\",\"role\":\"admin\"}}}', 8914, '2024-06-13 00:29:10', '2024-06-13 00:29:10', 0);
INSERT INTO `t_log` VALUES (1800928616662417409, '/api/auth/info', '用户获取个人信息', 3, 'GET', 'top.sharehome.springbootinittemplate.controller.AuthController.info', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{}', 0, '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"account\":\"admin\",\"id\":1900,\"name\":\"AntonyCheng\",\"role\":\"admin\"}}', 24, '2024-06-13 00:30:17', '2024-06-13 00:30:17', 0);
INSERT INTO `t_log` VALUES (1800928690914181121, '/api/user/update/account', '用户更新自身账号', 4, 'PUT', 'top.sharehome.springbootinittemplate.controller.user.UserController.updateAccount', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{\"newAccount\":\"admin\"}', 1, '{\"msg\":\"账号名称已经存在 ==> [不能和当前帐号重复]\",\"code\":11001,\"name\":\"USERNAME_ALREADY_EXISTS\"}', 6, '2024-06-13 00:30:34', '2024-06-13 00:30:34', 0);
INSERT INTO `t_log` VALUES (1800929054031855618, '/api/user/update/name', '用户更新自身名称', 4, 'PUT', 'top.sharehome.springbootinittemplate.controller.user.UserController.updateName', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{\"newName\":\"AntonyCheng\"}', 1, '{\"msg\":\"账号名称已经存在 ==> [不能和当前名称重复]\",\"code\":11001,\"name\":\"USERNAME_ALREADY_EXISTS\"}', 3, '2024-06-13 00:32:01', '2024-06-13 00:32:01', 0);
INSERT INTO `t_log` VALUES (1800929095920369666, '/api/user/update/avatar', '用户更新自身头像', 4, 'PUT', 'top.sharehome.springbootinittemplate.controller.user.UserController.updateAvatar', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{}', 1, '{\"exception\":\"java.lang.ExceptionInInitializerError\",\"method\":\"updateAvatar\",\"line\":285,\"class\":\"top.sharehome.springbootinittemplate.service.impl.UserServiceImpl\"}', 36, '2024-06-13 00:32:11', '2024-06-13 00:32:11', 0);
INSERT INTO `t_log` VALUES (1800929236089815042, '/api/admin/user/page', '管理员查询用户信息', 3, 'GET', 'top.sharehome.springbootinittemplate.controller.user.AdminUserController.pageUser', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{\"allowDeep\":\"\",\"role\":\"\",\"size\":\"\",\"name\":\"\",\"page\":\"\",\"state\":\"\",\"account\":\"\"}', 0, '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"current\":1,\"pages\":1,\"records\":[{\"account\":\"admin\",\"createTime\":\"2024-03-27 22:01:16\",\"id\":1900,\"name\":\"AntonyCheng\",\"role\":\"admin\",\"state\":0},{\"account\":\"user\",\"createTime\":\"2024-03-27 22:01:17\",\"id\":1901,\"name\":\"AntonyCoding\",\"role\":\"user\",\"state\":0},{\"account\":\"user1\",\"createTime\":\"2024-04-01 23:40:55\",\"id\":1902,\"name\":\"Antony1\",\"role\":\"user\",\"state\":0},{\"account\":\"user2\",\"createTime\":\"2024-04-01 23:40:55\",\"id\":1903,\"name\":\"Antony2\",\"role\":\"user\",\"state\":0},{\"account\":\"user3\",\"createTime\":\"2024-04-01 23:40:55\",\"id\":1904,\"name\":\"Antony3\",\"role\":\"user\",\"state\":0},{\"account\":\"user4\",\"createTime\":\"2024-04-01 23:40:55\",\"id\":1905,\"name\":\"Antony4\",\"role\":\"user\",\"state\":0},{\"account\":\"user5\",\"createTime\":\"2024-04-01 23:40:55\",\"id\":1906,\"name\":\"Antony5\",\"role\":\"user\",\"state\":0},{\"account\":\"user6\",\"createTime\":\"2024-04-01 23:40:55\",\"id\":1907,\"name\":\"Antony6\",\"role\":\"user\",\"state\":0},{\"account\":\"user7\",\"createTime\":\"2024-04-01 23:40:55\",\"id\":1908,\"name\":\"Antony7\",\"role\":\"user\",\"state\":0},{\"account\":\"user8\",\"createTime\":\"2024-04-01 23:40:55\",\"id\":1909,\"name\":\"Antony8\",\"role\":\"user\",\"state\":0},{\"account\":\"user9\",\"createTime\":\"2024-04-01 23:40:55\",\"id\":1910,\"name\":\"Antony9\",\"role\":\"user\",\"state\":0},{\"account\":\"user10\",\"createTime\":\"2024-04-02 18:51:27\",\"id\":1775113809426817025,\"name\":\"Antony10\",\"role\":\"user\",\"state\":0},{\"account\":\"user11\",\"createTime\":\"2024-04-02 18:58:13\",\"id\":1775115510955286530,\"name\":\"Antony11\",\"role\":\"user\",\"state\":0},{\"account\":\"user12\",\"createTime\":\"2024-04-02 20:17:22\",\"id\":1775135430363455490,\"name\":\"Antony12\",\"role\":\"user\",\"state\":0},{\"account\":\"user13\",\"createTime\":\"2024-04-02 20:54:08\",\"id\":1775144682826113025,\"name\":\"Antony13\",\"role\":\"user\",\"state\":0},{\"account\":\"user14\",\"createTime\":\"2024-04-02 20:56:28\",\"id\":1775145271509262337,\"name\":\"Antony14\",\"role\":\"user\",\"state\":0},{\"account\":\"user15\",\"createTime\":\"2024-04-02 20:59:35\",\"id\":1775146054472572929,\"name\":\"Antony15\",\"role\":\"user\",\"state\":0},{\"account\":\"user16\",\"...}', 200, '2024-06-13 00:32:44', '2024-06-13 00:32:44', 0);
INSERT INTO `t_log` VALUES (1800929447117832194, '/api/admin/user/page', '管理员查询用户信息', 3, 'GET', 'top.sharehome.springbootinittemplate.controller.user.AdminUserController.pageUser', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{\"allowDeep\":\"\",\"role\":\"\",\"size\":\"10\",\"name\":\"\",\"page\":\"2\",\"state\":\"\",\"account\":\"\"}', 0, '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"current\":2,\"pages\":4,\"records\":[{\"account\":\"user9\",\"createTime\":\"2024-04-01 23:40:55\",\"id\":1910,\"name\":\"Antony9\",\"role\":\"user\",\"state\":0},{\"account\":\"user10\",\"createTime\":\"2024-04-02 18:51:27\",\"id\":1775113809426817025,\"name\":\"Antony10\",\"role\":\"user\",\"state\":0},{\"account\":\"user11\",\"createTime\":\"2024-04-02 18:58:13\",\"id\":1775115510955286530,\"name\":\"Antony11\",\"role\":\"user\",\"state\":0},{\"account\":\"user12\",\"createTime\":\"2024-04-02 20:17:22\",\"id\":1775135430363455490,\"name\":\"Antony12\",\"role\":\"user\",\"state\":0},{\"account\":\"user13\",\"createTime\":\"2024-04-02 20:54:08\",\"id\":1775144682826113025,\"name\":\"Antony13\",\"role\":\"user\",\"state\":0},{\"account\":\"user14\",\"createTime\":\"2024-04-02 20:56:28\",\"id\":1775145271509262337,\"name\":\"Antony14\",\"role\":\"user\",\"state\":0},{\"account\":\"user15\",\"createTime\":\"2024-04-02 20:59:35\",\"id\":1775146054472572929,\"name\":\"Antony15\",\"role\":\"user\",\"state\":0},{\"account\":\"user16\",\"createTime\":\"2024-04-02 20:59:47\",\"id\":1775146105840214017,\"name\":\"Antony16\",\"role\":\"user\",\"state\":0},{\"account\":\"user17\",\"createTime\":\"2024-04-02 20:59:59\",\"id\":1775146158390648834,\"name\":\"Antony17\",\"role\":\"user\",\"state\":0},{\"account\":\"user18\",\"createTime\":\"2024-04-02 21:00:12\",\"id\":1775146210676842498,\"name\":\"Antony18\",\"role\":\"user\",\"state\":0}],\"size\":10,\"total\":33}}', 42, '2024-06-13 00:33:35', '2024-06-13 00:33:35', 0);
INSERT INTO `t_log` VALUES (1800929602185445378, '/api/admin/user/add', '管理员添加用户信息', 1, 'POST', 'top.sharehome.springbootinittemplate.controller.user.AdminUserController.addUser', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{\"name\":\"Antony32\",\"account\":\"user32\"}', 0, '{\"msg\":\"添加成功\",\"code\":200}', 46, '2024-06-13 00:34:11', '2024-06-13 00:34:11', 0);
INSERT INTO `t_log` VALUES (1800929644078153729, '/api/admin/user/add', '管理员添加用户信息', 1, 'POST', 'top.sharehome.springbootinittemplate.controller.user.AdminUserController.addUser', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{\"name\":\"Antony32\",\"account\":\"user32\"}', 1, '{\"msg\":\"账号名称已经存在\",\"code\":11001,\"name\":\"USERNAME_ALREADY_EXISTS\"}', 13, '2024-06-13 00:34:21', '2024-06-13 00:34:21', 0);
INSERT INTO `t_log` VALUES (1800929923221667841, '/api/admin/user/page', '管理员查询用户信息', 3, 'GET', 'top.sharehome.springbootinittemplate.controller.user.AdminUserController.pageUser', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{\"allowDeep\":\"\",\"role\":\"\",\"size\":\"\",\"name\":\"\",\"page\":\"\",\"state\":\"\",\"account\":\"user32\"}', 0, '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"current\":1,\"pages\":1,\"records\":[{\"account\":\"user32\",\"createTime\":\"2024-06-13 00:34:11\",\"id\":1800929601992507394,\"name\":\"Antony32\",\"role\":\"user\",\"state\":0}],\"size\":500,\"total\":1}}', 55, '2024-06-13 00:35:28', '2024-06-13 00:35:28', 0);
INSERT INTO `t_log` VALUES (1800930022186270721, '/api/admin/user/delete/1800929601992507394', '管理员删除用户信息', 2, 'DELETE', 'top.sharehome.springbootinittemplate.controller.user.AdminUserController.deleteUser', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{}', 0, '{\"msg\":\"删除成功\",\"code\":200}', 98, '2024-06-13 00:35:52', '2024-06-13 00:35:52', 0);
INSERT INTO `t_log` VALUES (1800930032965632001, '/api/admin/user/delete/1800929601992507394', '管理员删除用户信息', 2, 'DELETE', 'top.sharehome.springbootinittemplate.controller.user.AdminUserController.deleteUser', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{}', 1, '{\"msg\":\"用户账户不存在\",\"code\":11006,\"name\":\"USER_ACCOUNT_DOES_NOT_EXIST\"}', 10, '2024-06-13 00:35:54', '2024-06-13 00:35:54', 0);
INSERT INTO `t_log` VALUES (1800930086321373185, '/api/admin/user/export', '管理员导出用户表格', 6, 'GET', 'top.sharehome.springbootinittemplate.controller.user.AdminUserController.exportExcel', 1900, '172.20.10.3', '0|0|0|内网IP|内网IP', '{}', 0, '{}', 2112, '2024-06-13 00:36:07', '2024-06-13 00:36:07', 0);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `user_password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
  `user_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户邮箱',
  `user_login_num` int NOT NULL DEFAULT 0 COMMENT '用户连续登录失败次数',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `user_avatar_id` bigint NULL DEFAULT NULL COMMENT '用户头像ID',
  `user_role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户角色（admin/user）',
  `user_state` tinyint NOT NULL DEFAULT 0 COMMENT '用户状态（0表示启用，1表示禁用）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1900, 'admin', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'AntonyCheng', NULL, 'admin', 0, '2024-03-27 22:01:16', '2024-09-06 11:17:08', 0);
INSERT INTO `t_user` VALUES (1901, 'user', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'AntonyCoding', NULL, 'user', 0, '2024-03-27 22:01:17', '2024-09-02 10:06:57', 0);
INSERT INTO `t_user` VALUES (1902, 'user1', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony1', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1903, 'user2', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony2', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1904, 'user3', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony3', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1905, 'user4', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony4', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1906, 'user5', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony5', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1907, 'user6', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony6', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1908, 'user7', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony7', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1909, 'user8', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony8', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1910, 'user9', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony9', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775113809426817025, 'user10', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony10', NULL, 'user', 0, '2024-04-02 18:51:27', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775115510955286530, 'user11', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony11', NULL, 'user', 0, '2024-04-02 18:58:13', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775135430363455490, 'user12', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony12', NULL, 'user', 0, '2024-04-02 20:17:22', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775144682826113025, 'user13', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony13', NULL, 'user', 0, '2024-04-02 20:54:08', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775145271509262337, 'user14', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony14', NULL, 'user', 0, '2024-04-02 20:56:28', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775146054472572929, 'user15', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony15', NULL, 'user', 0, '2024-04-02 20:59:35', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775146105840214017, 'user16', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony16', NULL, 'user', 0, '2024-04-02 20:59:47', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775146158390648834, 'user17', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony17', NULL, 'user', 0, '2024-04-02 20:59:59', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775146210676842498, 'user18', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony18', NULL, 'user', 0, '2024-04-02 21:00:12', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775146297222111233, 'user19', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony19', NULL, 'user', 0, '2024-04-02 21:00:33', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775365730125459458, 'user20', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony20', NULL, 'user', 0, '2024-04-03 11:32:29', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775365807405510658, 'user21', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony21', NULL, 'user', 0, '2024-04-03 11:32:48', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775366194225197057, 'user22', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony22', NULL, 'user', 0, '2024-04-03 11:34:20', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775366255906631682, 'user23', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony23', NULL, 'user', 0, '2024-04-03 11:34:35', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775366338987405314, 'user24', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony24', NULL, 'user', 0, '2024-04-03 11:34:55', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775366388358557698, 'user25', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony25', NULL, 'user', 0, '2024-04-03 11:35:06', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775366594307272705, 'user26', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony26', NULL, 'user', 0, '2024-04-03 11:35:55', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775366755221745665, 'user27', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony27', NULL, 'user', 0, '2024-04-03 11:36:34', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775366808783007746, 'user28', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony28', NULL, 'user', 0, '2024-04-03 11:36:47', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775366952316284930, 'user29', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony29', NULL, 'user', 0, '2024-04-03 11:37:21', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1775393764199084034, 'user30', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony30', NULL, 'user', 0, '2024-04-03 13:23:53', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1792074787028615169, 'user31', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony31', NULL, 'user', 0, '2024-05-19 14:08:19', '2024-08-10 12:38:09', 0);
INSERT INTO `t_user` VALUES (1800929601992507394, 'user32', 'FBWzA2jdoO9tjpiK3rKTbw==', '1911261716@qq.com', 0, 'Antony32', NULL, 'user', 0, '2024-06-13 00:34:11', '2024-09-04 08:59:46', 0);

SET FOREIGN_KEY_CHECKS = 1;
