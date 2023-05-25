package com.dxxbjl.community;

import com.dxxbjl.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text ="这里可以吸毒，嫖娼，开票";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        String text2 ="这里可以吸--毒，嫖--娼，开--票";
        text2 = sensitiveFilter.filter(text2);
        System.out.println(text2);
    }
}
