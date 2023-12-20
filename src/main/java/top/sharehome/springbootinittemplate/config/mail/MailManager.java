package top.sharehome.springbootinittemplate.config.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.mail.async.MailAsyncMethod;
import top.sharehome.springbootinittemplate.config.mail.condition.MailCondition;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeMailException;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 邮件配置
 *
 * @author AntonyCheng
 */
@Service
@EnableAsync
@Slf4j
@Conditional(MailCondition.class)
public class MailManager {

    @Resource
    private MailProperties mailProperties;

    @Resource
    private MailAsyncMethod mailAsyncMethod;

    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 定义在邮件HTML中能够内嵌的图片后缀
     */
    private static final List<String> MAIL_PICTURE_SUFFIX = new ArrayList<String>() {
        {
            add("jpg");
            add("gif");
            add("png");
            add("bmp");
        }
    };

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param text    邮件内容
     */
    public void sendSimpleText(String to, String subject, String text) {
        // 判断参数是否为空
        if (StringUtils.isAnyBlank(to, subject, text)) {
            throw new CustomizeMailException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // 邮件发送方
        simpleMailMessage.setFrom(mailProperties.getUsername());
        // 邮件接收方
        simpleMailMessage.setTo(to);
        // 设置主题
        simpleMailMessage.setSubject(subject);
        // 设置内容
        simpleMailMessage.setText(text);
        // 异步发送邮件
        mailAsyncMethod.sendSimpleMail(simpleMailMessage);
    }

    /**
     * 发送HTML文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param html    邮件HTML内容
     */
    public void sendWithHtml(String to, String subject, String html) {
        // 判断参数是否为空
        if (StringUtils.isAnyBlank(to, subject, html)) {
            throw new CustomizeMailException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);
            // 邮件发送方
            mimeMessageHelper.setFrom(mailProperties.getUsername());
            // 邮件接收方
            mimeMessageHelper.setTo(to);
            // 设置主题
            mimeMessageHelper.setSubject(subject);
            // 设置内容，同时设置内容为HTML
            mimeMessageHelper.setText(html, true);
            // 异步发送邮件
            MimeMailMessage mimeMailMessage = new MimeMailMessage(mimeMessageHelper);
            mailAsyncMethod.sendMimeMail(mimeMailMessage);
        } catch (MessagingException e) {
            throw new CustomizeMailException(ReturnCode.EMAIL_WAS_SENT_ABNORMALLY);
        }
    }

    /**
     * 发送带有图片（来自客户端的图片）的HTML文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param html    邮件HTML内容
     * @param cids    CID是Content-ID的缩写，指将图片嵌入HTML邮件中，而不是以附件的形式发送。
     * @param files   服务器上图片地址，支持jpg、gif、png、bmp以及ico四种格式
     */
    public void sendWithImageHtml(String to, String subject, String html, String[] cids, MultipartFile[] files) {
        // 判断参数是否为空
        if (StringUtils.isAnyBlank(to, subject, html) || ObjectUtils.isEmpty(cids) || ObjectUtils.isEmpty(files)) {
            throw new CustomizeMailException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        // 判断cid是否和files数量匹配
        if (ObjectUtils.notEqual(cids.length, files.length)) {
            throw new CustomizeMailException(ReturnCode.PARAMETER_FORMAT_MISMATCH);
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            // 由于涉及到文件，所以需要设置允许上传文件
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            // 邮件发送方
            mimeMessageHelper.setFrom(mailProperties.getUsername());
            // 邮件接收方
            mimeMessageHelper.setTo(to);
            // 设置主题
            mimeMessageHelper.setSubject(subject);
            // 设置内容，同时设置内容为HTML
            mimeMessageHelper.setText(html, true);
            // 设置html中内联的图片
            for (int i = 0; i < cids.length; i++) {
                String contentType = StringUtils.isNotBlank(files[i].getContentType()) ? files[i].getContentType() : Constants.UNKNOWN_FILE_CONTENT_TYPE;
                String originalName = StringUtils.isNotBlank(files[i].getOriginalFilename()) ? files[i].getOriginalFilename() : files[i].getName();
                String suffix = FilenameUtils.getExtension(originalName);
                // 校验文件后缀名称被包含在要求之内
                if (StringUtils.isBlank(suffix) || !MAIL_PICTURE_SUFFIX.contains(suffix.toLowerCase())) {
                    throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_TYPE_MISMATCH);
                }
                byte[] fileBytes = files[i].getBytes();
                ByteArrayResource byteArrayResource = new ByteArrayResource(fileBytes);
                mimeMessageHelper.addInline(cids[i], byteArrayResource, contentType);
            }
            // 异步发送邮件
            MimeMailMessage mimeMailMessage = new MimeMailMessage(mimeMessageHelper);
            mailAsyncMethod.sendMimeMail(mimeMailMessage);
        } catch (MessagingException e) {
            throw new CustomizeMailException(ReturnCode.EMAIL_WAS_SENT_ABNORMALLY);
        } catch (IOException e) {
            throw new CustomizeMailException(ReturnCode.FILE_UPLOAD_EXCEPTION);
        }
    }

