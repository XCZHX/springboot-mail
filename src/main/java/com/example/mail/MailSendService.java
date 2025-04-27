//==============邮件发送类===============//
package com.example.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class MailSendService {

    @Autowired
    private MailConfig mailSendConfig;

    //@Autowired
    //JavaMailSender jms;

    private final Logger mailSendLogger = LoggerFactory.getLogger(this.getClass());

    //监控邮件发送
    public void sendMonitorMail(JavaMailSender javaMailSenderS,String setToAlone,String text) throws Exception{
        //新建MimeMessage
        MimeMessage message = javaMailSenderS.createMimeMessage();;

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(setToAlone);
        helper.setSubject(mailSendConfig.getSubject_monitor());
        helper.setText(text);

        String nick=javax.mail.internet.MimeUtility.encodeText(mailSendConfig.getName_monitor());
        helper.setFrom(new InternetAddress(nick+" <"+mailSendConfig.getUsername()+">"));

        //获取发送地址
        String getFrom="";
        Address[] mailfrom=message.getFrom();
        getFrom+=mailfrom[0].toString().split(" ")[1];

        //获取接受地址
        String getTo="";
        Address[] mailto=message.getRecipients(Message.RecipientType.TO);
        for(int i=0;i<mailto.length;i++){
            getTo+=mailto[i].toString();
            getTo+=";";
        }

        //发送邮件
        try{
            javaMailSenderS.send(message);
            mailSendLogger.info("发送成功");
            mailSendLogger.info(getFrom+" --> "+getTo+" : ~NOTICE");
        }catch (Exception e){
            mailSendLogger.error("发送失败",e);
            mailSendLogger.info(getFrom+" --> "+getTo+" : ~NOTICE");
        }
    }

    //发送监控邮件给多人
    //@RequestMapping("/asd")
    public void sendMailM(JavaMailSender javaMailSender,String text){

        String sendTo[] = mailSendConfig.getSendto_monitor();

        List<String> toList = new ArrayList();
        for(int i=0;i<sendTo.length;i++){
            if(!toList.contains(sendTo[i])){
                toList.add(sendTo[i]);
            }
        }

        for(int n=0;n<toList.size();n++){
            try{
                sendMonitorMail(javaMailSender,toList.get(n),text);
                mailSendLogger.info("------------------------------------------------------");
            }catch (Exception e){
                mailSendLogger.error("单独监控发送失败",e);
                mailSendLogger.info("------------------------------------------------------");
            }
        }
    }

    //发送邮件给多人
    //@RequestMapping("/atta")
    public void sendMail(JavaMailSenderImpl javaMailSender){
        int successCnt = 0;
        int failCnt = 0;

        String sendTo[] = mailSendConfig.getSendto();

        List<String> toList = new ArrayList();
        for(int i=0;i<sendTo.length;i++){
            if(!toList.contains(sendTo[i])){
                toList.add(sendTo[i]);
            }
        }

        SimpleDateFormat date_today = new SimpleDateFormat("yyyy-MM-dd");

        String dateFilePath = mailSendConfig.getFilepath()+"/"+date_today.format(new Date().getTime()+1000*60*60*24*mailSendConfig.getFiletimeset());

        mailSendLogger.info(dateFilePath);

        File attachArray[]=new File[]{};
        File attaFile = new File(dateFilePath);//获取路径
        if(!attaFile.exists()){
            sendMailM(javaMailSender,"附件文件夹地址不存在，请检查。");
            mailSendLogger.info("附件文件夹地址不存在，邮件服务结束");
            return;
        }else{
            attachArray = attaFile.listFiles();//用数组接收
        }

        if(attachArray.length==0){
            sendMailM(javaMailSender,"未找到附件，请检查附件文件夹。");
            mailSendLogger.info("附件不存在，邮件服务结束");
            return;
        }

        String logInfo="";

        for(int n=0;n<toList.size();n++){
            try{
                String logtext=sendAttachmentsMail(javaMailSender,toList.get(n),attachArray);
                logInfo+=logtext; //附件邮件
                logInfo+="\n";
                mailSendLogger.info("------------------------------------------------------");
                String strSuccess="success";
                if(logtext.split(" ")[2].equals(strSuccess)) successCnt++;
                else failCnt++;
            }catch (Exception e){
                mailSendLogger.error("单独发送失败",e);
                mailSendLogger.error("------------------------------------------------------");
                failCnt++;
            }
        }
        sendMailM(javaMailSender,"共计发送"+Integer.toString(successCnt+failCnt)+"封，成功"+Integer.toString(successCnt)+"封（success），失败"+Integer.toString(failCnt)+"封（其他参数）\n"+logInfo);
    }

    //带附件邮件发送
    public String sendAttachmentsMail(JavaMailSenderImpl javaMailSenderA, String setToAlone, File[] attachArray) throws Exception{
        //获取系统类型名
        //String systemName = System.getProperties().getProperty( "os.name" ).split(" ")[0];

        //创建mimeMessage
        MimeMessage mimeMessage = javaMailSenderA.createMimeMessage();

        //构造MimeMessageHelper，添加（除附件外）邮件配置
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(setToAlone);
        helper.setSubject(mailSendConfig.getSubject());
        helper.setText(mailSendConfig.getText());
        String nick=javax.mail.internet.MimeUtility.encodeText(mailSendConfig.getName());
        helper.setFrom(new InternetAddress(nick+" <"+mailSendConfig.getUsername()+">"));

        //添加附件及获取附件文件名
        String getAttach="";
        for (File fileSource : attachArray) {
            FileSystemResource file = new FileSystemResource(fileSource.toString());
            String fileName = file.getFilename();
            getAttach+=fileName;
            getAttach+=";";
            helper.addAttachment(fileName, file);
        }

        //获取发送地址
        String getFrom="";
        Address[] mailfrom=mimeMessage.getFrom();
        getFrom+=mailfrom[0].toString().split(" ")[1];

        //获取接受地址
        String getTo="";
        Address[] mailto=mimeMessage.getRecipients(Message.RecipientType.TO);
        for(int i=0;i<mailto.length;i++){
            getTo+=mailto[i].toString();
            getTo+=";";
        }

        String logtext="";

        //发送邮件
        try{
            javaMailSenderA.send(mimeMessage);
            logtext+=newLog("success",getFrom,getTo,getAttach);
            mailSendLogger.info("发送成功");
            mailSendLogger.info(getFrom+" --> "+getTo+" : "+getAttach);
        }catch (Exception e){
            String[] eMessage=e.getMessage().split("\n");
            logtext+=newLog(eMessage[0],getFrom,getTo,getAttach);
            mailSendLogger.error("发送失败",e);
            mailSendLogger.error(getFrom+" --> "+getTo+" : "+getAttach);
        }

        return logtext;

    }

    //写日志
    public void logWriter(String content) {
        //日志保存地址
        SimpleDateFormat date_log = new SimpleDateFormat("yyyy-MM-dd");
        String saveFile = mailSendConfig.getLogpath()+"/"+date_log.format(new Date())+".txt";
        File logFile = new File(saveFile);

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;


        try {
            if (!logFile.exists()) {       //若文件不存在，创建文件
                boolean hasFile = logFile.createNewFile();
                if(hasFile){
                    //System.out.println("file not exists, create new file");
                }
                fos = new FileOutputStream(logFile);
            } else {
                //System.out.println("file exists");
                fos = new FileOutputStream(logFile, true);
            }

            osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(content);         //写入内容
            osw.write("\r\n");      //换行

        } catch (Exception e) {
            e.printStackTrace();
        } finally {                     //关闭流
            try {
                if (osw != null) {
                    osw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //日志表达量修改
    public String newLog(String isSuccess, String getfrom, String getto, String getattach){

        SimpleDateFormat date_now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //时间 发送者 接收者 附件名
        String logText=date_now.format(new Date())+" "+isSuccess+" From:["+getfrom+"] To:["+getto+"] Attachment:["+getattach+"]";

        //logWriter(logText);
        return logText;
    }

    public static String Decode(String inStr) {
        String En = utf8ToUnicode(inStr).replaceAll("\\\\u","%").toUpperCase();
        String de = URLDecoder.decode(En);
        return de;
    }

    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(myBuffer[i]);
            if(ub == Character.UnicodeBlock.BASIC_LATIN){
                //英文及数字等
                sb.append(myBuffer[i]);
            }else if(ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS){
                //全角半角字符
                int j = (int) myBuffer[i] - 65248;
                sb.append((char)j);
            }else{
                //汉字
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u"+hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }

}
