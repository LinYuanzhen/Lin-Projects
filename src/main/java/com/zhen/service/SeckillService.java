package com.zhen.service;

import com.zhen.dto.Exposer;
import com.zhen.dto.SeckillExecution;
import com.zhen.entity.Seckill;
import com.zhen.exception.RepeatKillException;
import com.zhen.exception.SeckillCloseException;
import com.zhen.exception.SeckillException;

import java.util.List;

/*业务接口：站在“使用者”角度设计接口
* 返回的类型都在dto
* 异常都在exception
* */
public interface SeckillService {
//    查询所有秒杀记录
    List<Seckill> getSeckillList();

//    查询单个秒杀记录
    Seckill getById(long seckillId);

/*    秒杀开启时输出秒杀接口地址，
    否则输出系统时间和秒杀时间*/
    Exposer exportSeckillUrl(long seckillId);

    //执行秒杀操作 by 存储过程
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
    throws SeckillException, RepeatKillException, SeckillCloseException;
}
