//==============定时类===============//
package com.example.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class MailScheduleService {

    private final Logger exportLogger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public JavaMailSenderImpl scheduleJavaMailSender;

    @Resource
    MailSendService scheduleMailSend;

    //定时执行
    @Scheduled(cron = "${com.config.time}")
    public void regularTimeExport(){
        try{
            scheduleMailSend.sendMail(scheduleJavaMailSender);
            exportLogger.info("定时发送成功，邮件服务结束");
        }catch (Exception e){
            exportLogger.error("定时发送失败，邮件服务结束",e);
        }

    }
}
