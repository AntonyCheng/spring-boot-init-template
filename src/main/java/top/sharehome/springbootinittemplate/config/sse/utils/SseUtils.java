package top.sharehome.springbootinittemplate.config.sse.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.sse.SseConfiguration;
import top.sharehome.springbootinittemplate.config.sse.entity.SseMessage;

import java.util.List;
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
     * @param timeout       SSE连接超时时间
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
     * @param timeout       SSE连接超时时间
     */
    public static SseEmitter quickSseStrings(List<String> messages, Long timeout) {
        return SSE.quickSseStrings(messages, timeout);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     */
    public static SseEmitter quickSseMessages(List<SseMessage> messages) {
        return SSE.quickSseMessages(messages);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     * @param timeout       SSE连接超时时间
     */
    public static SseEmitter quickSseMessages(List<SseMessage> messages, Long timeout) {
        return SSE.quickSseMessages(messages, timeout);
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
     * @param userId 用户ID
     * @param token  用户登录会话Token
     */
    public static SseEmitter connect(Long userId, String token) {
        return SSE.connect(userId, token);
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
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public static void sendMessagesToCurrentUser(List<SseMessage> messages) {
        SSE.sendMessagesToCurrentUser(messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public static void sendMessagesToCurrentToken(List<SseMessage> messages) {
        SSE.sendMessagesToCurrentToken(messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param messages      消息内容
     */
    public static void sendMessages(Long userId, List<SseMessage> messages) {
        SSE.sendMessages(userId, messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     */
    public static void sendMessages(Long userId, String token, List<SseMessage> messages) {
        SSE.sendMessages(userId, token, messages);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    public static void sendMessages(Long userId, String token, List<SseMessage> messages, Boolean isDisconnect) {
        SSE.sendMessages(userId, token, messages, isDisconnect);
    }

}
