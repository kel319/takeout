-- 创建数据库
CREATE DATABASE IF NOT EXISTS `student` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `student`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户表主键ID',
    `username` VARCHAR(100) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '用户密码',
    `name` VARCHAR(100) COMMENT '姓名',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日期',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除的值: 0: 未删除  1: 删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`) COMMENT '用户名唯一索引',
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户信息表
CREATE TABLE IF NOT EXISTS `info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户信息表主键ID',
    `gender` TINYINT COMMENT '用户性别枚举,0男1女',
    `email` VARCHAR(100) COMMENT '用户电子邮箱',
    `phone` VARCHAR(20) COMMENT '用户电话号码',
    `user_id` INT NOT NULL COMMENT '用户表ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`) COMMENT '用户ID唯一索引',
    KEY `idx_phone` (`phone`),
    KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 菜品表
CREATE TABLE IF NOT EXISTS `dish` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜品表主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '菜品名',
    `price` DECIMAL(10,2) NOT NULL COMMENT '菜品价格',
    `inv` INT DEFAULT -1 COMMENT '菜品剩余库存(-1代表不限量)',
    `img` VARCHAR(500) COMMENT '菜品图片',
    `create_by` BIGINT COMMENT '创建人',
    `update_by` BIGINT COMMENT '更新人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '菜品信息创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '菜品信息更新时间',
    `status` TINYINT DEFAULT 1 COMMENT '菜品状态,0下架1上架',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品表';

-- 套餐表
CREATE TABLE IF NOT EXISTS `combo` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '套餐表主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '套餐名',
    `price` DECIMAL(10,2) NOT NULL COMMENT '套餐价格',
    `create_by` BIGINT COMMENT '创建人',
    `update_by` BIGINT COMMENT '更新人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
    `status` TINYINT DEFAULT 1 COMMENT '套餐状态',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='套餐表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_id` BIGINT NOT NULL COMMENT '订单号',
    `order_status` TINYINT DEFAULT 0 COMMENT '订单状态',
    `pay_time` DATETIME COMMENT '支付时间',
    `create_by` BIGINT COMMENT '创建人',
    `update_by` BIGINT COMMENT '更新人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
    `version` BIGINT DEFAULT 0 COMMENT '版本号,乐观锁用',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`) COMMENT '订单号唯一索引',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_pay_time` (`pay_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 购物车表
CREATE TABLE IF NOT EXISTS `shop_cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户表ID',
    `goods_type` TINYINT NOT NULL COMMENT '商品分类',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '商品数量',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品总价',
    `create_by` BIGINT COMMENT '创建人',
    `update_by` BIGINT COMMENT '更新人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_goods` (`user_id`, `goods_type`, `goods_id`) COMMENT '用户商品唯一索引',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_goods_type` (`goods_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 插入初始数据
-- 初始用户数据
INSERT IGNORE INTO `user` (`username`, `password`, `name`) VALUES
('admin', '$2a$10$xxxxxxxxxxxx', '管理员'),
('student1', '$2a$10$xxxxxxxxxxxx', '学生一'),
('student2', '$2a$10$xxxxxxxxxxxx', '学生二');

-- 初始用户信息数据
INSERT IGNORE INTO `info` (`gender`, `email`, `phone`, `user_id`) VALUES
(0, 'admin@wyk.com', '13800138000', 1),
(0, 'student1@wyk.com', '13800138001', 2),
(1, 'student2@wyk.com', '13800138002', 3);

-- 初始菜品数据
INSERT IGNORE INTO `dish` (`name`, `price`, `inv`, `img`, `status`) VALUES
('红烧肉', 25.00, 50, '/images/hongshaorou.jpg', 1),
('宫保鸡丁', 20.00, 30, '/images/gongbaojiding.jpg', 1),
('麻婆豆腐', 15.00, -1, '/images/mapodoufu.jpg', 1),
('清炒时蔬', 12.00, 20, '/images/qingchaoshishu.jpg', 1);

-- 初始套餐数据
INSERT IGNORE INTO `combo` (`name`, `price`, `status`) VALUES
('学生营养套餐A', 18.00, 1),
('学生营养套餐B', 22.00, 1),
('经济实惠套餐', 15.00, 1);

-- 创建索引后的一些示例数据
INSERT IGNORE INTO `order` (`user_id`, `order_id`, `order_status`) VALUES
(2, 2024000001, 0),
(3, 2024000002, 1);

-- 初始购物车数据
INSERT IGNORE INTO `shop_cart` (`user_id`, `goods_type`, `goods_id`, `quantity`, `price`) VALUES
(2, 1, 1, 2, 50.00),
(3, 1, 2, 1, 20.00);