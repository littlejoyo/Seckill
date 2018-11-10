# 高并发商品秒杀系统

项目对于秒杀的处理核心：**库存的处理**

### 对于秒杀操作要处理的问题：
- 减库存没有记录购买明细
- 记录了购买明细但没有减去库存
- 也就是解决超卖以及少卖的情况

![业务过程](https://github.com/littlejoyo/PictureCache/blob/master/images/seckill/%E7%A7%92%E6%9D%80%E4%B8%9A%E5%8A%A1%E5%AE%9E%E6%96%BD%E8%BF%87%E7%A8%8B.png)

### 难点处理：多用户竞争秒杀对象。

### 初步解决的措施：使用Mysql事务管理+行级锁。
![行级锁](https://github.com/littlejoyo/PictureCache/blob/master/images/seckill/%E8%A1%8C%E7%BA%A7%E9%94%81%E6%96%B9%E6%A1%88.png)

### 对于秒杀操作对数据库的操作步骤：
- Start Ttransaction : 开启事务
- insert ：插入购买明细
- Update ：更新库存数量 （持有行级锁）
- Commit ： 提交事务

针对这个过程的优化思路：将insert放在update之前，减少update对行级锁的持有时间 

### 秒杀活动页面的流程设计
![秒杀流程](https://github.com/littlejoyo/PictureCache/blob/master/images/seckill/%E9%A1%B5%E9%9D%A2%E6%B5%81%E7%A8%8B%E9%80%BB%E8%BE%91.png)

### 秒杀接口隐藏和暴露以及对MD5的认证来解决用户私自引用接口和对接口进行篡改
![秒杀模块](https://github.com/littlejoyo/PictureCache/blob/master/images/seckill/%E7%A7%92%E6%9D%80%E6%A8%A1%E5%9D%97%E7%9A%84%E6%B5%81%E7%A8%8B.png)

### 使用存储过程进行优化
![存储过程](https://github.com/littlejoyo/PictureCache/blob/master/images/seckill/%E5%AD%98%E5%82%A8%E8%BF%87%E7%A8%8B%E4%BC%98%E5%8C%96.png)

### 项目各个层次设计到的技术重点
![](https://github.com/littlejoyo/PictureCache/blob/master/images/seckill/%E6%95%B0%E6%8D%AEdao%E5%B1%82.jpg)
![](https://github.com/littlejoyo/PictureCache/blob/master/images/seckill/%E4%B8%9A%E5%8A%A1%E5%B1%82.png)
![](https://github.com/littlejoyo/PictureCache/blob/master/images/seckill/web%E5%B1%82%E8%AE%BE%E8%AE%A1.png)
![](https://github.com/littlejoyo/PictureCache/blob/master/images/seckill/%E5%B9%B6%E5%8F%91%E4%BC%98%E5%8C%96.png)



