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


CREATE TABLE `merchant_review` (
                                   `review_id`     BIGINT        NOT NULL AUTO_INCREMENT COMMENT '评价ID，主键，自增',
                                   `user_id`       BIGINT        NOT NULL COMMENT '评价用户ID（外键关联 user.id）',
                                   `merchant_id`   INT           NOT NULL COMMENT '商家ID（外键关联 merchant.id）',
                                   `content`       TEXT          DEFAULT NULL COMMENT '评价内容（文字评论）',
                                   `rating`        DECIMAL(3,1)  NOT NULL COMMENT '评分（1.0 ~ 5.0）',
                                   `images`        TEXT          DEFAULT NULL COMMENT '评价图片URL（JSON数组格式，如["url1", "url2"]）',
                                   `is_anonymous`  TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '是否匿名（0: 否，1: 是）',
                                   `created_at`    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价创建时间',
                                   `updated_at`    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                                   `is_deleted`    TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '软删除标记',
                                   PRIMARY KEY (`review_id`),
                                   KEY `idx_user_id` (`user_id`),
                                   KEY `idx_merchant_id` (`merchant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT='商家评价表';

CREATE TABLE `order` (
    `order_id`       BIGINT         NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键，自增',
    `user_id`        BIGINT         NOT NULL COMMENT '下单用户ID（外键关联 user.id）',
    `merchant_id`    INT            NOT NULL COMMENT '商家ID（外键关联 merchant.id）',
    `order_number`   VARCHAR(50)    NOT NULL COMMENT '订单编号，唯一',
    `total_amount`   DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    `status`         VARCHAR(20)    NOT NULL DEFAULT 'PENDING' COMMENT '订单状态(PENDING:待付款, PAID:已付款, PREPARING:准备中, DELIVERING:配送中, COMPLETED:已完成, CANCELLED:已取消)',
    `payment_method` VARCHAR(20)    DEFAULT NULL COMMENT '支付方式(WECHAT:微信, ALIPAY:支付宝, CASH:现金)',
    `payment_status` VARCHAR(20)    NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态(UNPAID:未支付, PAID:已支付, REFUNDED:已退款)',
    `address`        VARCHAR(255)   NOT NULL COMMENT '配送地址',
    `phone`          VARCHAR(20)    NOT NULL COMMENT '联系电话',
    `remark`         VARCHAR(255)   DEFAULT NULL COMMENT '订单备注',
    `delivery_fee`   DECIMAL(10, 2) DEFAULT 0.00 COMMENT '配送费',
    `expected_time`  TIMESTAMP      DEFAULT NULL COMMENT '预计送达时间',
    `created_at`     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '软删除标记',
    PRIMARY KEY (`order_id`),
    UNIQUE KEY `idx_order_number` (`order_number`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT='订单信息表';

CREATE TABLE `customer` (
    `customer_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '顾客ID，主键，自增',
    `user_id`        BIGINT       NOT NULL COMMENT '关联的用户ID（外键关联 user.id）',
    `real_name`      VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
    `gender`         VARCHAR(10)  DEFAULT NULL COMMENT '性别(MALE:男, FEMALE:女, OTHER:其他)',
    `birth_date`     DATE         DEFAULT NULL COMMENT '出生日期',
    `default_address` VARCHAR(255) DEFAULT NULL COMMENT '默认收货地址',
    `preferences`    TEXT         DEFAULT NULL COMMENT '饮食偏好（JSON格式）',
    `vip_level`      INT          NOT NULL DEFAULT 0 COMMENT 'VIP等级',
    `points`         INT          NOT NULL DEFAULT 0 COMMENT '积分',
    `created_at`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '软删除标记',
    PRIMARY KEY (`customer_id`),
    UNIQUE KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT='顾客信息表';

CREATE TABLE `menu_review` (
    `review_id`      BIGINT        NOT NULL AUTO_INCREMENT COMMENT '评价ID，主键，自增',
    `user_id`        BIGINT        NOT NULL COMMENT '评价用户ID（外键关联 user.id）',
    `menu_id`        INT           NOT NULL COMMENT '菜品ID（外键关联 menu.menu_id）',
    `order_id`       BIGINT        NOT NULL COMMENT '关联订单ID（外键关联 order.order_id）',
    `content`        TEXT          DEFAULT NULL COMMENT '评价内容',
    `rating`         DECIMAL(3,1)  NOT NULL COMMENT '评分（1.0 ~ 5.0）',
    `images`         TEXT          DEFAULT NULL COMMENT '评价图片URL（JSON数组格式）',
    `taste_rating`   DECIMAL(3,1)  DEFAULT NULL COMMENT '口味评分',
    `appearance_rating` DECIMAL(3,1) DEFAULT NULL COMMENT '外观评分',
    `is_anonymous`   TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '是否匿名（0:否，1:是）',
    `likes_count`    INT           NOT NULL DEFAULT 0 COMMENT '点赞数量',
    `created_at`     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价创建时间',
    `updated_at`     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `is_deleted`     TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '软删除标记',
    PRIMARY KEY (`review_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_menu_id` (`menu_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT='菜品评价表';


