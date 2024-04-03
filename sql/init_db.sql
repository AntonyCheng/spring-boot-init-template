CREATE database if NOT EXISTS `init_db` default character set utf8mb4 collate utf8mb4_general_ci;
use
    `init_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `user_password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `user_role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户角色（admin/user）',
  `user_state` tinyint NOT NULL COMMENT '用户状态（0表示启用，1表示禁用）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1900, 'admin', '207acd61a3c1bd506d7e9a4535359f8a', 'AntonyCheng', NULL, 'admin', 0, '2024-03-27 22:01:16', '2024-04-02 21:20:26', 0);
INSERT INTO `t_user` VALUES (1901, 'user', '207acd61a3c1bd506d7e9a4535359f8a', 'AntonyCoding', NULL, 'user', 0, '2024-03-27 22:01:17', '2024-04-03 13:54:37', 0);
INSERT INTO `t_user` VALUES (1902, 'user1', '6c8cc4d828425167278e9fe37789fd46', 'Antony1', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-04-03 13:27:53', 0);
INSERT INTO `t_user` VALUES (1903, 'user2', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony2', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-04-03 13:27:55', 0);
INSERT INTO `t_user` VALUES (1904, 'user3', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony3', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-04-03 13:27:55', 0);
INSERT INTO `t_user` VALUES (1905, 'user4', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony4', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-04-02 20:36:07', 0);
INSERT INTO `t_user` VALUES (1906, 'user5', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony5', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-04-03 13:22:11', 0);
INSERT INTO `t_user` VALUES (1907, 'user6', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony6', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-04-02 20:36:07', 0);
INSERT INTO `t_user` VALUES (1908, 'user7', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony7', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-04-02 20:36:07', 0);
INSERT INTO `t_user` VALUES (1909, 'user8', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony8', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-04-02 20:36:07', 0);
INSERT INTO `t_user` VALUES (1910, 'user9', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony9', NULL, 'user', 0, '2024-04-01 23:40:55', '2024-04-02 20:36:07', 0);
INSERT INTO `t_user` VALUES (1775113809426817025, 'user10', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony10', NULL, 'user', 0, '2024-04-02 18:51:27', '2024-04-02 20:36:07', 0);
INSERT INTO `t_user` VALUES (1775115510955286530, 'user11', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony11', NULL, 'user', 0, '2024-04-02 18:58:13', '2024-04-02 20:36:07', 0);
INSERT INTO `t_user` VALUES (1775135430363455490, 'user12', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony12', NULL, 'user', 0, '2024-04-02 20:17:22', '2024-04-02 20:36:07', 0);
INSERT INTO `t_user` VALUES (1775144682826113025, 'user13', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony13', NULL, 'user', 0, '2024-04-02 20:54:08', '2024-04-02 20:54:08', 0);
INSERT INTO `t_user` VALUES (1775145271509262337, 'user14', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony14', NULL, 'user', 0, '2024-04-02 20:56:28', '2024-04-02 20:56:28', 0);
INSERT INTO `t_user` VALUES (1775146054472572929, 'user15', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony15', NULL, 'user', 0, '2024-04-02 20:59:35', '2024-04-02 20:59:35', 0);
INSERT INTO `t_user` VALUES (1775146105840214017, 'user16', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony16', NULL, 'user', 0, '2024-04-02 20:59:47', '2024-04-02 20:59:47', 0);
INSERT INTO `t_user` VALUES (1775146158390648834, 'user17', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony17', NULL, 'user', 0, '2024-04-02 20:59:59', '2024-04-02 20:59:59', 0);
INSERT INTO `t_user` VALUES (1775146210676842498, 'user18', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony18', NULL, 'user', 0, '2024-04-02 21:00:12', '2024-04-02 21:00:12', 0);
INSERT INTO `t_user` VALUES (1775146297222111233, 'user19', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony19', NULL, 'user', 0, '2024-04-02 21:00:33', '2024-04-02 21:00:33', 0);
INSERT INTO `t_user` VALUES (1775365730125459458, 'user20', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony20', NULL, 'user', 0, '2024-04-03 11:32:29', '2024-04-03 11:38:28', 0);
INSERT INTO `t_user` VALUES (1775365807405510658, 'user21', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony21', NULL, 'user', 0, '2024-04-03 11:32:48', '2024-04-03 11:38:31', 0);
INSERT INTO `t_user` VALUES (1775366194225197057, 'user22', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony22', NULL, 'user', 0, '2024-04-03 11:34:20', '2024-04-03 11:38:32', 0);
INSERT INTO `t_user` VALUES (1775366255906631682, 'user23', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony23', NULL, 'user', 0, '2024-04-03 11:34:35', '2024-04-03 11:38:34', 0);
INSERT INTO `t_user` VALUES (1775366338987405314, 'user24', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony24', NULL, 'user', 0, '2024-04-03 11:34:55', '2024-04-03 13:22:21', 0);
INSERT INTO `t_user` VALUES (1775366388358557698, 'user25', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony25', NULL, 'user', 0, '2024-04-03 11:35:06', '2024-04-03 11:38:39', 0);
INSERT INTO `t_user` VALUES (1775366594307272705, 'user26', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony26', NULL, 'user', 0, '2024-04-03 11:35:55', '2024-04-03 11:38:41', 0);
INSERT INTO `t_user` VALUES (1775366755221745665, 'user27', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony27', NULL, 'user', 0, '2024-04-03 11:36:34', '2024-04-03 11:38:45', 0);
INSERT INTO `t_user` VALUES (1775366808783007746, 'user28', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony28', NULL, 'user', 0, '2024-04-03 11:36:47', '2024-04-03 11:38:47', 0);
INSERT INTO `t_user` VALUES (1775366952316284930, 'user29', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony29', NULL, 'user', 0, '2024-04-03 11:37:21', '2024-04-03 11:38:49', 0);
INSERT INTO `t_user` VALUES (1775393764199084033, 'user30', '207acd61a3c1bd506d7e9a4535359f8a', 'Antony30', NULL, 'user', 0, '2024-04-03 13:23:53', '2024-04-03 13:23:53', 0);

SET FOREIGN_KEY_CHECKS = 1;
