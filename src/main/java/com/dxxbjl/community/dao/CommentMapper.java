package com.dxxbjl.community.dao;

import com.dxxbjl.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 查询评论
     * @param entityType
     * @param entityId
     * @param offset    分页
     * @param limit     显示行数
     * @return
     */
    List<Comment> selectCommentsByEntity(int entityType ,int entityId,int offset,int limit);

    /**
     * 查询评论的数量
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCountByEntity(int entityType,int entityId);

    /**
     * 增加评论
     */
    int insertComment(Comment comment);
}
