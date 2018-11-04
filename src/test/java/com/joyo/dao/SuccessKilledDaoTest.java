package com.joyo.dao;

import com.joyo.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource private SuccessKilledDao successKilledDao;
    @Test
    public void insertSuccessKilled() throws Exception {
        Long seckillId = 1001L;
        Long userPhone = 13498234322L;
        int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
        System.out.println(insertCount);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long seckillId = 1001L;
        long userPhone = 13498234322L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }

}