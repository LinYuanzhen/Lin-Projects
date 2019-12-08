package com.zhen.service.impl;

import com.zhen.dao.SeckillDao;
import com.zhen.dao.SuccessKilledDao;
import com.zhen.dao.cache.RedisDao;
import com.zhen.dto.Exposer;
import com.zhen.dto.SeckillExecution;
import com.zhen.entity.Seckill;
import com.zhen.entity.SuccessKilled;
import com.zhen.enums.SeckillStateEnum;
import com.zhen.exception.RepeatKillException;
import com.zhen.exception.SeckillCloseException;
import com.zhen.exception.SeckillException;
import com.zhen.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //md5盐值字符串，用于混淆MD5
    private final String slat="sdhfkoajsh%*&%&*(+_)(+938457";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化，建立在超时的基础上维护一致性
        //1：访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        //redis里没有
        if (seckill == null) {
            //2：访问数据库
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //3:放入redis
                redisDao.putSeckill(seckill);
            }
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }
        //特定化字符串的过程，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    private String getMD5(long seckillId){
        String base = seckillId + "/" +slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());//二进制
        return md5;
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存+记录购买行为
        Date nowTime = new Date();

        try {
            /*将检测重复秒杀的行为放到前面，降低数据库压力*/
            //记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
            //唯一：seckillId,userPhone
            if(insertCount <= 0){
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            }else {
                //减库存，热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId,nowTime);
                if (updateCount <= 0){
                    //没有更新到记录（根据sql语句限定标准，可能是秒杀时间不符），rollback
                    throw new SeckillCloseException("seckill is closed");
                }else {
                    //秒杀成功，commit
                    SuccessKilled successKilled =successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                }
            }
        } catch (SeckillCloseException e1){
            throw e1;
        } catch (RepeatKillException e2){
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            //所有编译期异常 转化为运行期异常（出现异常就回滚，免得减库存和购买操作发生割裂）
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }
    }

}
