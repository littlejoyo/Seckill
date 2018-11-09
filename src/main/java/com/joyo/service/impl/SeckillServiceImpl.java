package com.joyo.service.impl;

import com.joyo.dao.SeckillDao;
import com.joyo.dao.SuccessKilledDao;
import com.joyo.dao.cache.RedisDao;
import com.joyo.dto.Exposer;
import com.joyo.dto.SeckillExecution;
import com.joyo.entity.Seckill;
import com.joyo.entity.SuccessKilled;
import com.joyo.enums.SeckillStateEnum;
import com.joyo.exception.RepeatKillException;
import com.joyo.exception.SeckillCloseException;
import com.joyo.exception.SeckillException;
import com.joyo.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService{

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入service依赖
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    //MD5盐值字符串，用户混淆MD5，防止MD5被解密解析
    private final String salt = "sderekoemek^%&$#@$dfsdsd@5534GED";
    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public Seckill getById(long seckillId) {

        return   seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {

        //并发优化：redis缓存
        //首先先访问redis看有没有缓存，没有的话再去访问数据库
       Seckill seckill =  redisDao.getSeckill(seckillId);
       if (seckill==null){
           //再去访问数据库
         seckill = seckillDao.queryById(seckillId);
         if (seckill==null){
             return new Exposer(false,seckillId);
         }else{
             redisDao.putSeckill(seckill);
         }
       }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date currentTime = new Date();
        if (currentTime.getTime()<startTime.getTime()||currentTime.getTime()>endTime.getTime())
        {
            return  new Exposer(false,seckillId,currentTime,startTime,endTime);
        }

        //转换特定字符的过程，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    /**
     * 将seckillId进行MD5加密
     * @param seckillId
     * @return
     */
    private String getMD5(long seckillId)
    {
        String base = seckillId +"/"+salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /**
     * 使用注解控制事务方法的优点：
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他的网络操作，RPC/HTTP请求，
     * 如果真的需要网络操作，最好进行方法抽取独立运行
     * 3.不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制。
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, SeckillCloseException, RepeatKillException {
        if (md5 == null || !md5.equals(getMD5(seckillId)))
        {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存+记录购买行为
        Date currentTime = new Date();
        try {
            //记录购买的行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                //减去库存
                int updateCount = seckillDao.reduceNumber(seckillId, currentTime);
                if (updateCount <= 0) {
                    //没有更新记录，秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }

            }

        }catch (SeckillCloseException e1)
        {
            throw e1;
        }catch (RepeatKillException e2)
        {
            throw e2;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
            //所有编译器异常 转换为 运行时异常
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }

    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))){
            return  new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map =  new HashMap<String,Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);

        //执行存储过程执行秒杀操作
        try {
            seckillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map,"result",-2);
            if (result ==1){
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return  new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,sk);
            }else {
                return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
        }
    }


}
