package top.sharehome.springbootinittemplate.ai;

import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.ai.minimax.MiniMaxChatModel;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.model.function.DefaultFunctionCallbackResolver;
import org.springframework.ai.moonshot.MoonshotChatModel;
import org.springframework.ai.moonshot.MoonshotChatOptions;
import org.springframework.ai.moonshot.api.MoonshotApi;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.ai.qianfan.QianFanChatOptions;
import org.springframework.ai.qianfan.api.QianFanApi;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity.OpenAiChatEntity;

import java.util.List;

/**
 * Spring AI测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class ChatTest {

    /**
     * 测试OpenAIChat
     */
    @Test
    public void testOpenAiChat() {
        OpenAiChatEntity entity = new OpenAiChatEntity(OpenAiApi.ChatModel.GPT_3_5_TURBO, "sk-xxx", "https://api.chatanywhere.tech");
        OpenAiApi openAiApi = new OpenAiApi(entity.getBaseUrl(), entity.getApiKey());
        OpenAiChatModel chatModel = new OpenAiChatModel(openAiApi, OpenAiChatOptions
                .builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTemperature())
                .build());
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试OllamaChat
     */
    @Test
    public void testOllamaChat() {
        OllamaApi ollamaApi = new OllamaApi("http://localhost:11434");
        OllamaChatModel chatModel = new OllamaChatModel(ollamaApi, OllamaOptions
                .builder()
                .model("qwen2.5:0.5b-instruct-fp16")
                .temperature(0.5)
                .topP(0.5)
                .topK(50)
                .build()
                , new DefaultFunctionCallbackResolver(), List.of(), ObservationRegistry.NOOP, ModelManagementOptions.builder().build());
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试ZhiPuAiChat
     */
    @Test
    public void testZhiPuAiChat() {
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi("xxx.xxx");
        ZhiPuAiChatModel chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions
                .builder()
                .model(ZhiPuAiApi.ChatModel.GLM_4_Flash.getValue())
                .temperature(0.5)
                .topP(0.5)
                .build());
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试MoonshotChat
     */
    @Test
    public void testMoonshotChat() {
        MoonshotApi moonshotApi = new MoonshotApi("sk-xxx");
        MoonshotChatModel chatModel = new MoonshotChatModel(moonshotApi, MoonshotChatOptions
                .builder()
                .model(MoonshotApi.ChatModel.MOONSHOT_V1_8K.getValue())
                .temperature(0.5)
                .topP(0.5)
                .build());
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试MistralAiChat
     */
    @Test
    public void testMistralAiChat() {
        MistralAiApi mistralAiApi = new MistralAiApi("xxx");
        MistralAiChatModel chatModel = new MistralAiChatModel(mistralAiApi, MistralAiChatOptions
                .builder()
                .model(MistralAiApi.ChatModel.SMALL.getName())
                .temperature(0.5)
                .topP(0.5)
                .build());
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试QianFanChat
     */
    @Test
    public void testQianFanChat() {
        QianFanApi qianFanApi = new QianFanApi("xxx", "xxx");
        QianFanChatModel chatModel = new QianFanChatModel(qianFanApi, QianFanChatOptions
                .builder()
                .model(QianFanApi.ChatModel.ERNIE_Speed_8K.getValue())
                .temperature(0.5)
                .topP(0.5)
                .build());
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试MinimaxChat，结果会失败
     * 模型未同步SpringAI官方，未来会进一步集成
     */
    @Test
    @Deprecated
    public void testMinimaxChat() {
        MiniMaxApi miniMaxApi = new MiniMaxApi("x.x.x.x");
        MiniMaxChatModel chatModel = new MiniMaxChatModel(miniMaxApi, MiniMaxChatOptions
                .builder()
                .withModel(MiniMaxApi.ChatModel.ABAB_5_5_Chat.getModelName())
                .withTemperature(0.5f)
                .withTopP(0.5f)
                .build());
        System.out.println(chatModel.call("你是谁？"));
    }

}
