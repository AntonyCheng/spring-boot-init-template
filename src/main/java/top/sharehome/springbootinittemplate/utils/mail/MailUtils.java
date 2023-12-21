package top.sharehome.springbootinittemplate.utils.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.mail.MailManager;

/**
 * 网络工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class MailUtils {

    /**
     * 被封装的redisson客户端对象
     */
    private static final MailManager MAIL_MANAGER = SpringContextHolder.getBean(MailManager.class);

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param text    邮件内容
     */
    public static void sendSimpleText(String to, String subject, String text) {
        MAIL_MANAGER.sendSimpleText(to, subject, text);
    }

    /**
     * 发送HTML文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param html    邮件HTML内容
     */
    public static void sendWithHtml(String to, String subject, String html) {
        MAIL_MANAGER.sendWithHtml(to, subject, html);
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
    public static void sendWithImageHtmlByClient(String to, String subject, String html, String[] cids, MultipartFile[] files) {
        MAIL_MANAGER.sendWithImageHtmlByClient(to, subject, html, cids, files);
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
    public static void sendWithImageHtmlByServer(String to, String subject, String html, String[] cids, String[] filePaths) {
        MAIL_MANAGER.sendWithImageHtmlByServer(to, subject, html, cids, filePaths);
    }

    /**
     * 发送带有附件（来自客户端的附件）的邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param text    邮件HTML内容
     * @param files   客户端上传的文件
     * @param isHtml  是否使用HTML格式
     */
    public static void sendWithAttachmentByClient(String to, String subject, String text, MultipartFile[] files, Boolean isHtml) {
        MAIL_MANAGER.sendWithAttachmentByClient(to, subject, text, files, isHtml);
    }

    /**
     * 发送带有附件（来自服务器的附件）的邮件
     *
     * @param to        收件人邮箱
     * @param subject   邮件主题
     * @param text      邮件HTML内容
     * @param filePaths 服务器上文件的地址
     * @param isHtml    是否使用HTML格式
     */
    public static void sendWithAttachmentByServer(String to, String subject, String text, String[] filePaths, Boolean isHtml) {
        MAIL_MANAGER.sendWithAttachmentByServer(to, subject, text, filePaths, isHtml);
    }

}