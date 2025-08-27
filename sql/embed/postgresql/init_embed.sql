-- ----------------------------
-- 启用扩展 vector
-- ----------------------------
CREATE EXTENSION IF NOT EXISTS vector;

-- ----------------------------
-- Table structure for t_knowledge
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_knowledge";
CREATE TABLE "public"."t_knowledge"
(
    "knowledge_id"          int8                                        NOT NULL,
    "knowledge_user_id"     int8                                        NOT NULL,
    "knowledge_name"        varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "knowledge_description" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "knowledge_state"       int2                                        NOT NULL DEFAULT 0,
    "knowledge_model_id"    int8                                        NOT NULL,
    "create_time"           timestamp(6)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time"           timestamp(6)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "is_deleted"            int2                                        NOT NULL DEFAULT 0
);
COMMENT ON COLUMN "public"."t_knowledge"."knowledge_id" IS '知识库ID';
COMMENT ON COLUMN "public"."t_knowledge"."knowledge_user_id" IS '知识库用户ID';
COMMENT ON COLUMN "public"."t_knowledge"."knowledge_name" IS '知识库名称';
COMMENT ON COLUMN "public"."t_knowledge"."knowledge_description" IS '知识库描述';
COMMENT ON COLUMN "public"."t_knowledge"."knowledge_state" IS '知识库状态（0表示启用，1表示禁用）';
COMMENT ON COLUMN "public"."t_knowledge"."knowledge_model_id" IS '知识库绑定向量模型ID';
COMMENT ON COLUMN "public"."t_knowledge"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_knowledge"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_knowledge"."is_deleted" IS '逻辑删除（0表示未删除，1表示已删除）';

-- ----------------------------
-- Primary Key structure for table t_knowledge
-- ----------------------------
ALTER TABLE "public"."t_knowledge"
    ADD CONSTRAINT "t_knowledge_pkey" PRIMARY KEY ("knowledge_id");

-- ----------------------------
-- Table structure for t_document
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_document";
CREATE TABLE "public"."t_document"
(
    "document_id"           int8                                       NOT NULL,
    "document_knowledge_id" int8                                       NOT NULL,
    "document_user_id"      int8                                       NOT NULL,
    "document_name"         varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "document_state"        int2                                       NOT NULL DEFAULT 0,
    "create_time"           timestamp(6)                               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time"           timestamp(6)                               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "is_deleted"            int2                                       NOT NULL DEFAULT 0
);
COMMENT ON COLUMN "public"."t_document"."document_id" IS '文档ID';
COMMENT ON COLUMN "public"."t_document"."document_knowledge_id" IS '文档知识库ID';
COMMENT ON COLUMN "public"."t_document"."document_user_id" IS '文档用户ID';
COMMENT ON COLUMN "public"."t_document"."document_name" IS '文档名称';
COMMENT ON COLUMN "public"."t_document"."document_state" IS '文档状态（0表示启用，1表示禁用）';
COMMENT ON COLUMN "public"."t_document"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_document"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_document"."is_deleted" IS '逻辑删除（0表示未删除，1表示已删除）';

-- ----------------------------
-- Primary Key structure for table t_document
-- ----------------------------
ALTER TABLE "public"."t_document"
    ADD CONSTRAINT "t_document_pkey" PRIMARY KEY ("document_id");

-- ----------------------------
-- Table structure for t_chunk
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_chunk";
CREATE TABLE "public"."t_chunk"
(
    "chunk_id"           int8                                NOT NULL,
    "chunk_document_id"  int8                                NOT NULL,
    "chunk_knowledge_id" int8                                NOT NULL,
    "chunk_user_id"      int8                                NOT NULL,
    "chunk_content"      text COLLATE "pg_catalog"."default" NOT NULL,
    "chunk_embedding"    "public"."vector",
    "chunk_state"        int2                                NOT NULL,
    "chunk_dimension"    int4,
    "chunk_fail_reason"  varchar(255) COLLATE "pg_catalog"."default",
    "create_time"        timestamp(6)                        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time"        timestamp(6)                        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "is_deleted"         int2                                NOT NULL DEFAULT 0
);
COMMENT ON COLUMN "public"."t_chunk"."chunk_id" IS '数据块ID';
COMMENT ON COLUMN "public"."t_chunk"."chunk_document_id" IS '数据块文档ID';
COMMENT ON COLUMN "public"."t_chunk"."chunk_knowledge_id" IS '数据块知识库ID';
COMMENT ON COLUMN "public"."t_chunk"."chunk_user_id" IS '数据块用户ID';
COMMENT ON COLUMN "public"."t_chunk"."chunk_content" IS '数据块内容';
COMMENT ON COLUMN "public"."t_chunk"."chunk_embedding" IS '数据块向量数据';
COMMENT ON COLUMN "public"."t_chunk"."chunk_state" IS '数据块状态（0表示待训练，1表示训练中，2表示训练成功，3表示训练失败）';
COMMENT ON COLUMN "public"."t_chunk"."chunk_dimension" IS '数据块向量维度';
COMMENT ON COLUMN "public"."t_chunk"."chunk_fail_reason" IS '数据块训练失败原因';
COMMENT ON COLUMN "public"."t_chunk"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_chunk"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_chunk"."is_deleted" IS '逻辑删除（0表示未删除，1表示已删除）';

-- ----------------------------
-- Primary Key structure for table t_chunk
-- ----------------------------
ALTER TABLE "public"."t_chunk"
    ADD CONSTRAINT "t_chunk_pkey" PRIMARY KEY ("chunk_id");