    /**
     * 发送带有图片（来自服务器的图片）的HTML文本邮件
     *
     * @param to        收件人邮箱
     * @param subject   邮件主题
     * @param html      邮件HTML内容
     * @param cids      CID是Content-ID的缩写，指将图片嵌入HTML邮件中，而不是以附件的形式发送。
     * @param filePaths 服务器上图片地址，支持jpg、gif、png、bmp以及ico四种格式
     */
    public void sendWithImageHtml(String to, String subject, String html, String[] cids, String[] filePaths) {
        // 判断参数是否为空
        if (StringUtils.isAnyBlank(to, subject, html) || ObjectUtils.isEmpty(cids) || ObjectUtils.isEmpty(filePaths)) {
            throw new CustomizeMailException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        // 判断cid是否和files数量匹配
        if (ObjectUtils.notEqual(cids.length, filePaths.length)) {
            throw new CustomizeMailException(ReturnCode.PARAMETER_FORMAT_MISMATCH);
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            // 由于涉及到文件，所以需要设置允许上传文件
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            // 邮件发送方
            mimeMessageHelper.setFrom(mailProperties.getUsername());
            // 邮件接收方
            mimeMessageHelper.setTo(to);
            // 设置主题
            mimeMessageHelper.setSubject(subject);
            // 设置内容，同时设置内容为HTML
            mimeMessageHelper.setText(html, true);
            // 设置html中内联的图片
            for (int i = 0; i < cids.length; i++) {
                File file = new File(filePaths[i]);
                // 校验路径是否为文件
                if (!file.isFile()) {
                    throw new CustomizeMailException(ReturnCode.SYSTEM_FILE_ADDRESS_IS_ABNORMAL);
                }
                // 校验文件后缀名称被包含在要求之内
                if (!MAIL_PICTURE_SUFFIX.contains(FilenameUtils.getExtension(file.getName()).toLowerCase())) {
                    throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_TYPE_MISMATCH);
                }
                mimeMessageHelper.addInline(cids[i], file);
            }
            // 异步发送邮件
            MimeMailMessage mimeMailMessage = new MimeMailMessage(mimeMessageHelper);
            mailAsyncMethod.sendMimeMail(mimeMailMessage);
        } catch (MessagingException e) {
            throw new CustomizeMailException(ReturnCode.EMAIL_WAS_SENT_ABNORMALLY);
        }
    }

    /**
     * 发送带有附件（来自服务器的图片）的邮件
     * todo 思考如何简化代码，比如是否使用HTML格式是否可以加载参数里
     *
     * @param to        收件人邮箱
     * @param subject   邮件主题
     * @param html      邮件HTML内容
     * @param filePaths 服务器上文件地址
     * @param isHtml    是否使用HTML格式
     */
    public void sendWithAttachment(String to, String subject, String html, String[] filePaths, Boolean isHtml) {
        // 判断参数是否为空
        if (StringUtils.isAnyBlank(to, subject, html) || ObjectUtils.isEmpty(filePaths)) {
            throw new CustomizeMailException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            // 由于涉及到文件，所以需要设置允许上传文件
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            // 邮件发送方
            mimeMessageHelper.setFrom(mailProperties.getUsername());
            // 邮件接收方
            mimeMessageHelper.setTo(to);
            // 设置主题
            mimeMessageHelper.setSubject(subject);
            // 设置内容
            mimeMessageHelper.setText(html, isHtml);
            // 设置html中内联的图片
            for (int i = 0; i < filePaths.length; i++) {
                File file = new File(filePaths[i]);
                // 校验路径是否为文件
                if (!file.isFile()) {
                    throw new CustomizeMailException(ReturnCode.SYSTEM_FILE_ADDRESS_IS_ABNORMAL);
                }
                String attachmentName = "attachment_" + i + "_" + file.getName();
                mimeMessageHelper.addAttachment(attachmentName, file);
            }
            // 异步发送邮件
            MimeMailMessage mimeMailMessage = new MimeMailMessage(mimeMessageHelper);
            mailAsyncMethod.sendMimeMail(mimeMailMessage);
        } catch (MessagingException e) {
            throw new CustomizeMailException(ReturnCode.EMAIL_WAS_SENT_ABNORMALLY);
        }
    }


}