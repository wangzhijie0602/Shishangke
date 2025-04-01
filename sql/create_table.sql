create table Shishangke.customer
(
    id          bigint                                not null comment '顾客ID，主键，自增'
        primary key,
    username    varchar(50)                           not null,
    password    varchar(255)                          not null,
    nickname    varchar(50)                           not null comment '昵称',
    avatar      varchar(255)                          null,
    gender      varchar(10) default 'OTHER'           not null comment '性别(MALE:男, FEMALE:女, OTHER:其他)',
    birth_date  date                                  null comment '出生日期',
    preferences text                                  null comment '饮食偏好（JSON格式）',
    vip_level   int         default 0                 not null comment 'VIP等级',
    points      int         default 0                 not null comment '积分',
    created_at  timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at  timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted  tinyint(1)  default 0                 not null comment '软删除标记'
)
    comment '顾客信息表';

create table Shishangke.customer_address
(
    id             bigint auto_increment comment '地址ID，主键，自增'
        primary key,
    customer_id    bigint                               not null comment '用户ID（外键关联 customer_id）',
    receiver_name  varchar(50)                          not null comment '收货人姓名',
    phone          varchar(20)                          not null comment '联系电话',
    province       varchar(50)                          not null comment '省份',
    city           varchar(50)                          not null comment '城市',
    district       varchar(50)                          not null comment '区/县',
    detail_address varchar(255)                         not null comment '详细地址',
    tag            varchar(20)                          null comment '地址标签（如家、公司、学校等）',
    is_default     tinyint(1)                           null,
    created_at     timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at     timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted     tinyint(1) default 0                 not null comment '软删除标记'
)
    comment '用户外卖地址表';

create table Shishangke.menu
(
    menu_id     bigint auto_increment comment '菜单项ID，主键，自增'
        primary key,
    merchant_id bigint                                                                                                                                         not null comment '所属商家ID（外键关联 merchant.id）',
    name        varchar(100)                                                                                                                                   not null comment '菜品名称',
    description text                                                                                                                                           null comment '菜品描述',
    price       decimal(10, 2)                                                                                                                                 not null comment '菜品价格（单位：元）',
    category    enum ('HOT_SALE', 'SPECIALTY', 'STAPLE_FOOD', 'SNACK', 'SOUP', 'COLD_DISH', 'HOT_DISH', 'DESSERT', 'BEVERAGE', 'ALCOHOL', 'SET_MEAL', 'OTHER') not null comment '菜品分类',
    image_url   varchar(255)                                                                                                                                   null comment '菜品图片URL',
    status      enum ('ENABLED', 'DISABLED', 'SOLD_OUT') default 'ENABLED'                                                                                     not null comment '菜品状态',
    sort_order  int                                      default 0                                                                                             not null comment '排序权重（数值越小越靠前）',
    created_at  timestamp                                default CURRENT_TIMESTAMP                                                                             not null comment '创建时间',
    updated_at  timestamp                                default CURRENT_TIMESTAMP                                                                             not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted  tinyint(1)                               default 0                                                                                             not null comment '软删除标记'
)
    comment '菜单表';

create index idx_merchant_id
    on Shishangke.menu (merchant_id);

create table Shishangke.merchant
(
    id             bigint                                                                                                 not null comment '商家ID，主键，自增'
        primary key,
    user_id        bigint                                                                                                 not null,
    name           varchar(100)                                                                                           not null comment '商家名称',
    logo           varchar(255)                                                                                           null comment '商家logo图片URL',
    phone          varchar(20)                                                                                            not null comment '商家联系电话',
    province       varchar(50)                                                                                            null comment '省份',
    city           varchar(50)                                                                                            null comment '城市',
    district       varchar(50)                                                                                            null comment '区/县',
    street         varchar(100)                                                                                           null comment '街道',
    address_detail varchar(255)                                                                                           null comment '详细地址',
    open_time      time                                                                         default '00:00:00'        not null comment '每日开店时间，如09:00',
    close_time     time                                                                         default '00:00:00'        not null comment '每日关店时间，如22:00',
    description    text                                                                                                   null comment '商家描述信息',
    min_price      decimal(10, 2)                                                               default 0.00              null comment '最低起送价',
    status         enum ('OPEN', 'CLOSED', 'SUSPENDED', 'PENDING_REVIEW', 'REJECTED', 'BANNED') default 'PENDING_REVIEW'  not null comment '商家状态',
    create_time    datetime                                                                     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime                                                                     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted     tinyint(1)                                                                   default 0                 not null
);

