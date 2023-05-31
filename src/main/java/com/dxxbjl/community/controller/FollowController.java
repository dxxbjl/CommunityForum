package com.dxxbjl.community.controller;

import com.dxxbjl.community.entity.Event;
import com.dxxbjl.community.entity.Page;
import com.dxxbjl.community.entity.User;
import com.dxxbjl.community.event.EventProducer;
import com.dxxbjl.community.service.FollowService;
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

import javax.jws.WebParam;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;


    /**
     * 关注
     * @param entityType
     * @param entityId
     * @return
     */
    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType , int entityId){
        //获取当前登录的用户
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType,entityId);

        //触发关注事件
        Event event =new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);


        return CommunityUtil.getJSONString(0,"已关注！");
    }

    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType , int entityId){
        //获取当前登录的用户
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType,entityId);

        return CommunityUtil.getJSONString(0,"已取关！");
    }

    /**
     *
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/followees/{userId}",method = RequestMethod.GET )
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user= userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int) followService.findFolloweeCount(userId,CommunityConstant.ENTITY_TYPE_USER));

        List<Map<String,Object>> userList = followService.findFollowees(userId,page.getoffset(),page.getLimit());
        if(userList !=null){
            for (Map<String,Object> map: userList) {
                User u = (User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",userList);

        return "/site/followee";
    }

    /**
     * 粉丝
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/followers/{userId}",method = RequestMethod.GET )
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user= userService.findUserById(userId);
        System.out.println("user:"+user);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followers/"+userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER,userId));

        List<Map<String,Object>> userList = followService.findFollowers(userId,page.getoffset(),page.getLimit());
        System.out.println("---");
        System.out.println(userList);
        if(userList !=null){
            for (Map<String,Object> map: userList) {
                User u = (User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",userList);
        System.out.println("******");
        System.out.println(userList);
        return "/site/follower";
    }

    private boolean hasFollowed(int userId){
        if(hostHolder.getUser() == null){
            return false;
        }

        return followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
    }

}
