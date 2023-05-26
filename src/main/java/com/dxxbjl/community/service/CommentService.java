package com.dxxbjl.community.service;

import com.dxxbjl.community.dao.CommentMapper;
import com.dxxbjl.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 查找评论
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    public List<Comment> findCommentsByEntity(int entityType, int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    /**
     * 查找评论的数量
     * @param entityType
     * @param entityId
     * @return
     */
    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }
}
