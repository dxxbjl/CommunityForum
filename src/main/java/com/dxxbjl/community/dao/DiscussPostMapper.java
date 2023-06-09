package com.dxxbjl.community.dao;

import com.dxxbjl.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     * 查询出帖子
     * offset：起始行号，limit：
     * */
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit,int orderMode);


    /**
     * 查询帖子的行数：
     * @Param 注解用于给参数取别名
     * 如果只有一个参数，并且要使用动态SQL，即sql中使用<if></if>,则必须加别名
     * */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 增加帖子
     * @param discussPost
     * @return 返回增加的行数
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 查询帖子详情
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     * 更新评论数量
     * @param id
     * @param commentCount
     * @return
     */
    int updateCommentCount(int id,int commentCount);

    //修改帖子类型
    int updateType(int id,int type);

    //修改帖子状态
    int updateStatus(int id,int status);

    int updateScore(int id,double score );
}
