package com.joyo.dao.cache;

import com.joyo.dao.SeckillDao;
import com.joyo.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
/*告诉Junit 加载Spring整合配置*/
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;
    private long id = 1001;
    @Test
    public void getSeckill() throws Exception {
        Seckill seckill = redisDao.getSeckill(id);
        if (seckill == null){
            seckill = seckillDao.queryById(id);
            if (seckill!=null){
               String result =  redisDao.putSeckill(seckill);
                System.out.println(result);
                Seckill s = redisDao.getSeckill(id);
                System.out.println(s);
            }
        }
    }

    @Test
    public void putSeckill() throws Exception {
    }

}