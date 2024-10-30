package top.sharehome.springbootinittemplate.config.websocket.spring.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.websocket.spring.condition.SpringWsCondition;
import top.sharehome.springbootinittemplate.service.AuthService;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * WebSocket处理器
 *
 * @author AntonyCheng
 */
@Component
@Slf4j
@Conditional(SpringWsCondition.class)
public class SpringWsHandler extends AbstractWebSocketHandler {

    /**
     * 获取Bean示例
     */
    private static final AuthService AUTH_SERVICE = SpringContextHolder.getBean(AuthService.class);

    /**
     * 日期格式
     */
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 链接成功后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("客户端与服务端已连接：{}", session.getId());
        super.afterConnectionEstablished(session);
    }

    /**
     * 处理接收到的文本消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = new String(message.asBytes(), StandardCharsets.UTF_8);
        log.info("服务器端收到消息：{}", msg);
        session.sendMessage(new TextMessage("[" + SIMPLE_DATE_FORMAT.format(new Date()) + "]" + " 服务端已收到：" + msg));
    }

    /**
     * 处理接收到的二进制消息
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
    }

    /**
     * 处理接收到的Pong消息（心跳监测）
     */
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        session.sendMessage(new PongMessage());
    }

    /**
     * 连接关闭后
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("客户端与服务端已断开连接：{}", session.getId());
    }

    /**
     * 处理WebSocket传输错误
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("发生异常：{}", exception.getMessage());
        session.close();
    }

    /**
     * 指示处理程序是否支持接收部分消息
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
