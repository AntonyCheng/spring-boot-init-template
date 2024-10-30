package top.sharehome.springbootinittemplate.config.websocket.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.websocket.netty.condition.NettyWsCondition;
import top.sharehome.springbootinittemplate.service.AuthService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * WebSocket处理器
 * 继承SimpleChannelInboundHandler以TextWebSocketFrame类型数据进行交互
 * 由于Netty框架和Spring框架中的元素有不同的生命周期，所以如果需要在Handler中使用Spring框架中的Bean，就需要使用SpringContextHolder.getBean()方法。
 *
 * @author AntonyCheng
 */
@Component
@Slf4j
@Conditional(NettyWsCondition.class)
public class NettyWsHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 获取Bean示例
     */
    private static final AuthService AUTH_SERVICE = SpringContextHolder.getBean(AuthService.class);

    /**
     * 日期格式
     */
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 处理Web客户端连接事件
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // id有两种形式：LongText（唯一的）和ShortText（不一定唯一）
        log.info("客户端与服务端已连接：{}", ctx.channel().id().asLongText());
    }

    /**
     * 处理可读就绪事件
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        log.info("服务器端收到消息：{}", msg.text());
        // 回复客户端
        ctx.channel().writeAndFlush(new TextWebSocketFrame("[" + SIMPLE_DATE_FORMAT.format(new Date()) + "]" + " 服务端已收到：" + msg.text()));
    }

    /**
     * 处理Web客户端断开连接事件
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.info("客户端与服务端已断开连接：{}", ctx.channel().id().asLongText());
    }

    /**
     * 处理发生异常事件
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("发生异常：{}", cause.getMessage());
        ctx.close();
    }
}
