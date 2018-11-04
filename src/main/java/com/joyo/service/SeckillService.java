package com.joyo.service;

import com.joyo.dto.Exposer;
import com.joyo.dto.SeckillExecution;
import com.joyo.entity.Seckill;
import com.joyo.exception.RepeatKillException;
import com.joyo.exception.SeckillCloseException;
import com.joyo.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：站在“使用者”角度设计接口
 * 三个方面：方法定义粒度,参数，返回类型
 */
public interface SeckillService {
    /**
     * 查询所有的秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址
     * 否则是输出系统时间和秒杀时间
     * 防止他人绕过前台验证直接访问秒杀地址
     * @param seckillId
     */
    Exposer exportSeckillUrl (long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws SeckillCloseException
     * @throws RepeatKillException
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException,SeckillCloseException,RepeatKillException;

}
