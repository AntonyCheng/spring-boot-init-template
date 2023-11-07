package top.sharehome.springbootinittemplate.config.rabbitmq.base;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * 消息队列实例基类，作为某一个消息队列的父类而存在，必须继承，否则无法使用RabbitMqUtils工具类
 * todo 建议自定义消息队列实例时统一类名：xxxRabbitMq，同时类注解和类结构保持和customize/DemoRabbitMq一致，便于日志查看；
 *
 * @author AntonyCheng
 */
@Slf4j
public class BaseMq {

    /**
     * 日志输出
     */
    @PostConstruct
    public void init() {
        log.info(">>>>>>>>>>> rabbitmq instance init:{}.", this.getClass());
    }

}