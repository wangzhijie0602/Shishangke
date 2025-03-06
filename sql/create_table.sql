create table user
(
    id              bigint                                                                                          not null comment '主键ID'
        primary key,
    username        varchar(255)                                                                                    not null comment '唯一用户名',
    nickname        varchar(255)                                                                                    null comment '用户昵称',
    avatar          varchar(255) default 'https://img.icons8.com/?size=100&id=7WwZau6gMj6x&format=png&color=000000' not null comment '头像URL',
    email           varchar(255)                                                                                    null comment '电子邮箱',
    phone           varchar(50)                                                                                     null comment '电话号码',
    password        varchar(255)                                                                                    not null comment '密码',
    status          varchar(10)  default 'ENABLED'                                                                  not null comment '账号状态',
    role            varchar(10)  default 'USER'                                                                     not null,
    last_login_time timestamp                                                                                       null comment '最后登录时间',
    created_at      timestamp    default CURRENT_TIMESTAMP                                                          not null comment '创建时间',
    updated_at      timestamp    default CURRENT_TIMESTAMP                                                          not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted      tinyint(1)   default 0                                                                          not null comment '软删除',
    constraint email
        unique (email),
    constraint phone
        unique (phone),
    constraint username
        unique (username)
)
    comment '用户表';

