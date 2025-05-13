-- ----------------------------
-- Sequence structure for b_config_config_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."b_config_config_id_seq";
CREATE SEQUENCE "public"."b_config_config_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for t_reminder_reminder_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."t_reminder_reminder_id_seq";
CREATE SEQUENCE "public"."t_reminder_reminder_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for t_user_user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."t_user_user_id_seq";
CREATE SEQUENCE "public"."t_user_user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for t_wx_bot_bot_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."t_wx_bot_bot_id_seq";
CREATE SEQUENCE "public"."t_wx_bot_bot_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Table structure for b_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."b_config";
CREATE TABLE "public"."b_config" (
  "config_id" int8 NOT NULL DEFAULT nextval('b_config_config_id_seq'::regclass),
  "config_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "config_key" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "config_value" text COLLATE "pg_catalog"."default" NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "public_flag" bool NOT NULL DEFAULT false
)
;
COMMENT ON COLUMN "public"."b_config"."config_id" IS '主键';
COMMENT ON COLUMN "public"."b_config"."config_name" IS '参数名字';
COMMENT ON COLUMN "public"."b_config"."config_key" IS '参数key';
COMMENT ON COLUMN "public"."b_config"."update_time" IS '上次修改时间';
COMMENT ON COLUMN "public"."b_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."b_config"."public_flag" IS '是否公开';
COMMENT ON TABLE "public"."b_config" IS '系统配置';

