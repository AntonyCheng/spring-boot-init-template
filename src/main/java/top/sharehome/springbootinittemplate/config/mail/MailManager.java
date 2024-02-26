package top.sharehome.springbootinittemplate.config.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.mail.async.MailAsyncMethod;
import top.sharehome.springbootinittemplate.config.mail.condition.MailCondition;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeMailException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮件配置
 *
 * @author AntonyCheng
 */
@Component
@EnableAsync
@Slf4j
@Conditional(MailCondition.class)
public class MailManager {

    /**
     * 定义在邮件HTML中能够内嵌的图片后缀
     */
    private static final Map<String, Long> MAIL_PICTURE_SUFFIX = new HashMap<String, Long>() {
        {
            put("jpg", 5 * 1024 * 1024L);
            put("gif", 5 * 1024 * 1024L);
            put("png", 5 * 1024 * 1024L);
            put("bmp", 5 * 1024 * 1024L);
            put("ico", 5 * 1024 * 1024L);
        }
    };
    /**
     * 定义邮件附件总大小不超过50MB
     */
    private static final Long MAIL_ATTACHMENT_SIZE = 50 * 1024 * 1024L;
    @Resource
    private MailProperties mailProperties;
    @Resource
    private MailAsyncMethod mailAsyncMethod;
    @Resource
    private JavaMailSender javaMailSender;

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
        // 校验邮箱格式
        if (!isValidEmail(to)) {
            throw new CustomizeMailException(ReturnCode.EMAIL_FORMAT_VERIFICATION_FAILED);
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
        // 校验邮箱格式
        if (!isValidEmail(to)) {
            throw new CustomizeMailException(ReturnCode.EMAIL_FORMAT_VERIFICATION_FAILED);
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
     * @param cids    CID是Content-ID的缩写，指将图片嵌入HTML邮件中，而不是以附件的形式发送，要求和files参数一一对应。
     * @param files   客户端上传的图片，支持jpg、gif、png、bmp以及ico五种格式，单张大小不超过5MB，总体大小不超过50MB。
     */
    public void sendWithImageHtmlByClient(String to, String subject, String html, String[] cids, MultipartFile[] files) {
        // 判断参数是否为空
        if (StringUtils.isAnyBlank(to, subject, html) || ObjectUtils.isEmpty(cids) || ObjectUtils.isEmpty(files)) {
            throw new CustomizeMailException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        // 校验邮箱格式
        if (!isValidEmail(to)) {
            throw new CustomizeMailException(ReturnCode.EMAIL_FORMAT_VERIFICATION_FAILED);
        }
        // 判断cid是否和files数量匹配
        int cidsRealLength = Arrays.stream(cids).filter(StringUtils::isNotBlank).toArray().length;
        int filesRealLength = Arrays.stream(files).filter(ObjectUtils::isNotEmpty).toArray().length;
        if (ObjectUtils.notEqual(cids.length, files.length) || ObjectUtils.notEqual(cidsRealLength, filesRealLength)) {
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
            // 设置文件总大小
            long fileTotalSize = 0L;
            // 设置html中内联的图片
            for (int i = 0; i < cids.length; i++) {
                String contentType = StringUtils.isNotBlank(files[i].getContentType()) ? files[i].getContentType() : Constants.UNKNOWN_FILE_CONTENT_TYPE;
                String originalName = StringUtils.isNotBlank(files[i].getOriginalFilename()) ? files[i].getOriginalFilename() : files[i].getName();
                String suffix = FilenameUtils.getExtension(originalName);
                Long needSize = MAIL_PICTURE_SUFFIX.get(suffix);
                // 校验文后缀名称被包含在要求之内
                if (StringUtils.isBlank(suffix) || ObjectUtils.isEmpty(needSize)) {
                    throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_TYPE_MISMATCH);
                }
                // 校验图片大小
                long fileSize = files[i].getSize();
                if (fileSize > needSize) {
                    throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE);
                }
                fileTotalSize += fileSize;
                byte[] fileBytes = files[i].getBytes();
                ByteArrayResource byteArrayResource = new ByteArrayResource(fileBytes);
                mimeMessageHelper.addInline(cids[i], byteArrayResource, contentType);
            }
            // 如果总的文件大小大于50M就不允许
            if (fileTotalSize > MAIL_ATTACHMENT_SIZE) {
                throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE);
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
     * @param cids      CID是Content-ID的缩写，指将图片嵌入HTML邮件中，而不是以附件的形式发送，要求和filePaths参数一一对应。
     * @param filePaths 服务器上图片的地址，支持jpg、gif、png、bmp以及ico五种格式，单张大小不超过5MB，总体大小不超过50MB。
     */
    public void sendWithImageHtmlByServer(String to, String subject, String html, String[] cids, String[] filePaths) {
        // 判断参数是否为空
        if (StringUtils.isAnyBlank(to, subject, html) || ObjectUtils.isEmpty(cids) || ObjectUtils.isEmpty(filePaths) || cids.length == 0 || filePaths.length == 0) {
            throw new CustomizeMailException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        // 校验邮箱格式
        if (!isValidEmail(to)) {
            throw new CustomizeMailException(ReturnCode.EMAIL_FORMAT_VERIFICATION_FAILED);
        }
        // 判断cid是否和files数量匹配
        int cidsRealLength = Arrays.stream(cids).filter(StringUtils::isNotBlank).toArray().length;
        int filesRealLength = Arrays.stream(filePaths).filter(StringUtils::isNotBlank).toArray().length;
        if (ObjectUtils.notEqual(cids.length, filePaths.length) || ObjectUtils.notEqual(cidsRealLength, filesRealLength)) {
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
            // 设置文件总大小
            long fileTotalSize = 0L;
            for (int i = 0; i < cids.length; i++) {
                File file = new File(filePaths[i]);
                // 校验路径是否为文件
                if (!file.isFile()) {
                    throw new CustomizeMailException(ReturnCode.SYSTEM_FILE_ADDRESS_IS_ABNORMAL);
                }
                String suffix = FilenameUtils.getExtension(file.getName()).toLowerCase();
                Long needSize = MAIL_PICTURE_SUFFIX.get(suffix);
                // 校验文后缀名称被包含在要求之内
                if (StringUtils.isBlank(suffix) || ObjectUtils.isEmpty(needSize)) {
                    throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_TYPE_MISMATCH);
                }
                // 校验图片大小
                long fileSize = file.length();
                if (fileSize > needSize) {
                    throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE);
                }
                fileTotalSize += fileSize;
                mimeMessageHelper.addInline(cids[i], file);
            }
            // 如果总的文件大小大于50M就不允许
            if (fileTotalSize > MAIL_ATTACHMENT_SIZE) {
                throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE);
            }
            // 异步发送邮件
            MimeMailMessage mimeMailMessage = new MimeMailMessage(mimeMessageHelper);
            mailAsyncMethod.sendMimeMail(mimeMailMessage);
        } catch (MessagingException e) {
            throw new CustomizeMailException(ReturnCode.EMAIL_WAS_SENT_ABNORMALLY);
        }
    }

    /**
     * 发送带有附件（来自客户端的附件）的邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param text    邮件HTML内容
     * @param files   客户端上传的文件，文件单个大小和总体大小均不能超过50MB。
     * @param isHtml  是否使用HTML格式
     */
    public void sendWithAttachmentByClient(String to, String subject, String text, MultipartFile[] files, Boolean isHtml) {
        // 判断参数是否为空
        if (StringUtils.isAnyBlank(to, subject, text) || ObjectUtils.anyNull(files, isHtml) || files.length == 0) {
            throw new CustomizeMailException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        // 校验邮箱格式
        if (!isValidEmail(to)) {
            throw new CustomizeMailException(ReturnCode.EMAIL_FORMAT_VERIFICATION_FAILED);
        }
        // 参数内部不能为空值
        if (Arrays.stream(files).filter(ObjectUtils::isNotEmpty).toArray().length == 0) {
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
            // 设置内容
            mimeMessageHelper.setText(text, isHtml);
            // 设置文件总大小
            long fileTotalSize = 0L;
            // 设置附件信息
            for (int i = 0; i < files.length; i++) {
                String originalName = StringUtils.isNotBlank(files[i].getOriginalFilename()) ? files[i].getOriginalFilename() : files[i].getName();
                long fileSize = files[i].getSize();
                // 单个附件不允许大于50M
                if (fileSize > MAIL_ATTACHMENT_SIZE) {
                    throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE);
                }
                fileTotalSize += fileSize;
                byte[] bytes = files[i].getBytes();
                ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
                String attachmentName = "attachment_" + i + "_" + originalName;
                mimeMessageHelper.addAttachment(attachmentName, byteArrayResource);
            }
            // 如果总的文件大小大于50M就不允许
            if (fileTotalSize > MAIL_ATTACHMENT_SIZE) {
                throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE);
            }
            // 异步发送邮件
            MimeMailMessage mimeMailMessage = new MimeMailMessage(mimeMessageHelper);
            mailAsyncMethod.sendMimeMail(mimeMailMessage);
        } catch (MessagingException e) {
            throw new CustomizeMailException(ReturnCode.EMAIL_WAS_SENT_ABNORMALLY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送带有附件（来自服务器的附件）的邮件
     *
     * @param to        收件人邮箱
     * @param subject   邮件主题
     * @param text      邮件HTML内容
     * @param filePaths 服务器上文件的地址，文件单个大小和总体大小均不能超过50MB。
     * @param isHtml    是否使用HTML格式
     */
    public void sendWithAttachmentByServer(String to, String subject, String text, String[] filePaths, Boolean isHtml) {
        // 判断参数是否为空
        if (StringUtils.isAnyBlank(to, subject, text) || ObjectUtils.anyNull(filePaths, isHtml) || filePaths.length == 0) {
            throw new CustomizeMailException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        // 校验邮箱格式
        if (!isValidEmail(to)) {
            throw new CustomizeMailException(ReturnCode.EMAIL_FORMAT_VERIFICATION_FAILED);
        }
        // 参数内部不能为空值
        if (Arrays.stream(filePaths).filter(StringUtils::isNotBlank).toArray().length == 0) {
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
            // 设置内容
            mimeMessageHelper.setText(text, isHtml);
            // 设置文件总大小
            long fileTotalSize = 0L;
            // 设置附件信息
            for (int i = 0; i < filePaths.length; i++) {
                File file = new File(filePaths[i]);
                // 校验路径是否为文件
                if (!file.isFile()) {
                    throw new CustomizeMailException(ReturnCode.SYSTEM_FILE_ADDRESS_IS_ABNORMAL);
                }
                long fileSize = file.length();
                // 单个附件不允许大于50M
                if (fileSize > MAIL_ATTACHMENT_SIZE) {
                    throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE);
                }
                fileTotalSize += fileSize;
                String attachmentName = "attachment_" + i + "_" + file.getName();
                mimeMessageHelper.addAttachment(attachmentName, file);
            }
            // 如果总的文件大小大于50M就不允许
            if (fileTotalSize > MAIL_ATTACHMENT_SIZE) {
                throw new CustomizeMailException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE);
            }
            // 异步发送邮件
            MimeMailMessage mimeMailMessage = new MimeMailMessage(mimeMessageHelper);
            mailAsyncMethod.sendMimeMail(mimeMailMessage);
        } catch (MessagingException e) {
            throw new CustomizeMailException(ReturnCode.EMAIL_WAS_SENT_ABNORMALLY);
        }
    }

    /**
     * 邮件地址格式验证
     *
     * @param email 待验证的邮箱
     * @return 返回验证结果
     */
    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(Constants.REGEX_MAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}