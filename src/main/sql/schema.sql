CREATE TABLE seckill(
  `seckill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` VARCHAR(120) NOT NULL COMMENT '商品名称',
  `number` INT NOT NULL COMMENT '库存数量',
  `start_time` TIMESTAMP NOT NULL  COMMENT '秒杀开启时间',
  `end_time` TIMESTAMP NOT NULL COMMENT '秒杀结束时间',
  `create_time` TIMESTAMP NOT NULL DEFAULT current_timestamp COMMENT '创建时间',
  PRIMARY KEY (seckill_id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_create_time(create_time)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 DEFAULT CHARSET = UTF8 COMMENT '秒杀库存表';

  /*初始化数据*/
insert into
  seckill(name,number,start_time,end_time)
values
('4000元秒杀iphoneX',100,'2018-10-29 00:00:00','2018-10-30 00:00:00'),
('4000元秒杀Macbook',100,'2018-10-29 00:00:00','2018-10-30 00:00:00'),
('4000元秒杀ipad',100,'2018-10-29 00:00:00','2018-10-30 00:00:00'),
('4000元秒杀Macbook pro',100,'2018-10-29 00:00:00','2018-10-30 00:00:00');

/*秒杀成功明细表*/
 CREATE TABLE success_seckill(
   `seckill_id` BIGINT NOT NULL COMMENT '秒杀商品id',
   `user_phone` BIGINT NOT NULL COMMENT '用户手机号码',
   `state`  TINYINT NOT NULL DEFAULT -1 COMMENT '状态标志',
   `create_time` TIMESTAMP NOT NULL COMMENT '创建时间',
   PRIMARY KEY (seckill_id,user_phone)
 )ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT = '秒杀成功明细表';

