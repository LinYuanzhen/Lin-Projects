package com.zhen.dao;

import com.zhen.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
/*告诉junit spring配置文件*/
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void testinsertSuccessKilled() {
        int insertCount = successKilledDao.insertSuccessKilled(1000L,13486677967L);
        System.out.println("insertCount" + insertCount);
    }

    @Test
    public void testqueryByIdWithSeckill() {
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(1000L,13486677967L);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}