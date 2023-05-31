package com.dxxbjl.community.service;

import com.dxxbjl.community.dao.MessageMapper;
import com.dxxbjl.community.entity.Message;
import com.dxxbjl.community.util.SensitiveFilter;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    //查询当前用户的会话列表，针对每个会话只返回一个最新的私信
    public List<Message> findConversations(int userId,int offset,int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    //查询当前用户的会话数量
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    //查询某个会话所包含的私信列表
    public List<Message> findLetters(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    //查询某个会话包含的私信数量
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    //查询未读私信的数量
    public int findLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    /**
     * 新增私信内容
     * @param message
     * @return
     */
    public int addMessage(Message message){
        //过滤
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    /**
     * 将消息变成已读
     * @param ids 消息中可能含有多条消息，用List集合
     * @return 直接修改消息的状态，1为已读
     */
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }

    /**
     * 查询某个主题下最新的通知
     * @param userId
     * @param topic
     * @return
     */
    public Message findLatestNotice(int userId,String topic){
        return messageMapper.selectLatestNotice(userId,topic);
    }

    /**
     * 查询某个主题所包含的通知的数量
     * @param userId
     * @param topic
     * @return
     */
    public int findNoticeCount(int userId, String topic){
        return messageMapper.selectNoticeCount(userId,topic);
    }

    /**
     *     查询未读的通知的数量
     * @param userId
     * @param topic
     * @return
     */
    public int findNoticeUnreadCount(int userId,String topic){
        return messageMapper.selectNoticeUnreadCount(userId,topic);
    }

    /**
     * 查询某个主题所包含的通知列表
     */
    public List<Message> findNotices(int userId,String topic,int offset,int limit){
        return messageMapper.selectNotices(userId,topic,offset,limit);
    }
}
