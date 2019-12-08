package com.zhen.service;

import com.zhen.dto.Exposer;
import com.zhen.dto.SeckillExecution;
import com.zhen.entity.Seckill;
import com.zhen.exception.RepeatKillException;
import com.zhen.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() {
        List<Seckill> seckills = seckillService.getSeckillList();
        logger.info("list={}",seckills);
    }

    @Test
    public void testGetById() {
        long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

 /*   @Test
    public void testExportSeckillUrl() {
        long id = 1000L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);
    }

    @Test
    public void testExecuteSeckill() {
        long id = 1000L;
        long userPhone = 13486677970L;
        String md5 = "e522db07752aea391ecac4cbb5d43e43";
        SeckillExecution seckillExecution = seckillService.executeSeckill(id,userPhone,md5);
        logger.info("seckillExecution={}",seckillExecution);
    }*/
    //集成测试代码完整逻辑，注意可重复执行
    @Test
    public void testExportLogic() {
     long id = 1000L;
     Exposer exposer = seckillService.exportSeckillUrl(id);
     if (exposer.isExposed()) {
         logger.info("exposer={}", exposer);
         long userPhone = 13486677970L;
         String md5 = exposer.getMd5();
         try {
             SeckillExecution seckillExecution = seckillService.executeSeckill(id, userPhone, md5);
             logger.info("seckillExecution={}", seckillExecution);
         }catch (RepeatKillException e){
             logger.error(e.getMessage());
         }catch (SeckillCloseException e){
             logger.error(e.getMessage());
         }
     }else {
         //秒杀未开启或已结束
         logger.warn("exposer={}",exposer);
     }
 }

}