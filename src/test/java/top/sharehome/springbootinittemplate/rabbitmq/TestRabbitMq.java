package top.sharehome.springbootinittemplate.rabbitmq;

import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.utils.rabbitmq.RabbitMqUtils;
import top.sharehome.springbootinittemplate.utils.rabbitmq.model.RabbitMessage;

import java.time.Duration;

/**
 * 测试消息队列类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class TestRabbitMq {

    /**
     * 测试RabbitMqUtils
     */
    @Test
    void testRabbitMqUtils() throws InterruptedException {
//        RabbitMqUtils.defaultSendMsg("1", "1");
//        RabbitMessage rabbitMessage = RabbitMqUtils.defaultReceiveMsg();
//        System.out.println("rabbitMessage = " + rabbitMessage);
//        System.out.println("=====1=====");

        RabbitMqUtils.defaultSendMsgWithDlx("2", "22");
//        RabbitMessage rabbitMessage1 = RabbitMqUtils.defaultReceiveMsgWithDlx();
//        System.out.println("rabbitMessage1 = " + rabbitMessage1);
//        System.out.println("=====2=====");

        RabbitMqUtils.defaultSendMsgWithDlx("3", "333");
//        ThreadUtils.sleep(Duration.ofSeconds(20));
//        RabbitMessage rabbitMessage2 = RabbitMqUtils.defaultReceiveMsgWithDlx();
//        System.out.println("rabbitMessage2 = " + rabbitMessage2);
//        RabbitMessage rabbitMessage3 = RabbitMqUtils.defaultReceiveMsgWithDlxInDlx();
//        System.out.println("rabbitMessage3 = " + rabbitMessage3);
//        System.out.println("=====3=====");

//        RabbitMqUtils.defaultSendMsgWithDelay("4", "4444");
//        RabbitMessage rabbitMessage4 = RabbitMqUtils.defaultReceiveMsgWithDelay();
//        System.out.println("rabbitMessage4 = " + rabbitMessage4);
//        System.out.println("=====4=====");
    }

}