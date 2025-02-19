package top.sharehome.springbootinittemplate.config.sse.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.sse.SseConfiguration;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * SSE工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class SseUtils {

    /**
     * SSE配置对象
     */
    private static final SseConfiguration SSE = SpringContextHolder.getBean(SseConfiguration.class);

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     */
    public static SseEmitter quickSseStream(Stream<String> messages) {
        return SSE.quickSseStream(messages);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     * @param timeout       SSE连接等待超时时间
     */
    public static SseEmitter quickSseStream(Stream<String> messages, Long timeout) {
        return SSE.quickSseStream(messages, timeout);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     */
    public static SseEmitter quickSseStrings(List<String> messages) {
        return SSE.quickSseStrings(messages);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     * @param timeout       SSE连接等待超时时间
     */
    public static SseEmitter quickSseStrings(List<String> messages, Long timeout) {
        return SSE.quickSseStrings(messages, timeout);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     */
    public static SseEmitter quickSseFlux(Flux<String> messages) {
        return SSE.quickSseFlux(messages);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     * @param timeout       SSE连接等待超时时间
     */
    public static SseEmitter quickSseFlux(Flux<String> messages, Long timeout) {
        return SSE.quickSseFlux(messages, timeout);
    }

    /**
     * 建立SSE连接
     */
    public static SseEmitter connect() {
        return SSE.connect();
    }

    /**
     * 建立SSE连接
     *
     * @param timeout   连接超时时间
     */
    public static SseEmitter connect(Long timeout) {
        return SSE.connect(timeout);
    }

    /**
     * 建立SSE连接
     *
     * @param userId    用户ID
     * @param token     用户登录会话Token
     */
    public static SseEmitter connect(Long userId, String token) {
        return SSE.connect(userId, token);
    }

    /**
     * 建立SSE连接
     *
     * @param userId    用户ID
     * @param token     用户登录会话Token
     * @param timeout   连接超时时间
     */
    public static SseEmitter connect(Long userId, String token, Long timeout) {
        return SSE.connect(userId, token, timeout);
    }

    /**
     * 切断SSE连接
     */
    public static void disconnect() {
        SSE.disconnect();
    }

    /**
     * 切断SSE连接
     *
     * @param userId 用户ID
     * @param token  用户登录会话Token
     */
    public static void disconnect(Long userId, String token) {
        SSE.disconnect(userId, token);
    }

    /**
     * 根据用户ID获取Sse连接
     *
     * @param userId 用户ID
     */
    public static Map<String, SseEmitter> getSseEmitter(Long userId) {
        return SSE.getSseEmitter(userId);
    }

    /**
     * 根据用户ID和用户登录会话Token获取Sse连接
     *
     * @param userId    用户ID
     * @param token     用户登录会话Token
     */
    public static SseEmitter getSseEmitter(Long userId, String token) {
        return SSE.getSseEmitter(userId, token);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public static void sendStreamToCurrentUser(Stream<String> messages) {
        SSE.sendStreamToCurrentUser(messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public static void sendStreamToCurrentToken(Stream<String> messages) {
        SSE.sendStreamToCurrentToken(messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param messages      消息内容
     */
    public static void sendStream(Long userId, Stream<String> messages) {
        SSE.sendStream(userId, messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     */
    public static void sendStream(Long userId, String token, Stream<String> messages) {
        SSE.sendStream(userId, token, messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    public static void sendStream(Long userId, String token, Stream<String> messages, Boolean isDisconnect) {
        SSE.sendStream(userId, token, messages, isDisconnect);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public static void sendStringsToCurrentUser(List<String> messages) {
        SSE.sendStringsToCurrentUser(messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public static void sendStringsToCurrentToken(List<String> messages) {
        SSE.sendStringsToCurrentToken(messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param messages      消息内容
     */
    public static void sendStrings(Long userId, List<String> messages) {
        SSE.sendStrings(userId, messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     */
    public static void sendStrings(Long userId, String token, List<String> messages) {
        SSE.sendStrings(userId, token, messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    public static void sendStrings(Long userId, String token, List<String> messages, Boolean isDisconnect) {
        SSE.sendStrings(userId, token, messages, isDisconnect);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public static void sendMessagesToCurrentUser(Flux<String> messages) {
        SSE.sendMessagesToCurrentUser(messages);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public static void sendFluxToCurrentToken(Flux<String> messages) {
        SSE.sendFluxToCurrentToken(messages);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param messages      消息内容
     */
    public static void sendFlux(Long userId, Flux<String> messages) {
        SSE.sendFlux(userId, messages);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     */
    public static void sendFlux(Long userId, String token, Flux<String> messages) {
        SSE.sendFlux(userId, token, messages);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    public static void sendFlux(Long userId, String token, Flux<String> messages, Boolean isDisconnect) {
        SSE.sendFlux(userId, token, messages, isDisconnect);
    }

}
