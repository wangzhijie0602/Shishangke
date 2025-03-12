CREATE TABLE `user`
(
    `id`              bigint       NOT NULL COMMENT '主键ID',
    `username`        varchar(255) NOT NULL COMMENT '唯一用户名',
    `nickname`        varchar(255)          DEFAULT NULL COMMENT '用户昵称',
    `avatar`          varchar(255) NOT NULL DEFAULT 'https://img.icons8.com/?size=100&id=7WwZau6gMj6x&format=png&color=000000' COMMENT '头像URL',
    `email`           varchar(255)          DEFAULT NULL COMMENT '电子邮箱',
    `phone`           varchar(50)           DEFAULT NULL COMMENT '电话号码',
    `password`        varchar(255) NOT NULL COMMENT '密码',
    `status`          varchar(10)  NOT NULL DEFAULT 'ENABLED' COMMENT '账号状态',
    `role`            varchar(10)  NOT NULL DEFAULT 'USER',
    `last_login_time` timestamp    NULL     DEFAULT NULL COMMENT '最后登录时间',
    `created_at`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      tinyint(1)   NOT NULL DEFAULT '0' COMMENT '软删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`),
    UNIQUE KEY `email` (`email`),
    UNIQUE KEY `phone` (`phone`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户表';


CREATE TABLE `merchant`
(
    `id`             int          NOT NULL AUTO_INCREMENT COMMENT '商家ID，主键，自增',
    `name`           varchar(100) NOT NULL COMMENT '商家名称',
    `logo`           varchar(255)          DEFAULT NULL COMMENT '商家logo图片URL',
    `phone`          varchar(20)  NOT NULL COMMENT '商家联系电话',
    `address`        varchar(255) NOT NULL COMMENT '商家详细地址',
    `longitude`      decimal(10, 7)        DEFAULT NULL COMMENT '经度坐标，用于地图定位',
    `latitude`       decimal(10, 7)        DEFAULT NULL COMMENT '纬度坐标，用于地图定位',
    `business_hours` varchar(100)          DEFAULT NULL COMMENT '营业时间，如9:00-22:00',
    `description`    text COMMENT '商家描述信息',
    `min_price`      decimal(10, 2)        DEFAULT '0.00' COMMENT '最低起送价',
    `status`         varchar(10)           DEFAULT 'ENABLED' COMMENT '商家状态',
    `created_at`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     tinyint(1)            DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE `menu` (
                        `menu_id`          INT          NOT NULL AUTO_INCREMENT COMMENT '菜单项ID，主键，自增',
                        `merchant_id`      BIGINT          NOT NULL COMMENT '所属商家ID（外键关联 merchant.id）',
                        `name`             VARCHAR(100) NOT NULL COMMENT '菜品名称',
                        `description`      TEXT                  DEFAULT NULL COMMENT '菜品描述',
                        `price`            DECIMAL(10, 2) NOT NULL COMMENT '菜品价格（单位：元）',
                        `category`         VARCHAR(50)   NOT NULL COMMENT '菜品分类',
                        `image_url`        VARCHAR(255)  DEFAULT NULL COMMENT '菜品图片URL',
                        `status`           VARCHAR(10)   NOT NULL DEFAULT 'ENABLED' COMMENT '菜品状态',
                        `sort_order`       INT           NOT NULL DEFAULT 0 COMMENT '排序权重（数值越小越靠前）',
                        `created_at`       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updated_at`       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `is_deleted`       TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '软删除标记',
                        PRIMARY KEY (`menu_id`),
                        KEY `idx_merchant_id` (`merchant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT='菜单表';


