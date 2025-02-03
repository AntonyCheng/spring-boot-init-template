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

}
