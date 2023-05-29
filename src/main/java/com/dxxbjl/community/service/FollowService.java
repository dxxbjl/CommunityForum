package com.dxxbjl.community.service;

import com.dxxbjl.community.entity.User;
import com.dxxbjl.community.util.CommunityConstant;
import com.dxxbjl.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 关注
     * @param userId    进行操作的用户id
     * @param entityType 关注的实体的类型
     * @param entityId 实体Id
     */
    public void follow(int userId,int entityType,int entityId){
        //使用事务　在Redis中执行编程式事务
        //通过使用redisTemplate对象的execute方法，可以执行一个Redis会话回调。会话回调是一个接口，它定义了在Redis会话中执行的操作。
        //
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType); //目标
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);//粉丝

                //启用Redis事务。此行代码表示接下来的操作将被包含在一个事务中，保证原子性执行。
                operations.multi();

                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());//当前时间戳作为分数，添加到目标用户的关注列表中。
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());//当前时间戳作为分数，添加到实体对象的粉丝列表中。

                //提交Redis事务并返回事务执行的结果。
                return operations.exec();
            }
        });
    }

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void unfollow(int userId,int entityType,int entityId){
        //使用事务　在Redis中执行编程式事务
        //通过使用redisTemplate对象的execute方法，可以执行一个Redis会话回调。会话回调是一个接口，它定义了在Redis会话中执行的操作。
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType); //目标
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);//粉丝

                //启用Redis事务。此行代码表示接下来的操作将被包含在一个事务中，保证原子性执行。
                operations.multi();

                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);

                //提交Redis事务并返回事务执行的结果。
                return operations.exec();
            }
        });
    }

    //查询关注的实体的数量
    public long findFolloweeCount(int userId,int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);//统计数量
    }

     //查询实体的粉丝的数量
    public long findFollowerCount(int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询当前用户是否关注该实体
    public boolean hasFollowed(int userId,int entityType,int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId)!=null;
    }

    //查询某个用户关注的人
    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey,offset,offset+limit-1);

        if(targetIds == null){
            return null;
        }

        List<Map<String,Object>> list = new ArrayList<>();

        for (Integer targetId : targetIds) {
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    //查询某个用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if(targetIds == null){
            return null;
        }

        List<Map<String,Object>> list = new ArrayList<>();

        for (Integer targetId : targetIds) {
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}
