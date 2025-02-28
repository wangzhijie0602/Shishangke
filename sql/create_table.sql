CREATE DATABASE IF NOT EXISTS Shishangke;
USE Shishangke;

-- ----------------------------
-- 1. 用户表 (user)
-- ----------------------------
CREATE TABLE user
(
    id              BIGINT PRIMARY KEY COMMENT '主键ID',
    username        VARCHAR(255) UNIQUE NOT NULL COMMENT '唯一用户名',
    nickname        VARCHAR(255) COMMENT '用户昵称',
    avatar          VARCHAR(255)                 DEFAULT 'https://i-blog.csdnimg.cn/blog_migrate/a4fa5161369727154bc3a7d1c52bb9c0.png' COMMENT '头像URL',
    email           VARCHAR(255) UNIQUE COMMENT '电子邮箱',
    phone           VARCHAR(50) UNIQUE COMMENT '电话号码',
    password        VARCHAR(255)        NOT NULL COMMENT '密码',
    status          ENUM ('DISABLED', 'ENABLED') DEFAULT 'ENABLED' COMMENT '账号状态',
    last_login_time TIMESTAMP COMMENT '最后登录时间',
    created_at      TIMESTAMP                    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      TIMESTAMP                    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      BOOLEAN                      DEFAULT 0 COMMENT '软删除'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
    COMMENT ='用户表';

-- ----------------------------
-- 2. 角色表 (role)
-- ----------------------------
CREATE TABLE role
(
    id         INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_code  VARCHAR(50) UNIQUE NOT NULL COMMENT '角色唯一标识（如ADMIN）',
    role_name  VARCHAR(100)       NOT NULL COMMENT '角色显示名称',
    is_system  BOOLEAN   DEFAULT false COMMENT '是否系统内置角色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
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
    id          BIGINT PRIMARY KEY COMMENT '主键ID',
    user_id     BIGINT       NOT NULL COMMENT '用户ID',
    role_id     INT UNSIGNED NOT NULL COMMENT '角色ID',
    assigned_by BIGINT COMMENT '分配人ID',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_role_user (user_id),
    INDEX idx_user_role_role (role_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '用户-角色关系表';

-- ----------------------------
-- 4. 权限表 (permission)
-- ----------------------------
CREATE TABLE permission
(
    id              INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    permission_code VARCHAR(100) UNIQUE NOT NULL COMMENT '权限唯一标识（如USER:CREATE）',
    permission_name VARCHAR(100)        NOT NULL COMMENT '权限名称',
    parent_id       INT UNSIGNED COMMENT '父权限ID，用于构建权限树',
    is_system       BOOLEAN   DEFAULT false COMMENT '是否系统内置权限',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_permission_parent (parent_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '权限表';

-- ----------------------------
-- 5. 角色权限关联表 (role_permission)
-- ----------------------------
CREATE TABLE role_permission
(
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id       INT UNSIGNED NOT NULL COMMENT '角色ID',
    permission_id INT UNSIGNED NOT NULL COMMENT '权限ID',
    granted_by    BIGINT COMMENT '授权人ID',
    granted_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_perm_role (role_id),
    INDEX idx_role_perm_perm (permission_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
    COMMENT = '角色-权限关系表';
