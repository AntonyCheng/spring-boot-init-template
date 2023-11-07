package top.sharehome.springbootinittemplate.config.rabbitmq;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * 不带有死信队列的消息队列实例基类，作为某一个消息队列的父类而存在，必须继承，否则无法使用RabbitMqUtils工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class BaseCustomizeMq {

    /**
     * 日志输出
     */
    @PostConstruct
    public void init() {
        log.info(">>>>>>>>>>> rabbitmq instance init:{}.", this.getClass());
    }

}