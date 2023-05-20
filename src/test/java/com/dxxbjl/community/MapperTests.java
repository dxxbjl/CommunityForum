package com.dxxbjl.community;

import com.dxxbjl.community.dao.DiscussPostMapper;
import com.dxxbjl.community.dao.UserMapper;
import com.dxxbjl.community.entity.DiscussPost;
import com.dxxbjl.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(11);
        System.out.println(user);

        User guanyu = userMapper.selectByName("guanyu");
        System.out.println(guanyu);

        User user1 = userMapper.selectByEmail("nowcoder11@sina.com");
        System.out.println(user1);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123");
        user.setSalt("456");
        user.setEmail("test@11.com");
        user.setHeaderUrl("123");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void uodateUser(){
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

         rows = userMapper.updateHeader(149, "http:");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "dxxbjl");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts(){
        List<DiscussPost> list  = discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost post: list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
        System.out.println("test");
        System.out.println("test");
    }
}
