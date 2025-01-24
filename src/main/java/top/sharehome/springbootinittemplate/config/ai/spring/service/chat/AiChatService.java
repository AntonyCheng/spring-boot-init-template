package top.sharehome.springbootinittemplate.config.ai.spring.service.chat;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;

/**
 * AI Chat服务接口
 *
 * @author AntonyCheng
 */
public interface AiChatService {

    /**
     * 发起AI Chat功能
     *
     * @param chatModel chat模型信息
     * @param message   提示词
     */
    String chat(ChatModelBase chatModel, String message);

    /**
     * 发起AI Chat功能
     *
     * @param chatModel chat模型信息
     * @param messages  提示词
     */
    String chat(ChatModelBase chatModel, Message... messages);

    /**
     * 发起AI Chat功能
     *
     * @param chatModel chat模型信息
     * @param prompt    提示词
     */
    String chat(ChatModelBase chatModel, Prompt prompt);

}
