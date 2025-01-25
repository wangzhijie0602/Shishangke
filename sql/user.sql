-- ----------------------------
-- 1. 用户表 (user)
-- ----------------------------
CREATE TABLE user
(
    id                    BIGINT PRIMARY KEY COMMENT '雪花算法生成的64位唯一ID',
    username              VARCHAR(255) UNIQUE NOT NULL COMMENT '唯一用户名（应用层处理大小写）',
    email                 VARCHAR(255) UNIQUE COMMENT '已验证邮箱（NULL表示未验证）',
    phone                 VARCHAR(50) UNIQUE COMMENT '国际格式号码（如：+86-13812345678）',
    password_hash         VARCHAR(255)        NOT NULL COMMENT 'Argon2id加密的密码哈希值',

    -- 第三方登录 --
    provider              VARCHAR(50) COMMENT '认证提供商（如wechat/github）',
    provider_id           VARCHAR(255) COMMENT '第三方用户ID',
    UNIQUE INDEX idx_provider (provider, provider_id) COMMENT '第三方登录唯一约束',

    -- 安全验证 --
    mfa_secret            VARCHAR(255) COMMENT 'Base32编码的MFA密钥',
    mfa_enabled           BOOLEAN   DEFAULT false COMMENT '是否启用MFA',
    recovery_codes        TEXT COMMENT 'AES加密的恢复代码（JSON数组）',
    password_reset_token  VARCHAR(255) COMMENT 'JWT格式重置令牌',
    reset_token_expiry    TIMESTAMP COMMENT '令牌过期时间',

    -- 状态管理 --
    is_verified           BOOLEAN   DEFAULT false COMMENT '验证状态',
    is_active             BOOLEAN   DEFAULT true COMMENT '账户激活状态',
    is_locked             BOOLEAN   DEFAULT false COMMENT '锁定状态',
    failed_login_attempts INT       DEFAULT 0 COMMENT '连续登录失败次数',
    deleted_at            TIMESTAMP           NULL COMMENT '软删除时间戳',

    -- 审计字段 --
    last_login            TIMESTAMP COMMENT '最后登录时间',
    last_login_ip         VARCHAR(45) COMMENT '最后登录IP（支持IPv6）',
    last_login_user_agent TEXT COMMENT '最后登录设备信息',
    password_changed_at   TIMESTAMP COMMENT '最后密码修改时间',
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
    COMMENT ='用户主表（业务层维护外键约束）';

-- ----------------------------
-- 2. 角色表 (role)
-- ----------------------------
CREATE TABLE role
(
    id          INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '自增角色ID',
    role_code   VARCHAR(50) UNIQUE NOT NULL COMMENT '角色唯一标识（如ADMIN）',
    role_name   VARCHAR(100)       NOT NULL COMMENT '角色显示名称',
    description TEXT COMMENT '角色描述',
    is_system   BOOLEAN   DEFAULT false COMMENT '是否系统内置角色',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
    COMMENT ='角色表';

-- 初始化数据
INSERT INTO role (role_code, role_name, is_system)
VALUES ('USER', '普通用户', true),
       ('ADMIN', '系统管理员', true);

-- ----------------------------
-- 3. 用户角色关联表 (user_role)
-- ----------------------------
CREATE TABLE user_role
(
    id          BIGINT AUTO_INCREMENT COMMENT '自增主键ID',
    user_id     BIGINT       NOT NULL COMMENT '用户ID',
    role_id     INT UNSIGNED NOT NULL COMMENT '角色ID',
    assigned_by BIGINT COMMENT '分配人ID',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id) COMMENT '用户与角色的唯一组合约束',
    INDEX idx_user_role_user (user_id) COMMENT '用户维度查询优化',
    INDEX idx_user_role_role (role_id) COMMENT '角色维度查询优化'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '用户-角色关系表（业务层维护关联）';