package com.dxxbjl.community.service;

import com.dxxbjl.community.dao.AlphaDao;
import com.dxxbjl.community.dao.DiscussPostMapper;
import com.dxxbjl.community.dao.UserMapper;
import com.dxxbjl.community.entity.DiscussPost;
import com.dxxbjl.community.entity.User;
import com.dxxbjl.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/**
 * 示例
 */
@Service
public class AlphaService {
    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);
    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;


    /**
     * 声明式事务
     * @return
     */
    //propagation 事务传播的机制
    //REQUIRED：支持当前事务（调用者，外部事物），如果不存在则创建新事务
    //REQUIRES_NEW：创建一个新事物，并且暂停当前事务（外部事物）
    //NESTED：如果当前存在事务（外部事物），则嵌套在该事务中执行（独立的提交和回滚），否则会和REQUIRED一样
    //设置隔离级别
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123")+user.getSalt());
        user.setEmail("alpha@163.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserID(user.getId());
        post.setTitle("新人抱抱抱");
        post.setContent("Hello");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        //如果不添加事务，当在这个地方报错后，上面的插入语句仍能正常插入，不会回滚
        Integer.valueOf("abc");
        return "ok";
    }

    /**
     * 编程式事务
     * @return
     */
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {

                //新增用户
                User user = new User();
                user.setUsername("bete");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123")+user.getSalt());
                user.setEmail("bete@163.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/99.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserID(user.getId());
                post.setTitle("新人bete");
                post.setContent("betebete");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                //如果不添加事务，当在这个地方报错后，上面的插入语句仍能正常插入，不会回滚
                Integer.valueOf("abc");

                return "ok";
            }
        });
    }

    //让该方法在多线程环境下，被异步的调用
   @Async
   public void executel(){
        logger.debug("execute1");
   }

   @Scheduled(initialDelay = 10000,fixedRate =  10000)
   public void execute2(){
       logger.debug("execute2");
   }
}
