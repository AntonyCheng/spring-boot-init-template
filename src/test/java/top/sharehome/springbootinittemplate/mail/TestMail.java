package top.sharehome.springbootinittemplate.mail;

import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.config.mail.MailManager;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;

@SpringBootTest
public class TestMail {

    @Resource
    MailManager mailManager;

    private String to = "1911261716@qq.com";

    /**
     * 测试发送简单文本邮件
     *
     * @throws InterruptedException 由于发送邮件是多线程异步进行的，所以为了不打断邮件发送，测试时让主线程睡一会儿
     */
    @Test
    public void testSendSimpleText() throws InterruptedException {
        String subject = "标题：简单文本发送测试";
        String text = "Hello World";
        mailManager.sendSimpleText(to, subject, text);
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
        mailManager.sendSimpleText(to, subject, text);
        ThreadUtils.sleep(Duration.ofSeconds(10));
    }

    /**
     * 测试发送带有图片（来自客户端的图片）的HTML文本邮件
     *
     * @throws InterruptedException 由于发送邮件是多线程异步进行的，所以为了不打断邮件发送，测试时让主线程睡一会儿
     */
    @Test
    public void testSendWithImageHtmlFromBrowser() throws InterruptedException, IOException {
        String subject = "标题：带有图片（来自客户端的图片）的Html发送测试";
        String htmlContent = "<html><body>" +
                "<h1>Hello World</h1>" +
                //cid:是约定好的固定格式，只需要修改后面的变量
                "<image width='50' height='50' src='cid:gif1'>动图1 </image>" +
                "<image width='50' height='50' src='cid:pic2'>静图2 </image>" +
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
        mailManager.sendWithImageHtml(to, subject, htmlContent, cids, files);
        ThreadUtils.sleep(Duration.ofSeconds(10));
    }

    /**
     * 测试发送带有图片（来自服务器上的图片）的HTML文本邮件
     *
     * @throws InterruptedException 由于发送邮件是多线程异步进行的，所以为了不打断文件发送，让主线程睡一会儿
     */
    @Test
    public void testSendWithImageHtmlFromServer() throws InterruptedException {
        String subject = "标题：带有图片（来自服务器上的图片）的Html发送测试";
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
        mailManager.sendWithImageHtml(to, subject, htmlContent, cids, filePaths);
        ThreadUtils.sleep(Duration.ofSeconds(10));
    }

}
