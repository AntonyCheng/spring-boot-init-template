package top.sharehome.springbootinittemplate.config.mail.async;

import jakarta.annotation.Resource;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.mail.condition.MailCondition;

import java.util.Arrays;

/**
 * 邮件异步发送方法
 *
 * @author AntonyCheng
 */
@Component
@Async("mailAsyncExecutor")
@Slf4j
@Conditional(MailCondition.class)
public class MailAsyncMethod {

    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 发送简单文本邮件
     *
     * @param simpleMailMessage 简单文本邮件封装实体
     */
    public void sendSimpleMail(SimpleMailMessage simpleMailMessage) {
        log.info("Start sending emails ...");
        try {
            javaMailSender.send(simpleMailMessage);
            log.info("The message was successfully sent from {} to {}", simpleMailMessage.getFrom(), Arrays.toString(simpleMailMessage.getTo()));
        } catch (Exception e) {
            log.error("An error occurred in the message sent from {} to {} ==> {}", simpleMailMessage.getFrom(), Arrays.toString(simpleMailMessage.getTo()), e.getCause().toString());
        }
    }

    /**
     * 发送复杂附件邮件
     *
     * @param mimeMailMessage 复杂附件邮件封装实体
     */
    public void sendMimeMail(MimeMailMessage mimeMailMessage) {
        MimeMessage mimeMessage = mimeMailMessage.getMimeMessage();
        log.info("Start sending emails ...");
        String from = "";
        String to = "";
        try {
            from = mimeMessage.getFrom()[0].toString();
            to = Arrays.toString(mimeMessage.getRecipients(Message.RecipientType.TO));
            javaMailSender.send(mimeMessage);
            log.info("The message was successfully sent from {} to {}", from, to);
        } catch (Exception e) {
            log.error("An error occurred in the message sent from {} to {} ==> {}", from, to, e.getCause().toString());
        }
    }

}