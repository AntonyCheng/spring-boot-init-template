package top.sharehome.springbootinittemplate.mail;

import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.utils.mail.MailUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;

@SpringBootTest
public class TestMail {

    private final String to = "1911261716@qq.com";

    /**
     * 测试发送简单文本邮件
     *
     * @throws InterruptedException 由于发送邮件是多线程异步进行的，所以为了不打断邮件发送，测试时让主线程睡一会儿
     */
    @Test
    public void testSendSimpleText() throws InterruptedException {
        String subject = "标题：简单文本发送测试";
        String text = "Hello World";
        MailUtils.sendSimpleText(to, subject, text);
        ThreadUtils.sleep(Duration.ofSeconds(10));
    }

    /**
     * 测试发送HTML文本邮件
     *
     * @throws InterruptedException 由于发送邮件是多线程异步进行的，所以为了不打断邮件发送，测试时让主线程睡一会儿
     */
    @Test
    public void testSendWithHtml() throws InterruptedException {
        String subject = "标题：HTML文本发送测试";
        String text = "<h1>Hello World</h1>";
        MailUtils.sendWithHtml(to, subject, text);
        ThreadUtils.sleep(Duration.ofSeconds(10));
    }

    /**
     * 测试发送带有图片（来自客户端的图片）的HTML文本邮件
     *
     * @throws InterruptedException 由于发送邮件是多线程异步进行的，所以为了不打断邮件发送，测试时让主线程睡一会儿
     */
    @Test
    public void testSendWithImageHtmlByClient() throws InterruptedException, IOException {
        String subject = "标题：带有图片（来自客户端的图片）的Html发送测试";
        String htmlContent = "<html><body>" +
                "<h1>Hello World</h1>" +
                //cid:是约定好的固定格式，只需要修改后面的变量
                "<image width='50' height='50' src='cid:gif1'>动图1</image>" +
                "<image width='50' height='50' src='cid:pic2'>静图2</image>" +
                "</body></html>";
        //数组中的cid要和上面html中image中的cid一致，否则图片将设置失败
        String[] cids = new String[]{"gif1", "pic2"};
        String projectRootPath = System.getProperty("user.dir");

        FileInputStream gif1 = new FileInputStream(projectRootPath + "/src/test/java/top/sharehome/springbootinittemplate/mail/picture/gif1.gif");
        FileInputStream pic2 = new FileInputStream(projectRootPath + "/src/test/java/top/sharehome/springbootinittemplate/mail/picture/pic2.png");
        MultipartFile[] files = new MultipartFile[]{
                new MockMultipartFile("gif1.gif", gif1),
                new MockMultipartFile("pic2.png", pic2)
        };
        MailUtils.sendWithImageHtmlByClient(to, subject, htmlContent, cids, files);
        ThreadUtils.sleep(Duration.ofSeconds(10));
    }

    /**
     * 测试发送带有图片（来自服务器上的图片）的HTML文本邮件
     *
     * @throws InterruptedException 由于发送邮件是多线程异步进行的，所以为了不打断文件发送，让主线程睡一会儿
     */
    @Test
    public void testSendWithImageHtmlByServer() throws InterruptedException {
        String subject = "标题：带有图片（来自服务器的图片）的Html发送测试";
        String htmlContent = "<html><body>" +
                "<h1>Hello World</h1>" +
                //cid:是约定好的固定格式，只需要修改后面的变量
                "<image width='50' height='50' src='cid:gif1'>动图1 </image>" +
                "<image width='50' height='50' src='cid:pic2'>静图2 </image>" +
                "</body></html>";
        //数组中的cid要和上面html中image中的cid一致，否则图片将设置失败
        String[] cids = new String[]{"gif1", "pic2"};
        String projectRootPath = System.getProperty("user.dir");

        String[] filePaths = new String[]{
                projectRootPath + "/src/test/java/top/sharehome/springbootinittemplate/mail/picture/gif1.gif",
                projectRootPath + "/src/test/java/top/sharehome/springbootinittemplate/mail/picture/pic2.png"
        };
        MailUtils.sendWithImageHtmlByServer(to, subject, htmlContent, cids, filePaths);
        ThreadUtils.sleep(Duration.ofSeconds(10));
    }

    /**
     * 测试发送带有附件（来自客户端的附件）的邮件
     *
     * @throws InterruptedException 由于发送邮件是多线程异步进行的，所以为了不打断邮件发送，测试时让主线程睡一会儿
     */
    @Test
    public void testSendWithAttachmentByClient() throws InterruptedException, IOException {
        String subject = "标题：带有附件（来自客户端的附件）的邮件发送测试";
        String htmlContent = "<html><body>" +
                "<h1>Hello World</h1>" +
                "</body></html>";
        String projectRootPath = System.getProperty("user.dir");
        FileInputStream file1 = new FileInputStream(projectRootPath + "/src/test/java/top/sharehome/springbootinittemplate/mail/file/file.zip");
        FileInputStream file2 = new FileInputStream(projectRootPath + "/src/test/java/top/sharehome/springbootinittemplate/mail/file/file.zip");
        MultipartFile[] files = new MultipartFile[]{
                new MockMultipartFile("file1.zip", file1),
                new MockMultipartFile("file2.zip", file2)
        };
        MailUtils.sendWithAttachmentByClient(to, subject, htmlContent, files, true);
        MailUtils.sendWithAttachmentByClient(to, subject, htmlContent, files, false);
        ThreadUtils.sleep(Duration.ofSeconds(10));
    }

    /**
     * 测试发送带有附件（来自服务器的附件）的邮件
     */
    @Test
    public void testSendWithAttachmentByServer() throws InterruptedException {
        String subject = "标题：带有附件（来自服务器的附件）的邮件发送测试";
        String htmlContent = "<html><body>" +
                "<h1>Hello World</h1>" +
                "</body></html>";
        String projectRootPath = System.getProperty("user.dir");
        String[] filePaths = new String[]{
                projectRootPath + "/src/test/java/top/sharehome/springbootinittemplate/mail/file/file.zip",
                projectRootPath + "/src/test/java/top/sharehome/springbootinittemplate/mail/file/file.zip",
        };
        MailUtils.sendWithAttachmentByServer(to, subject, htmlContent, filePaths, true);
        MailUtils.sendWithAttachmentByServer(to, subject, htmlContent, filePaths, false);
        ThreadUtils.sleep(Duration.ofSeconds(10));
    }

}