-- ----------------------------
-- Records of b_config
-- ----------------------------
INSERT INTO "public"."b_config" VALUES (1, 'AI检测用户行为提示词', 'ai_detect_user_action_system_prompt', 'test', NULL, '2025-02-14 20:48:31.476748', '2025-02-14 20:48:31.476748', 'f');
INSERT INTO "public"."b_config" VALUES (5, 'bot 对话配置', 'bot_chat_config', '{"applyTrialText":"📢使用须知 AI对话仅为技术演示场景，未进行定向数据训练，不保证AI生成内容的准确性和功能的完整，欢迎有需求或对技术感兴趣的朋友联系📩 turbohub@163.com 畅聊人生。","freeChatNum":50,"freeRoomChatNum":50}
', NULL, '2025-05-09 20:55:24.533261', '2025-05-09 20:55:24.533261', 'f');
INSERT INTO "public"."b_config" VALUES (3, 'dev_wx_id', 'dev_wx_id', 'xx@chatroom', NULL, '2025-02-15 20:36:23.857427', '2025-02-15 20:36:23.857427', 'f');

-- ----------------------------
-- Table structure for t_reminder
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_reminder";
CREATE TABLE "public"."t_reminder" (
  "reminder_id" int4 NOT NULL DEFAULT nextval('t_reminder_reminder_id_seq'::regclass),
  "bot_id" int4 NOT NULL,
  "user_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "user_wx_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "room_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "reminder_content" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "reminder_time" timestamp(6) NOT NULL,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "handle_flag" bool NOT NULL DEFAULT false
)
;
COMMENT ON COLUMN "public"."t_reminder"."user_name" IS '用户昵称';
COMMENT ON COLUMN "public"."t_reminder"."room_id" IS '群聊id';
COMMENT ON COLUMN "public"."t_reminder"."reminder_content" IS '提醒内容';
COMMENT ON COLUMN "public"."t_reminder"."reminder_time" IS '提醒时间';
COMMENT ON COLUMN "public"."t_reminder"."handle_flag" IS '是否已处理';
COMMENT ON TABLE "public"."t_reminder" IS '提醒事项';

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_user";
CREATE TABLE "public"."t_user" (
  "user_id" int4 NOT NULL DEFAULT nextval('t_user_user_id_seq'::regclass),
  "wx_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "user_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "room_flag" bool NOT NULL,
  "user_desc" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "update_time" timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "create_time" timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "config" jsonb
)
;
COMMENT ON COLUMN "public"."t_user"."user_id" IS '用户id';
COMMENT ON COLUMN "public"."t_user"."wx_id" IS '个人/群聊 wx id';
COMMENT ON COLUMN "public"."t_user"."user_name" IS '个人/群聊 wx 昵称';
COMMENT ON COLUMN "public"."t_user"."room_flag" IS '是否群聊';
COMMENT ON COLUMN "public"."t_user"."config" IS '配置';
COMMENT ON TABLE "public"."t_user" IS '用户数据表 ';

-- ----------------------------
-- Table structure for t_webhook_key
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_webhook_key";
CREATE TABLE "public"."t_webhook_key" (
  "api_key" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "bot_id" int4 NOT NULL,
  "user_wx_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "user_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "expire_time" timestamp(6) NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "disabled_flag" bool NOT NULL DEFAULT false
)
;
COMMENT ON COLUMN "public"."t_webhook_key"."api_key" IS 'id';
COMMENT ON COLUMN "public"."t_webhook_key"."bot_id" IS 'bot id';
COMMENT ON COLUMN "public"."t_webhook_key"."user_wx_id" IS '用户/群聊id';
COMMENT ON COLUMN "public"."t_webhook_key"."user_name" IS '用户/群聊名称';
COMMENT ON COLUMN "public"."t_webhook_key"."expire_time" IS '到期时间';
COMMENT ON COLUMN "public"."t_webhook_key"."disabled_flag" IS '是否禁用';
COMMENT ON TABLE "public"."t_webhook_key" IS 'webhook key 数据表';

-- ----------------------------
-- Records of t_webhook_key
-- ----------------------------
INSERT INTO "public"."t_webhook_key" VALUES ('testxxxx', 100, 'xxx@chatroom', 'bot-test', '2029-02-15 12:45:51', NULL, '2025-02-14 20:46:06.85208', '2025-02-14 20:46:06.85208', 'f');

-- ----------------------------
-- Table structure for t_wx_bot
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_wx_bot";
CREATE TABLE "public"."t_wx_bot" (
  "bot_id" int4 NOT NULL DEFAULT nextval('t_wx_bot_bot_id_seq'::regclass),
  "bot_wx_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "bot_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON TABLE "public"."t_wx_bot" IS 'wx bot ';

-- ----------------------------
-- Records of t_wx_bot
-- ----------------------------
INSERT INTO "public"."t_wx_bot" VALUES (100, 'xxx', '土拨鼠', NULL, '2025-02-14 22:07:39.524091', '2025-02-14 22:07:39.524091');

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."b_config_config_id_seq"
OWNED BY "public"."b_config"."config_id";
SELECT setval('"public"."b_config_config_id_seq"', 10000, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."t_reminder_reminder_id_seq"
OWNED BY "public"."t_reminder"."reminder_id";
SELECT setval('"public"."t_reminder_reminder_id_seq"', 10000, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."t_user_user_id_seq"
OWNED BY "public"."t_user"."user_id";
SELECT setval('"public"."t_user_user_id_seq"', 10000, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."t_wx_bot_bot_id_seq"
OWNED BY "public"."t_wx_bot"."bot_id";
SELECT setval('"public"."t_wx_bot_bot_id_seq"', 10000, false);

-- ----------------------------
-- Primary Key structure for table b_config
-- ----------------------------
ALTER TABLE "public"."b_config" ADD CONSTRAINT "b_config_pkey" PRIMARY KEY ("config_id");

-- ----------------------------
-- Primary Key structure for table t_reminder
-- ----------------------------
ALTER TABLE "public"."t_reminder" ADD CONSTRAINT "t_reminder_pkey" PRIMARY KEY ("reminder_id");

-- ----------------------------
-- Indexes structure for table t_user
-- ----------------------------
CREATE UNIQUE INDEX "uk_wx_id" ON "public"."t_user" USING btree (
  "wx_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
COMMENT ON INDEX "public"."uk_wx_id" IS '个人/群聊 wx id';

-- ----------------------------
-- Primary Key structure for table t_user
-- ----------------------------
ALTER TABLE "public"."t_user" ADD CONSTRAINT "t_user_pkey" PRIMARY KEY ("user_id");

-- ----------------------------
-- Primary Key structure for table t_webhook_key
-- ----------------------------
ALTER TABLE "public"."t_webhook_key" ADD CONSTRAINT "t_webhook_key_pkey" PRIMARY KEY ("api_key");

-- ----------------------------
-- Primary Key structure for table t_wx_bot
-- ----------------------------
ALTER TABLE "public"."t_wx_bot" ADD CONSTRAINT "t_wx_bot_pkey" PRIMARY KEY ("bot_id");
