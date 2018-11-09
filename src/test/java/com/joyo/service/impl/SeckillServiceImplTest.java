package com.joyo.service.impl;

import com.joyo.dto.Exposer;
import com.joyo.dto.SeckillExecution;
import com.joyo.entity.Seckill;
import com.joyo.exception.RepeatKillException;
import com.joyo.exception.SeckillCloseException;
import com.joyo.service.SeckillService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:/spring/spring-service.xml"})
public class SeckillServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;
    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getById() throws Exception {
        long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id = 1000L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);

        //md5:6195f505a5df2a0430f95d632aba576d
    }

    @Test
    public void executeSeckill() throws Exception {
        long id = 1000L;
        long phone = 15623849324L;
        String md5 = "6195f505a5df2a0430f95d632aba576d";
        SeckillExecution seckillExecution = seckillService.executeSeckill(id,phone,md5);
        logger.info("result={}",seckillExecution);
    }

    @Test
    public void testSeckillLogic() throws Exception
    {
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed())
        {
            logger.info("exposer={}",exposer);
            long phone = 13923439403L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id,phone,md5);
                logger.info("execution={}",execution);
            }catch (SeckillCloseException  e)
            {
                logger.error(e.getMessage());
            }
            catch (RepeatKillException e)
            {
                logger.error(e.getMessage());
            }
        }else {
            logger.warn("exposer{}",exposer);
        }
    }

    @Test
    public void executeSeckillProcedure(){
        long seckillId = 1000;
        long phone = 13234322345L;

        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            logger.info(execution.getStateInfo());
        }

    }



}