-- 基础订单表保留共同字段
CREATE TABLE Shishangke.`base_order` (
                                         id BIGINT NOT NULL COMMENT '订单ID' PRIMARY KEY,
                                         order_type ENUM('DELIVERY', 'DINE_IN') NOT NULL COMMENT '订单类型',
                                         customer_id BIGINT NOT NULL COMMENT '用户ID',
                                         merchant_id BIGINT NOT NULL COMMENT '商家ID',
                                         merchant_name VARCHAR(100) NULL COMMENT '店铺名称',
                                         total_amount DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
                                         actual_amount DECIMAL(10, 2) NOT NULL COMMENT '实际支付金额',
                                         status ENUM ('PENDING', 'PAID', 'PREPARING', 'DELIVERING', 'COMPLETED', 'CANCELLED', 'REFUNDED', 'REFUNDING') DEFAULT 'PENDING' NOT NULL COMMENT '订单状态',
                                         remark VARCHAR(255) NULL COMMENT '订单备注',
                                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
                                         updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         is_deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除'
);

-- 外卖订单表
CREATE TABLE Shishangke.`delivery_order` (
                                             order_id BIGINT NOT NULL COMMENT '订单ID' PRIMARY KEY,
                                             receiver_name VARCHAR(50) NULL COMMENT '收货人姓名',
                                             receiver_phone VARCHAR(20) NULL COMMENT '收货人电话',
                                             receiver_address VARCHAR(255) NULL COMMENT '收货地址',
                                             delivery_fee DECIMAL(10, 2) DEFAULT 0.00 NOT NULL COMMENT '配送费',
                                             expected_delivery_time DATETIME NULL COMMENT '预计送达时间',
                                             actual_delivery_time DATETIME NULL COMMENT '实际送达时间',
                                             FOREIGN KEY (order_id) REFERENCES base_order(id)
);

-- 堂食订单表
CREATE TABLE Shishangke.`dine_in_order` (
                                            order_id BIGINT NOT NULL COMMENT '订单ID' PRIMARY KEY,
                                            table_number VARCHAR(20) NOT NULL COMMENT '桌号',
                                            seat_count INT NOT NULL COMMENT '就餐人数',
                                            check_in_time DATETIME NOT NULL COMMENT '入座时间',
                                            check_out_time DATETIME NULL COMMENT '结账时间',
                                            waiter_id BIGINT NULL COMMENT '服务员ID',
                                            FOREIGN KEY (order_id) REFERENCES base_order(id)
);

create index idx_created_at
    on Shishangke.`base_order` (created_at);

create index idx_customer_id
    on Shishangke.`base_order` (customer_id);

create index idx_merchant_id
    on Shishangke.`base_order` (merchant_id);

create index idx_status
    on Shishangke.`base_order` (status);

