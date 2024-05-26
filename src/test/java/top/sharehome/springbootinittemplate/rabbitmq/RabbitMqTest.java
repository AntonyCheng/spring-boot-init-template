package top.sharehome.springbootinittemplate.rabbitmq;

import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.utils.rabbitmq.RabbitMqUtils;
import top.sharehome.springbootinittemplate.utils.rabbitmq.model.RabbitMqMessage;

import java.time.Duration;

/**
 * 测试消息队列类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class RabbitMqTest {

    /**
     * 测试RabbitMqUtils
     */
    @Test
    void testRabbitMqUtils() throws InterruptedException {
        RabbitMqUtils.defaultSendMq("1", "1");
        RabbitMqMessage rabbitMqMessage = RabbitMqUtils.defaultReceiveMsg();
        System.out.println(rabbitMqMessage);
        System.out.println("=====1=====");

        RabbitMqUtils.defaultSendMqWithDlx("2", "22");
        RabbitMqMessage rabbitMqMessage1 = RabbitMqUtils.defaultReceiveMsgWithDlx();
        System.out.println(rabbitMqMessage1);
        System.out.println("=====2=====");

        RabbitMqUtils.defaultSendMqWithDlx("3", "333");
        ThreadUtils.sleep(Duration.ofSeconds(20));
        RabbitMqMessage rabbitMqMessage2 = RabbitMqUtils.defaultReceiveMsgWithDlx();
        System.out.println(rabbitMqMessage2);
        RabbitMqMessage rabbitMqMessage3 = RabbitMqUtils.defaultReceiveMsgWithDlxInDlx();
        System.out.println(rabbitMqMessage3);
        System.out.println("=====3=====");

        RabbitMqUtils.defaultSendMqWithDelay("4", "444");
        RabbitMqMessage rabbitMqMessage4 = RabbitMqUtils.defaultReceiveMsgWithDelay();
        System.out.println(rabbitMqMessage4);
        System.out.println("=====4=====");
    }

}