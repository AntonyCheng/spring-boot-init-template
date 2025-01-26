package top.sharehome.springbootinittemplate.config.ai.spring.service.chat;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;

import java.util.stream.Stream;

/**
 * AI Chat服务接口
 *
 * @author AntonyCheng
 */
public interface AiChatService {

    /**
     * 发起AI Chat功能，返回字符串数据
     *
     * @param model     chat模型信息
     * @param prompt    提示词
     */
    String chatString(ChatModelBase model, String prompt);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model     chat模型信息
     * @param prompt    提示词
     */
    Stream<String> chatStream(ChatModelBase model, String prompt);

    /**
     * 发起AI Chat功能，返回字符串数据
     *
     * @param model     chat模型信息
     * @param prompt    提示词
     */
    String chatString(ChatModelBase model, Message... prompt);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model     chat模型信息
     * @param prompt    提示词
     */
    Stream<String> chatStream(ChatModelBase model, Message... prompt);

    /**
     * 发起AI Chat功能，返回字符串数据
     *
     * @param model     chat模型信息
     * @param prompt    提示词
     */
    String chatString(ChatModelBase model, Prompt prompt);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model     chat模型信息
     * @param prompt    提示词
     */
    Stream<String> chatStream(ChatModelBase model, Prompt prompt);


//    /**
//     * 发起AI Chat功能，并且将结果输出至SSE连接
//     *
//     * @param model       chat模型信息
//     * @param message     提示词
//     * @param sseEmitter  SSE连接
//     * @param isLogin     是否存在登录状态
//     * @param isStream    是否流式返回
//     */
//    void chatToSse(ChatModelBase model, String message, SseEmitter sseEmitter, Boolean isLogin, Boolean isStream);

}