create table Shishangke.order_item
(
    id          bigint auto_increment comment '订单项ID'
        primary key,
    order_id    bigint                             not null comment '订单ID',
    menu_id     bigint                             not null comment '菜品ID',
    menu_name   varchar(100)                       not null comment '菜品名称',
    quantity    int                                not null comment '数量',
    price       decimal(10, 2)                     not null comment '单价',
    total_price decimal(10, 2)                     not null comment '总价',
    image_url   varchar(255)                       null comment '菜品图片',
    created_at  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '订单项表';

create index idx_menu_id
    on Shishangke.order_item (menu_id);

create index idx_order_id
    on Shishangke.order_item (order_id);

create table Shishangke.payment
(
    id             bigint                                            not null comment '支付ID'
        primary key,
    order_id       bigint                                            not null comment '订单ID',
    customer_id    bigint                                            not null comment '用户ID',
    payment_amount decimal(10, 2)                                    not null comment '支付金额',
    payment_method enum ('WECHAT', 'ALIPAY', 'CREDIT_CARD', 'CASH')  not null comment '支付方式',
    status         enum ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') not null comment '支付状态',
    payment_time   datetime                                          null comment '支付时间',
    created_at     datetime default CURRENT_TIMESTAMP                not null comment '创建时间',
    updated_at     datetime default CURRENT_TIMESTAMP                not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '支付表';

create index idx_customer_id
    on Shishangke.payment (customer_id);

create index idx_order_id
    on Shishangke.payment (order_id);

create index idx_payment_time
    on Shishangke.payment (payment_time);

create index idx_status
    on Shishangke.payment (status);

create index payment_order_id_index
    on Shishangke.payment (order_id);

create table Shishangke.refund
(
    id            bigint                                not null comment '退款ID'
        primary key,
    payment_id    bigint                                not null comment '支付ID',
    order_id      bigint                                not null comment '订单ID',
    customer_id   bigint                                not null comment '用户ID',
    refund_amount decimal(10, 2)                        not null comment '退款金额',
    reason        varchar(255)                          null comment '退款原因',
    status        enum ('PENDING', 'SUCCESS', 'FAILED') not null comment '退款状态',
    refund_time   datetime                              null comment '退款时间',
    created_at    datetime default CURRENT_TIMESTAMP    not null comment '创建时间',
    updated_at    datetime default CURRENT_TIMESTAMP    not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '退款表';

create index idx_customer_id
    on Shishangke.refund (customer_id);

create index idx_order_id
    on Shishangke.refund (order_id);

create index idx_payment_id
    on Shishangke.refund (payment_id);

create index idx_status
    on Shishangke.refund (status);

create table Shishangke.user
(
    id              bigint                                                                                                                   not null comment '主键ID'
        primary key,
    username        varchar(255)                                                                                                             not null comment '唯一用户名',
    nickname        varchar(255)                                                                                                             null comment '用户昵称',
    avatar          varchar(255)                          default 'https://img.icons8.com/?size=100&id=7WwZau6gMj6x&format=png&color=000000' not null comment '头像URL',
    email           varchar(255)                                                                                                             null comment '电子邮箱',
    phone           varchar(50)                                                                                                              null comment '电话号码',
    password        varchar(255)                                                                                                             not null comment '密码',
    status          enum ('ACTIVE', 'DISABLED', 'LOCKED') default 'ACTIVE'                                                                   not null comment '账号状态',
    role            enum ('ADMIN', 'BOSS', 'MANAGER', 'CHEF', 'CUSTOMER', 'DELIVERY')                                                        not null,
    last_login_time timestamp                                                                                                                null comment '最后登录时间',
    created_at      timestamp                             default CURRENT_TIMESTAMP                                                          not null comment '创建时间',
    updated_at      timestamp                             default CURRENT_TIMESTAMP                                                          not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted      tinyint(1)                            default 0                                                                          not null comment '软删除',
    constraint email
        unique (email),
    constraint phone
        unique (phone),
    constraint username
        unique (username)
)
    comment '用户表';

CREATE TABLE `shopping_cart`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `customer_id` bigint(20) NOT NULL COMMENT '用户ID',
    `merchant_id` bigint(20) NOT NULL COMMENT '商家ID',
    `created_at`  datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='购物车表';

CREATE TABLE `shopping_cart_item`
(
    `id`         bigint(20)     NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
    `cart_id`    bigint(20)     NOT NULL COMMENT '购物车ID',
    `menu_id`    bigint(20)     NOT NULL COMMENT '菜品ID',
    `quantity`   int(11)        NOT NULL DEFAULT '1' COMMENT '数量',
    `price`      decimal(10, 2) NOT NULL COMMENT '单价',
    `created_at` datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_cart_id` (`cart_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='购物车项表';


