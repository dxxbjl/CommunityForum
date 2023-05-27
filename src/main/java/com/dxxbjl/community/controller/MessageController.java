package com.dxxbjl.community.controller;

import com.dxxbjl.community.dao.MessageMapper;
import com.dxxbjl.community.entity.Message;
import com.dxxbjl.community.entity.Page;
import com.dxxbjl.community.entity.User;
import com.dxxbjl.community.service.MessageService;
import com.dxxbjl.community.service.UserService;
import com.dxxbjl.community.util.CommunityUtil;
import com.dxxbjl.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring5.util.SpringRequestUtils;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model , Page page){

        User user = hostHolder.getUser();

        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        //会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getoffset(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversations != null){

            for (Message message : conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                //此会话中共多少条消息
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                //会话中的未读消息数量
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                //显示当前会话另外一个人的头像，目标对象
                int targetId = user.getId() == message.getFromId()?message.getToId():message.getFromId();
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        //查询未读消息数目
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){

        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //私信列表
        List<Message> lettersList = messageService.findLetters(conversationId, page.getoffset(), page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(lettersList !=null){
            for (Message message : lettersList){
                Map<String ,Object> map =new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);

        //查询私信的目标
        model.addAttribute("target",getLetterTarget(conversationId));

        //设置已读
        List<Integer> ids = getLetterIds(lettersList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if(hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getLetterIds (List<Message> letterList){
        List<Integer> ids = new ArrayList<>();

        if(letterList != null){
            for (Message message : letterList) {
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    /**
     * 发送私信
     * @param toName 发给的是某个人，接收到的是用户名，然后需要通过用户名获取用户id
     * @param content
     * @return
     */
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        User target = userService.findUserByName(toName);

        if(target == null){
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" +message.getToId());
        }else {
            message.setConversationId(message.getToId() + "_" +message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        //0是成功
        return CommunityUtil.getJSONString(0);
    }
}
