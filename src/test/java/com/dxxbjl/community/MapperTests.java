package com.dxxbjl.community;

import com.dxxbjl.community.dao.DiscussPostMapper;
import com.dxxbjl.community.dao.LoginTicketMapper;
import com.dxxbjl.community.dao.MessageMapper;
import com.dxxbjl.community.dao.UserMapper;
import com.dxxbjl.community.entity.DiscussPost;
import com.dxxbjl.community.entity.LoginTicket;
import com.dxxbjl.community.entity.Message;
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

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

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

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("aaaaa");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() +1000*60*10));//当前时间往后10分钟

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("aaaaa");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("aaaaa",1);
        loginTicket = loginTicketMapper.selectByTicket("aaaaa");
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectLetters(){
         List<Message> list = messageMapper.selectConversations(111,0,20);
        for (Message message : list) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        List<Message> list2 = messageMapper.selectLetters("111_112", 0, 10);
        for(Message message : list2){
            System.out.println(list2);
        }

        int count2 = messageMapper.selectLetterCount("111_112");
        System.out.println(count2);

        int COUNT3 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(COUNT3);
    }
}
