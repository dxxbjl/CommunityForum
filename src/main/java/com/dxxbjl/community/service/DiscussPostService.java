package com.dxxbjl.community.service;

import com.dxxbjl.community.controller.interceptor.LoginRequireInterceptor;
import com.dxxbjl.community.dao.DiscussPostMapper;
import com.dxxbjl.community.entity.DiscussPost;
import com.dxxbjl.community.util.SensitiveFilter;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    //Caffeine 核心接口：Cache,LoagingCahe,AsyncLoadingCache

    //帖子列表的缓存
    private LoadingCache<String ,List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer,Integer> postRowsCache;

    @PostConstruct
    public void init(){
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @NonNull
                    @Override
                    public @Nullable List<DiscussPost> load( @NonNull String key) throws Exception {
                       if(key == null || key.length() ==0){
                           throw new IllegalArgumentException("参数错误！");
                       }
                        String[] params = key.split(":");
                       if(params == null || params.length !=2){
                           throw new IllegalArgumentException("参数错误！");
                       }

                       int offset = Integer.valueOf(params[0]);
                       int limit = Integer.valueOf(params[1]);

                       //二级缓存 Redis -> mysql

                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });

        //初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @NonNull
                    @Override
                    public @Nullable Integer load( @NonNull Integer key) throws Exception {
                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }


    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit,int orderMode){
//        if(userId ==0 && orderMode == 1){
//            return postListCache.get(offset + ":" + limit);
//        }
        logger.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderMode);
    }

    public int findDiscussPostRows(int userId){
//        if(userId == 0 ){
//            return postRowsCache.get(userId);
//        }
        logger.debug("load post rows from DB.");

        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 增加帖子
     * @param post
     * @return  返回插入影响的行数
     */
    public int addDiscussPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //转译HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        //插入帖子数据
        return discussPostMapper.insertDiscussPost(post);
    }

    /**
     * 通过id查询帖子详情
     * @param id
     * @return
     */
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    /**
     * 更新评论的数量
     */
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    //更改帖子类型
    public int updateType(int id, int type){
        return discussPostMapper.updateType(id,type);
    }

    //更改帖子状态
    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id,status);
    }

    //更新帖子分数
    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id,score);
    }

}
