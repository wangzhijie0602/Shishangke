CREATE TABLE `user`
(
    `id`              bigint       NOT NULL COMMENT '主键ID',
    `username`        varchar(255) NOT NULL COMMENT '唯一用户名',
    `nickname`        varchar(255)          DEFAULT NULL COMMENT '用户昵称',
    `avatar`          varchar(255) NOT NULL DEFAULT 'https://img.icons8.com/?size=100&id=7WwZau6gMj6x&format=png&color=000000' COMMENT '头像URL',
    `email`           varchar(255)          DEFAULT NULL COMMENT '电子邮箱',
    `phone`           varchar(50)           DEFAULT NULL COMMENT '电话号码',
    `password`        varchar(255) NOT NULL COMMENT '密码',
    `status`          enum('ACTIVE', 'DISABLED', 'LOCKED')  NOT NULL DEFAULT 'ACTIVE' COMMENT '账号状态',
    `role`            enum('ADMIN', 'BOSS', 'MANAGER', 'CHEF', 'CUSTOMER', 'DELIVERY') NOT NULL,
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
    `status`         enum('OPEN', 'CLOSED', 'SUSPENDED', 'PENDING_REVIEW', 'REJECTED', 'BANNED') DEFAULT 'PENDING_REVIEW' COMMENT '商家状态',
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
                        `category`         enum('HOT_SALE', 'SPECIALTY', 'STAPLE_FOOD', 'SNACK', 'SOUP', 'COLD_DISH', 'HOT_DISH', 'DESSERT', 'BEVERAGE', 'ALCOHOL', 'SET_MEAL', 'OTHER') NOT NULL COMMENT '菜品分类',
                        `image_url`        VARCHAR(255)  DEFAULT NULL COMMENT '菜品图片URL',
                        `status`           enum('ENABLED', 'DISABLED', 'SOLD_OUT')   NOT NULL DEFAULT 'ENABLED' COMMENT '菜品状态',
                        `sort_order`       INT           NOT NULL DEFAULT 0 COMMENT '排序权重（数值越小越靠前）',
                        `created_at`       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updated_at`       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `is_deleted`       TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '软删除标记',
                        PRIMARY KEY (`menu_id`),
                        KEY `idx_merchant_id` (`merchant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT='菜单表';

CREATE TABLE `customer` (
                            `id` bigint NOT NULL COMMENT '顾客ID，主键，自增',
                            `username` varchar(50) NOT NULL,
                            `password` varchar(255) NOT NULL,
                            `nickname` varchar(50) NOT NULL COMMENT '昵称',
                            `gender` varchar(10) NOT NULL DEFAULT 'OTHER' COMMENT '性别(MALE:男, FEMALE:女, OTHER:其他)',
                            `birth_date` date DEFAULT NULL COMMENT '出生日期',
                            `default_address` varchar(255) DEFAULT NULL COMMENT '默认收货地址',
                            `preferences` text COMMENT '饮食偏好（JSON格式）',
                            `vip_level` int NOT NULL DEFAULT '0' COMMENT 'VIP等级',
                            `points` int NOT NULL DEFAULT '0' COMMENT '积分',
                            `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '软删除标记',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='顾客信息表';

CREATE TABLE `customer_address` (
                                    `id` bigint NOT NULL primary key AUTO_INCREMENT COMMENT '地址ID，主键，自增',
                                    `customer_id` bigint NOT NULL unique COMMENT '用户ID（外键关联 customer_id）',
                                    `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
                                    `phone` varchar(20) NOT NULL COMMENT '联系电话',
                                    `province` varchar(50) NOT NULL COMMENT '省份',
                                    `city` varchar(50) NOT NULL COMMENT '城市',
                                    `district` varchar(50) NOT NULL COMMENT '区/县',
                                    `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
                                    `tag` varchar(20) DEFAULT NULL COMMENT '地址标签（如家、公司、学校等）',
                                    `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认地址（0:否，1:是）',
                                    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '软删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户外卖地址表';

CREATE TABLE `order` (
                         `id` bigint(20) NOT NULL COMMENT '订单ID',
                         `customer_id` bigint(20) NOT NULL COMMENT '用户ID',
                         `merchant_id` bigint(20) NOT NULL COMMENT '商家ID',
                        'merchant_name' varchar(100) comment '店铺名称',
                         `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
                         `actual_amount` decimal(10,2) NOT NULL COMMENT '实际支付金额',
                         `status` ENUM('PENDING', 'PAID', 'PREPARING', 'DELIVERING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING' COMMENT '订单状态',
                         `address_id` bigint(20) COMMENT '收货地址ID',
                         `receiver_name` varchar(50) COMMENT '收货人姓名',
                         `receiver_phone` varchar(20) COMMENT '收货人电话',
                         `receiver_address` varchar(255) COMMENT '收货地址',
                         `delivery_fee` decimal(10,2) DEFAULT '0.00' COMMENT '配送费',
                         `remark` varchar(255) DEFAULT NULL COMMENT '订单备注',
                         `expected_delivery_time` datetime DEFAULT NULL COMMENT '预计送达时间',
                         `actual_delivery_time` datetime DEFAULT NULL COMMENT '实际送达时间',
                         `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                         PRIMARY KEY (`id`),
                         KEY `idx_customer_id` (`customer_id`),
                         KEY `idx_merchant_id` (`merchant_id`),
                         KEY `idx_status` (`status`),
                         KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE `order_item` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
                              `order_id` bigint(20) NOT NULL COMMENT '订单ID',
                              `menu_id` bigint(20) NOT NULL COMMENT '菜品ID',
                              `menu_name` varchar(100) NOT NULL COMMENT '菜品名称',
                              `quantity` int(11) NOT NULL COMMENT '数量',
                              `price` decimal(10,2) NOT NULL COMMENT '单价',
                              `total_price` decimal(10,2) NOT NULL COMMENT '总价',
                              `image_url` varchar(255) DEFAULT NULL COMMENT '菜品图片',
                              `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              KEY `idx_order_id` (`order_id`),
                              KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

CREATE TABLE `payment` (
                           `id` bigint(20) NOT NULL COMMENT '支付ID',
                           `order_id` bigint(20) NOT NULL COMMENT '订单ID',
                           `customer_id` bigint(20) NOT NULL COMMENT '用户ID',
                           `payment_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
                           `payment_method` ENUM('WECHAT', 'ALIPAY', 'CREDIT_CARD', 'CASH') NOT NULL COMMENT '支付方式',
                           `transaction_id` varchar(100) DEFAULT NULL COMMENT '第三方交易号',
                           `status` ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') NOT NULL COMMENT '支付状态',
                           `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
                           `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_order_id` (`order_id`),
                           KEY `idx_customer_id` (`customer_id`),
                           KEY `idx_status` (`status`),
                           KEY `idx_payment_time` (`payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';

CREATE TABLE `refund` (
                          `id` bigint(20) NOT NULL COMMENT '退款ID',
                          `payment_id` bigint(20) NOT NULL COMMENT '支付ID',
                          `order_id` bigint(20) NOT NULL COMMENT '订单ID',
                          `customer_id` bigint(20) NOT NULL COMMENT '用户ID',
                          `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
                          `reason` varchar(255) DEFAULT NULL COMMENT '退款原因',
                          `status` ENUM('PENDING', 'SUCCESS', 'FAILED') NOT NULL COMMENT '退款状态',
                          `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
                          `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`),
                          KEY `idx_payment_id` (`payment_id`),
                          KEY `idx_order_id` (`order_id`),
                          KEY `idx_customer_id` (`customer_id`),
                          KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款表';

CREATE TABLE `shopping_cart` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
                                 `customer_id` bigint(20) NOT NULL COMMENT '用户ID',
                                 `merchant_id` bigint(20) NOT NULL COMMENT '商家ID',
                                 `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_customer_id` (`customer_id`),
                                 KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

CREATE TABLE `shopping_cart_item` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
                                      `cart_id` bigint(20) NOT NULL COMMENT '购物车ID',
                                      `menu_id` bigint(20) NOT NULL COMMENT '菜品ID',
                                      `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '数量',
                                      `price` decimal(10,2) NOT NULL COMMENT '单价',
                                      `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_cart_id` (`cart_id`),
                                      KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车项表';


