package com.dxxbjl.community.controller;

import com.dxxbjl.community.entity.Comment;
import com.dxxbjl.community.entity.DiscussPost;
import com.dxxbjl.community.entity.Event;
import com.dxxbjl.community.event.EventProducer;
import com.dxxbjl.community.service.CommentService;
import com.dxxbjl.community.service.DiscussPostService;
import com.dxxbjl.community.util.CommunityConstant;
import com.dxxbjl.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;


    @Autowired
    private DiscussPostService discussPostService;


    /**
     * 增加评论
     * @param discussPostId 帖子id
     * @param comment
     * @return
     */
    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发评论事件
        Event event =new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if(comment.getEntityType() == ENTITY_TYPE_POST){
            //触发发帖事件
             event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/"+discussPostId;
    }
}
