package top.sharehome.springbootinittemplate.config.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import top.sharehome.springbootinittemplate.config.websocket.condition.WebSocketCondition;
import top.sharehome.springbootinittemplate.config.websocket.handler.TextWebSocketFrameHandler;
import top.sharehome.springbootinittemplate.config.websocket.properties.WebSocketProperties;

/**
 * Websocket服务器
 * todo 设计Bean创建之后和销毁之后要做的事儿，销毁需要关闭线程组之类的
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(WebSocketProperties.class)
@Slf4j
@Conditional(WebSocketCondition.class)
public class WebSocketServer {

    private EventLoopGroup bossGroup = null;

    private EventLoopGroup workerGroup = null;

    public WebSocketServer(WebSocketProperties webSocketProperties) {
        // 判断端口号是否合法
        if (webSocketProperties.getPort() > 65535 || webSocketProperties.getPort() < 0) {
            log.info("An invalid WebSocket port[{}] was configured, now 39999.", webSocketProperties.getPort());
            webSocketProperties.setPort(39999);
        }
        bossGroup = new NioEventLoopGroup(webSocketProperties.getBossThread());
        workerGroup = new NioEventLoopGroup(webSocketProperties.getWorkerThread());
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                // 设置服务端通道类型
                .channel(NioServerSocketChannel.class)
                // 配置服务器可连接队列的大小
                .option(ChannelOption.SO_BACKLOG, 128)
                // 配置workerGroup保持连接活动状态
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 配置bossGroup处理器
                .handler(new ChannelInitializer<NioServerSocketChannel>() {
                    @Override
                    protected void initChannel(NioServerSocketChannel nioServerSocketChannel) throws Exception {
                        nioServerSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                    }
                })
                // 配置workerGroup处理器
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        // 因为基于Http协议，使用Http编解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 因为是以块方式写，添加ChunkedWriteHandler处理器
                        pipeline.addLast(new ChunkedWriteHandler());
                            /*
                            HttpObjectAggregator处理器可以将多个块聚合起来，这就是为什么当浏览器发送大量数据时会发出多次Http请求的原因。
                            参数说明：
                            int maxContentLength 即单次聚合的最大长度
                             */
                        pipeline.addLast(new HttpObjectAggregator(8192));
                            /*
                            WebSocket 传递数据是以帧的形式进行传递，可以看到 WebSocketFrame 类中有六个子类，最常用的就是 TextWebSocketFrame 类，
                            浏览器请求时是以 ws://IP:PORT/xxx 的格式表示 URL，而这里添加 WebSocketServerProtocolHandler 对象主要是将 Http 协议升级为 WS 协议，保持长连接，
                            那么如何升级的呢？是因为服务器和 Web 客户端之前会提前隐式沟通，通过 101 状态码表示可切换传输协议。
                            参数说明：
                            String websocketPath 即 WebSocket 访问的基础路径，此时如果访问 ws://127.0.0.1:39999 是没办法进行 WebSocket 数据传输的，需要 ws://127.0.0.1:39999/websocket 才行，
                                                 如果没有按照要求访问，101 状态码就不会存在。
                             */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
                        // 添加自定义Handler来处理WebSocket数据
                        pipeline.addLast(new TextWebSocketFrameHandler());
                    }
                });
        // 异步监听服务器启动事件
        ChannelFuture bindFuture = serverBootstrap.bind(webSocketProperties.getPort());
        // 监听bindFuture绑定事件结果
        bindFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    log.info("WebSocket Server is RUNNING, Port is {}.", webSocketProperties.getPort());
                } else {
                    System.err.println(channelFuture.cause().getMessage());
                }
            }
        });
        // 异步监听关闭事件
        ChannelFuture closeFuture = bindFuture.channel().closeFuture();
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    log.info("WebSocket Server is SHUTTING.");
                } else {
                    System.err.println(channelFuture.cause().getMessage());
                }
            }
        });
    }

    @Bean(destroyMethod = "destroy")
    protected WebSocketServer startServer0() {
        return this;
    }

    public void destroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}