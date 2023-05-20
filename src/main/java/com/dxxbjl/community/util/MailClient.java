package com.dxxbjl.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {
    //记录日志，以当前的类命名
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    //mail核心组件、Spring提供的
    @Autowired
    private JavaMailSender mailSender;

    //发件人  将key注入bean
    @Value("${spring.mail.username}")
    private String from;

    /**
     * @param to 发给谁
     * @param subject   发送主题
     * @param content   发送内容
     */
    public void sendMail(String to,String subject,String content){

        try{
            //创建一个MimeMessage对象，用于表示邮件消息。
            MimeMessage message = mailSender.createMimeMessage();
            //创建一个MimeMessageHelper对象，用于简化设置MimeMessage的过程。该对象将与上一步创建的MimeMessage对象相关联。
            MimeMessageHelper helper = new MimeMessageHelper(message);
            //设置发件人的电子邮件地址。
            helper.setFrom(from);
            //设置收件人的电子邮件地址。
            helper.setTo(to);
            //设置邮件的主题。
            helper.setSubject(subject);
            //设置邮件的内容。
            helper.setText(content,true);
            //使用mailSender对象发送邮件。mailSender是一个邮件发送器实例。
            mailSender.send(helper.getMimeMessage());
        }catch (MessagingException e){
            logger.error("发送邮件失败"+e.getMessage());
        }


    }
}
