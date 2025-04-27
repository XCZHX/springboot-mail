package com.example.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class MailConfig {
    //获取application.properties的属性

    @Value("${spring.mail.username}")
    private String username;
    public String getUsername() {
        return username;
    }

    @Value("${com.config.sendto}")
    private String sendto;
    public String[] getSendto() {
        String[] sdt=sendto.split(";");
        return sdt;
    }

    @Value("${com.config.logpath}")
    private String logpath;
    public String getLogpath() {
        return logpath;
    }

    @Value("${com.config.filepath}")
    private String filepath;
    public String getFilepath() {
        return filepath;
    }

    @Value("${com.config.subject}")
    private String subject;
    public String getSubject() {
        return subject;
    }

    @Value("${com.config.text}")
    private String text;
    public String getText() {
        return text;
    }

    @Value("${com.config.time}")
    private String time;
    public String getTime() {
        String Time[]=time.split(":");
        return Time[2]+" "+Time[1]+" "+Time[0]+" ? * *";
    }

    @Value("${com.config.name}")
    private String name;
    public String getName(){
        return name;
    }

    @Value("${com.config.sendto_monitor}")
    private String sendto_monitor;
    public String[] getSendto_monitor() {
        String[] sdt=sendto_monitor.split(";");
        return sdt;
    }

    @Value("${com.config.subject_monitor}")
    private String subject_monitor;
    public String getSubject_monitor() {
        return subject_monitor;
    }

    @Value("${com.config.name_monitor}")
    private String name_monitor;
    public String getName_monitor() {
        return name_monitor;
    }

    @Value("${com.config.filetimeset}")
    private String filetimeset;
    public int getFiletimeset(){
        return Integer.parseInt(filetimeset);
    }
}
