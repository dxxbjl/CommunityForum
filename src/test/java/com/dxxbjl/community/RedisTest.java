package com.dxxbjl.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey ="test:count";

        redisTemplate.opsForValue().set(redisKey,1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHash(){
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"name","张三");

        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"name"));
    }

    @Test
    public void testList(){
        String redisKey ="test:ids";

        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);
        redisTemplate.opsForList().rightPush(redisKey,401);

        System.out.println(redisTemplate.opsForList().size(redisKey));//查看list的长度
        System.out.println(redisTemplate.opsForList().index(redisKey,0));//获取下标为0的元素
        System.out.println(redisTemplate.opsForList().range(redisKey,0,3));//获取从[0-3]的数据

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));//删除并返回左边第一个
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSets(){
        String redisKey = "test:teachers";
        redisTemplate.opsForSet().add(redisKey,"刘备","张飞","关羽","赵云","黄忠");

        System.out.println(redisTemplate.opsForSet().size(redisKey));//set的长度
        System.out.println(redisTemplate.opsForSet().members(redisKey));//查看set元素
        System.out.println(redisTemplate.opsForSet().pop(redisKey));//删除并随机返回一个元素
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testDortedSet(){
        String redisKey = "test:student";

        redisTemplate.opsForZSet().add(redisKey,"孙悟空",80);
        redisTemplate.opsForZSet().add(redisKey,"八戒",88);
        redisTemplate.opsForZSet().add(redisKey,"白龙马",68);
        redisTemplate.opsForZSet().add(redisKey,"唐僧",98);
        redisTemplate.opsForZSet().add(redisKey,"观音",100);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));//统计长度
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"八戒"));//查看某元素的得分
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"八戒"));//查看八戒的排名（倒叙 由大到小），返回的是索引
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,2));//倒叙取前三名

    }

    @Test
    public void testKeys(){

        redisTemplate.delete("test:user");

        System.out.println(redisTemplate.hasKey("test:user"));

        redisTemplate.expire("test:student",10, TimeUnit.SECONDS);
    }

    @Test
    //多次访问同一个Key
    public void testBoundOperation(){
        String redisKey ="test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    //在Redis中执行编程式事务 确保ACID
    @Test
    public void testTransactional(){
        //通过使用redisTemplate对象的execute方法，可以执行一个Redis会话回调。
        // 会话回调是一个接口，它定义了在Redis会话中执行的操作。
        Object obj= redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";

                //通过调用multi()方法，开始了一个Redis事务。
                // 在事务中，连续执行了三个操作，分别是将"张三"、"lisi"和"王五"添加到名为redisKey的集合中，
                // 使用opsForSet()方法获取集合操作对象，然后调用add()方法进行添加操作。
                operations.multi();

                operations.opsForSet().add(redisKey,"张三");
                operations.opsForSet().add(redisKey,"lisi");
                operations.opsForSet().add(redisKey,"王五");

                System.out.println(operations.opsForSet().members(redisKey));
                //最后，通过调用exec()方法提交事务，并将事务执行的结果返回。
                return operations.exec();
            }
        });
        System.out.println(obj);
    }
}
