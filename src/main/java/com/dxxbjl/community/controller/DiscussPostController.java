package com.dxxbjl.community.controller;

import com.dxxbjl.community.entity.Comment;
import com.dxxbjl.community.entity.DiscussPost;
import com.dxxbjl.community.entity.Page;
import com.dxxbjl.community.entity.User;
import com.dxxbjl.community.service.CommentService;
import com.dxxbjl.community.service.DiscussPostService;
import com.dxxbjl.community.service.UserService;
import com.dxxbjl.community.util.CommunityConstant;
import com.dxxbjl.community.util.CommunityUtil;
import com.dxxbjl.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 帖子相关
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    CommentService commentService;

    /**
     * 发布帖子
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(path = "/add" ,method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"你还没有登录!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserID(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //报错的情况，之后统一单独处理
        return CommunityUtil.getJSONString(0,"发布成功！");
    }

    /**
     * 帖子详情
     * @param discussPostId
     * @param model
     * @return 返回模版页
     */
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model , Page page){
        //查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);

        //查询作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //查评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" +discussPostId);
        page.setRows(post.getCommentCount());

        //评论：给帖子的评论
        //回复：给评论的评论

        //评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getoffset(), page.getLimit());

        //评论VO列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList != null){

            for (Comment comment : commentList) {
                //评论VO
                Map<String,Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                //回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                //回复Vo列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for (Comment reply :replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        //作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        replyVoList.add(replyVo);
                    }
                }

                commentVo.put("replys",replyVoList);
                
                //回复的数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount",replyCount);
                commentVoList.add(commentVo);
            }

        }

        model.addAttribute("comments",commentVoList);
        model.addAttribute("offset",page.getoffset());
        return "/site/discuss-detail";
    }
}
