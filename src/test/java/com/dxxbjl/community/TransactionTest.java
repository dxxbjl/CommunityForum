package com.dxxbjl.community;

import com.dxxbjl.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTest {

    @Autowired
    private AlphaService alphaService;

    /**
     * 程序会报错，主要检查数据有没有插入，有没有回滚
     */
    @Test
    public void testSave1(){
        Object object = alphaService.save1();
        System.out.println(object);
    }

    @Test
    public void testSave2(){
        Object object = alphaService.save2();
        System.out.println(object);
    }
}
