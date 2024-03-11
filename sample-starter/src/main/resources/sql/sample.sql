#todo 你的建表语句,包含索引
-- 按照阿里提供的开发手册中建议单表500W数据或者文件2G就要考虑分库分表
-- 分表：根据id进行分表, 假如id是整形, 背景按2000W的订单量 创建4个表：order_0、order_1、order_2、order_3 取模4得到表id
-- 分库：根据user_id, 进行分库, 假如user_id是整形 取模分库数量得到库id

create table order_0
(
    id          varchar(32)      not null
        primary key,
    user_id     varchar(32)      null comment '购买人',
    sku_id      varchar(32)      null comment 'skuId',
    amount      tinyint          null comment '数量',
    money       decimal(12, 2)   null comment '购买金额',
    pay_time    datetime         null comment '支付时间',
    pay_status  varchar(32)      null comment '支付状态',
    del_flag    bit default b'0' null comment '删除标志（0代表存在 1代表删除）',
    create_by   varchar(32)      null comment '创建人',
    create_time datetime         null comment '创建时间',
    update_by   varchar(32)      null comment '修改人',
    update_time datetime         null comment '修改时间'
)
    comment '订单表' collate = utf8mb4_general_ci;

create index idx_user_id
    on order_0 (user_id);
create index idx_sku_id
    on order_0 (sku_id);


