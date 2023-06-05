package com.dxxbjl.community;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@org.springframework.boot.test.context.SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTest {

    @BeforeClass
    public static void beforeClass() {
        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("afterClass");
    }

    @Before
    public  void before(){
        System.out.println("before");
    }

    @After
    public  void after(){
        System.out.println("after");
    }

    @Test
    public void test1(){
        System.out.println("test1");
        //Assert.assertArrayEquals();
    }

    @Test
    public void test2(){
        System.out.println("test2");
    }
}
