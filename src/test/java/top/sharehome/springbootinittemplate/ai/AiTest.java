package top.sharehome.springbootinittemplate.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.function.DefaultFunctionCallbackResolver;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import top.sharehome.springbootinittemplate.config.sse.SseConfiguration;
import top.sharehome.springbootinittemplate.config.sse.entity.SseMessage;

import java.util.ArrayList;

/**
 * Spring AI测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class AiTest {

    @Test
    public void testZhiPuAi() {
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi("f403f6d2d967699b5db06313391af8c9.lE9g6q5Xl3WAZjEw");
        ZhiPuAiChatModel chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions
                .builder()
                .model(ZhiPuAiApi.ChatModel.GLM_4_Flash.getValue())
                .temperature(0.4)
                .maxTokens(200)
                .build());
        System.out.println(chatModel.call("你好"));
    }

    @Test
    public void testOllamaAi() {
        OllamaApi ollamaApi = new OllamaApi();
//        new OllamaChatModel();
//        System.out.println(ollamaChatModel.call(new UserMessage("我的名字叫AntonyCheng，你能说出我的名字么？")));
//        System.out.println(ollamaChatModel.call(new UserMessage("你能说出我的名字么？")));
//        System.out.println(ollamaChatModel.call(new UserMessage("我的名字叫AntonyCheng，你能说出我的名字么？")));
//        System.out.println(ollamaChatModel.call(new UserMessage("我的名字叫AntonyCheng，你能说出我的名字么？"), new UserMessage("我的名字叫什么？")));
    }

}
