package com.dxxbjl.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
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

    //统计20万个重复数据的独立数据
    @Test
    public void testHyperLogLog(){
        String rediskey = "test:hll:01";
        for (int i = 1; i <= 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(rediskey,i);
        }

        for (int i = 1; i <= 100000 ; i++) {
            int r = (int) (Math.random() * 10000+1);
            redisTemplate.opsForHyperLogLog().add(rediskey,r);
        }

        long size = redisTemplate.opsForHyperLogLog().size(rediskey);
        System.out.println(size);
    }

    //将三组数据合并，再统计合并后的重读数据的独立总数
    @Test
    public void testHyperLogLogUnion(){
        String redisKey2 ="test:hll:02";
        for (int i = 1; i < 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2,i);
        }

        String redisKey3 ="test:hll:03";
        for (int i = 5001; i < 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3,i);
        }

        String redisKey4 ="test:hll:04";
        for (int i = 10001; i < 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4,i);
        }

        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey,redisKey2,redisKey3,redisKey4);
        long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);
    }

    //统计一组数据的布尔值(可以用来 判断是否签到)
    @Test
    public void testBitMap(){
        String redisKey = "test:bm:01";

        //记录
        redisTemplate.opsForValue().setBit(redisKey,1,true);
        redisTemplate.opsForValue().setBit(redisKey,4,true);
        redisTemplate.opsForValue().setBit(redisKey,7,true);

        //查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));

        //统计
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
    }

    //统计3组数据，并对这三组运算做OR运算
    @Test
    public void testBitMapOperation(){
        String redisKey2 = "test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey2,0,true);
        redisTemplate.opsForValue().setBit(redisKey2,1,true);
        redisTemplate.opsForValue().setBit(redisKey2,2,true);

        String redisKey3 = "test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey3,2,true);
        redisTemplate.opsForValue().setBit(redisKey3,3,true);
        redisTemplate.opsForValue().setBit(redisKey3,4,true);

        String redisKey4 = "test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey4,4,true);
        redisTemplate.opsForValue().setBit(redisKey4,5,true);
        redisTemplate.opsForValue().setBit(redisKey4,6,true);

        String redisKey = "test:bm:or";

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),redisKey2.getBytes(),
                        redisKey3.getBytes(),redisKey4.getBytes());

                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);

        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,6));
    }
}
