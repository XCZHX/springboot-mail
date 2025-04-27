package com.example.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.net.URLEncoder;

@RestController
public class Test {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    MailSendService mss;

    @Autowired
    JavaMailSenderImpl javaMailSender;

    @RequestMapping("/test")
    public String test(){
        mss.sendMail(javaMailSender);
        //logger.info("test");
        return "test";
    }
}
