CREATE DATABASE seckill;
use seckill;

CREATE TABLE seckill(
seckill_id bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
name varchar(120) NOT NULL COMMENT '商品名称',
number int NOT NULL COMMENT '库存数量',
start_time timestamp NOT NULL COMMENT '秒杀开始时间',
end_time timestamp NOT NULL COMMENT '秒杀结束时间',
create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表'

/*初始化数据*/
insert into
    seckill(name,number,start_time,end_time)
values
    ('1000元秒杀iPhone6',100,'2019-11-21 00:00:00','2019-11-30 00:00:00'),
    ('500元秒杀ipad2',200,'2019-11-21 00:00:00','2019-11-30 00:00:00'),
    ('300元秒杀小米4',300,'2019-11-21 00:00:00','2019-11-30 00:00:00'),
    ('200元秒杀红米note',400,'2019-11-21 00:00:00','2019-11-30 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关信息
create table success_killed(
seckill_id bigint not null comment '秒杀商品id',
user_phone bigint not null comment '用户手机号',
state tinyint not null default -1 comment '状态标示：-1：无效 0：成功 1：已付款',
create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY(seckill_id,user_phone),/*联合主键,可起到防止同一用户重复秒杀同一商品的情况*/
key idx_create_time(create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表'

-- 连接数据库控制台
mysql -uroot -p
