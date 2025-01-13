-- ----------------------------
-- Table structure for t_file
-- ----------------------------
DROP TABLE IF EXISTS t_file;
CREATE TABLE t_file
(
    file_id            bigint       NOT NULL PRIMARY KEY,
    file_unique_key    varchar(255) NOT NULL,
    file_name          varchar(255) NOT NULL,
    file_original_name varchar(255) NOT NULL,
    file_suffix        varchar(255) NOT NULL,
    file_size          bigint       NOT NULL,
    file_url           varchar(255) NOT NULL,
    file_oss_type      varchar(20)  NOT NULL,
    create_time        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted         smallint     NOT NULL DEFAULT 0
);

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS t_log;
CREATE TABLE t_log
(
    log_id             bigint        NOT NULL PRIMARY KEY,
    log_uri            varchar(255)  NOT NULL,
    log_description    varchar(255)  NOT NULL,
    log_operator       int           NOT NULL,
    log_request_method varchar(10)   NOT NULL,
    log_method         varchar(255)  NOT NULL,
    log_user_id        bigint        NULL     DEFAULT NULL,
    log_ip             varchar(128)  NULL     DEFAULT NULL,
    log_location       varchar(255)  NULL     DEFAULT NULL,
    log_param          varchar(2048) NOT NULL,
    log_result         int           NOT NULL,
    log_json           varchar(2048) NOT NULL,
    log_time           bigint        NOT NULL,
    create_time        timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted         smallint      NOT NULL DEFAULT 0
);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user
(
    user_id        bigint       NOT NULL PRIMARY KEY,
    user_account   varchar(64)  NOT NULL,
    user_password  varchar(64)  NOT NULL,
    user_email     varchar(255) NOT NULL,
    user_login_num int          NOT NULL DEFAULT 0,
    user_name      varchar(64)  NULL     DEFAULT NULL,
    user_avatar_id bigint       NULL     DEFAULT NULL,
    user_role      varchar(32)  NULL     DEFAULT NULL,
    user_state     smallint     NOT NULL DEFAULT 0,
    create_time    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted     smallint     NOT NULL DEFAULT 0
);