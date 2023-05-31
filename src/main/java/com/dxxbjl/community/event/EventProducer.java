package com.dxxbjl.community.event;

import com.alibaba.fastjson.JSONObject;
import com.dxxbjl.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件 - 发送消息
    public void fireEvent(Event event){
        //将事件发布到指定的主题   // 发送的消息是一个JSON字符串
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
