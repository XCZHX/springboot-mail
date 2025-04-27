package com.example.mail;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.annotation.Resource;

@SpringBootTest
class MailApplicationTests {

    private final Logger testLogger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MailConfig testConfig;

    @Autowired
    public JavaMailSenderImpl testJavaMailSender;

    @Resource
    MailSendService testMailSend;

    @Test
    void contextLoads() {
        testMailSend.sendMail(testJavaMailSender);
    }

}
