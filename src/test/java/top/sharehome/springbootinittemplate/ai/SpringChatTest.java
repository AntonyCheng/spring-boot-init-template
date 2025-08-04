package top.sharehome.springbootinittemplate.ai;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.OpenAIServiceVersion;
import com.azure.core.credential.AzureKeyCredential;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.minimax.MiniMaxChatModel;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.resolution.DelegatingToolCallbackResolver;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.impl.AiChatServiceImpl;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatResultChunk;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity.*;

import java.util.List;
import java.util.Objects;

/**
 * Spring AI Chat测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class SpringChatTest {

    @Resource
    private AiChatServiceImpl aiChatService;

    /**
     * 测试DeepSeekChat
     */
    @Test
    public void testDeepSeekChat() {
        DeepSeekChatEntity entity = new DeepSeekChatEntity("deepseek-reasoner", "sk-xxx");
        DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                .baseUrl(entity.getBaseUrl())
                .apiKey(new SimpleApiKey(entity.getApiKey()))
                .restClientBuilder(RestClient.builder())
                .build();
        DeepSeekChatModel chatModel = DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(DeepSeekChatOptions.builder()
                        .model(entity.getModel())
                        .temperature(entity.getTemperature())
                        .topP(entity.getTopP())
                        .build())
                .build();
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试OpenAIChat
     */
    @Test
    public void testOpenAiChat() {
        OpenAiChatEntity entity = new OpenAiChatEntity(OpenAiApi.ChatModel.GPT_3_5_TURBO, "sk-xxx");
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(entity.getBaseUrl())
                .apiKey(new SimpleApiKey(entity.getApiKey()))
                .build();
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(entity.getModel())
                        .temperature(entity.getTemperature())
                        .topP(entity.getTemperature())
                        .build())
                .build();
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试OllamaChat
     */
    @Test
    public void testOllamaChat() {
        OllamaChatEntity entity = new OllamaChatEntity("deepseek-r1:8b", "http://localhost:11434");
        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl(entity.getBaseUrl())
                .build();
        OllamaChatModel chatModel = new OllamaChatModel(ollamaApi, OllamaOptions.builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build()
                , new DefaultToolCallingManager(ObservationRegistry.NOOP,
                new DelegatingToolCallbackResolver(List.of()),
                new DefaultToolExecutionExceptionProcessor(true)), ObservationRegistry.NOOP, ModelManagementOptions.builder().build());
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试ZhiPuAiChat
     */
    @Test
    public void testZhiPuAiChat() {
        ZhiPuAiChatEntity entity = new ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel.GLM_4_Flash.getName(), "xxx.xxx");
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(entity.getApiKey());
        ZhiPuAiChatModel chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build());
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试MistralAiChat
     */
    @Test
    public void testMistralAiChat() {
        MistralAiChatEntity entity = new MistralAiChatEntity(MistralAiApi.ChatModel.MINISTRAL_8B_LATEST.getName(), "xxx");
        MistralAiApi mistralAiApi = new MistralAiApi(entity.getApiKey());
        MistralAiChatModel chatModel = MistralAiChatModel.builder()
                .mistralAiApi(mistralAiApi)
                .defaultOptions(MistralAiChatOptions.builder()
                        .model(entity.getModel())
                        .temperature(entity.getTemperature())
                        .topP(entity.getTopP())
                        .build())
                .build();
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试MinimaxChat
     */
    @Test
    public void testMiniMaxChat() {
        MiniMaxChatEntity entity = new MiniMaxChatEntity(MiniMaxApi.ChatModel.ABAB_6_5_Chat, "xxx.xxx.xxx-xxx-xxx-xxx-xxx-xxx");
        MiniMaxApi miniMaxApi = new MiniMaxApi(entity.getApiKey());
        MiniMaxChatModel chatModel = new MiniMaxChatModel(miniMaxApi, MiniMaxChatOptions.builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build());
        System.out.println(chatModel.call("你是谁？"));
    }

    /**
     * 测试AzureOpenAiChat
     */
    @Test
    public void testAzureOpenAiChat() {
        AzureOpenAiChatEntity entity = new AzureOpenAiChatEntity("gpt-35-turbo", OpenAIServiceVersion.getLatest(), "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        OpenAIClientBuilder clientBuilder = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(entity.getApiKey()))
                .endpoint(entity.getEndpoint())
                .serviceVersion(entity.getModelVersion());
        AzureOpenAiChatModel chatModel = new AzureOpenAiChatModel(clientBuilder, AzureOpenAiChatOptions.builder()
                .deploymentName(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build(), DefaultToolCallingManager.builder().build(), ObservationRegistry.NOOP);
        System.out.println(chatModel.call("你是谁？"));
    }

    @Test
    public void testStringByString() {
        DeepSeekChatEntity deepSeekChatEntity = new DeepSeekChatEntity("deepseek-r1", "sk-xxx");
        OpenAiChatEntity openAiChatEntity = new OpenAiChatEntity(OpenAiApi.ChatModel.GPT_3_5_TURBO, "sk-xxx");
        OllamaChatEntity ollamaChatEntity = new OllamaChatEntity("qwen2.5:0.5b-instruct-fp16", "http://localhost:11434");
        ZhiPuAiChatEntity zhiPuAiChatEntity = new ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel.GLM_4_Flash.getName(), "xxx.xxx");
        MistralAiChatEntity mistralAiChatEntity = new MistralAiChatEntity(MistralAiApi.ChatModel.MINISTRAL_8B_LATEST.getName(), "xxx");
        MiniMaxChatEntity miniMaxChatEntity = new MiniMaxChatEntity(MiniMaxApi.ChatModel.ABAB_6_5_Chat, "xxx.xxx.xxx-xxx-xxx-xxx-xxx-xxx");
        AzureOpenAiChatEntity azureOpenAiChatEntity = new AzureOpenAiChatEntity("gpt-4o-mini", OpenAIServiceVersion.V2024_05_01_PREVIEW, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        String message = "你是什么模型？";
        System.out.println(aiChatService.chatString(deepSeekChatEntity, message));
        System.out.println(aiChatService.chatString(openAiChatEntity, message));
        System.out.println(aiChatService.chatString(ollamaChatEntity, message));
        System.out.println(aiChatService.chatString(zhiPuAiChatEntity, message));
        System.out.println(aiChatService.chatString(mistralAiChatEntity, message));
        System.out.println(aiChatService.chatString(miniMaxChatEntity, message));
        System.out.println(aiChatService.chatString(azureOpenAiChatEntity, message));
    }

    @Test
    public void testStreamByString() {
        DeepSeekChatEntity deepSeekChatEntity = new DeepSeekChatEntity("deepseek-r1", "sk-xxx");
        OpenAiChatEntity openAiChatEntity = new OpenAiChatEntity(OpenAiApi.ChatModel.GPT_3_5_TURBO, "sk-xxx");
        OllamaChatEntity ollamaChatEntity = new OllamaChatEntity("qwen2.5:0.5b-instruct-fp16", "http://localhost:11434");
        ZhiPuAiChatEntity zhiPuAiChatEntity = new ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel.GLM_4_Flash.getName(), "xxx.xxx");
        MistralAiChatEntity mistralAiChatEntity = new MistralAiChatEntity(MistralAiApi.ChatModel.MINISTRAL_8B_LATEST.getName(), "xxx");
        MiniMaxChatEntity miniMaxChatEntity = new MiniMaxChatEntity(MiniMaxApi.ChatModel.ABAB_6_5_Chat, "xxx.xxx.xxx-xxx-xxx-xxx-xxx-xxx");
        AzureOpenAiChatEntity azureOpenAiChatEntity = new AzureOpenAiChatEntity("gpt-4o-mini", OpenAIServiceVersion.V2024_05_01_PREVIEW, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        String message = "你是什么模型？";
        List<ChatResultChunk> deepseek = aiChatService.chatStream(deepSeekChatEntity, message).toList();
        for (ChatResultChunk s : deepseek) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<ChatResultChunk> openAi = aiChatService.chatStream(openAiChatEntity, message).toList();
        for (ChatResultChunk s : openAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<ChatResultChunk> ollama = aiChatService.chatStream(ollamaChatEntity, message).toList();
        for (ChatResultChunk s : ollama) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<ChatResultChunk> zhiPuAi = aiChatService.chatStream(zhiPuAiChatEntity, message).toList();
        for (ChatResultChunk s : zhiPuAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<ChatResultChunk> mistralAi = aiChatService.chatStream(mistralAiChatEntity, message).toList();
        for (ChatResultChunk s : mistralAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<ChatResultChunk> miniMax = aiChatService.chatStream(miniMaxChatEntity, message).toList();
        for (ChatResultChunk s : miniMax) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<ChatResultChunk> azureOpenAi = aiChatService.chatStream(azureOpenAiChatEntity, message).toList();
        for (ChatResultChunk s : azureOpenAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testFluxByString() {
        DeepSeekChatEntity deepSeekChatEntity = new DeepSeekChatEntity("deepseek-r1", "sk-xxx");
        OpenAiChatEntity openAiChatEntity = new OpenAiChatEntity(OpenAiApi.ChatModel.GPT_3_5_TURBO, "sk-xxx");
        OllamaChatEntity ollamaChatEntity = new OllamaChatEntity("qwen2.5:0.5b-instruct-fp16", "http://localhost:11434");
        ZhiPuAiChatEntity zhiPuAiChatEntity = new ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel.GLM_4_Flash.getName(), "xxx.xxx");
        MistralAiChatEntity mistralAiChatEntity = new MistralAiChatEntity(MistralAiApi.ChatModel.MINISTRAL_8B_LATEST.getName(), "xxx");
        MiniMaxChatEntity miniMaxChatEntity = new MiniMaxChatEntity(MiniMaxApi.ChatModel.ABAB_6_5_Chat, "xxx.xxx.xxx-xxx-xxx-xxx-xxx-xxx");
        AzureOpenAiChatEntity azureOpenAiChatEntity = new AzureOpenAiChatEntity("gpt-4o-mini", OpenAIServiceVersion.V2024_05_01_PREVIEW, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        String message = "你是什么模型？";
        aiChatService.chatFlux(deepSeekChatEntity, message).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(openAiChatEntity, message).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(ollamaChatEntity, message).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(zhiPuAiChatEntity, message).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(mistralAiChatEntity, message).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(miniMaxChatEntity, message).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(azureOpenAiChatEntity, message).doOnNext(System.out::print).blockLast();
        System.out.println();
    }

    @Test
    public void testStringByMessages() {
        DeepSeekChatEntity deepSeekChatEntity = new DeepSeekChatEntity("deepseek-r1", "sk-xxx");
        OpenAiChatEntity openAiChatEntity = new OpenAiChatEntity(OpenAiApi.ChatModel.GPT_3_5_TURBO, "sk-xxx");
        OllamaChatEntity ollamaChatEntity = new OllamaChatEntity("qwen2.5:0.5b-instruct-fp16", "http://localhost:11434");
        ZhiPuAiChatEntity zhiPuAiChatEntity = new ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel.GLM_4_Flash.getName(), "xxx.xxx");
        MistralAiChatEntity mistralAiChatEntity = new MistralAiChatEntity(MistralAiApi.ChatModel.MINISTRAL_8B_LATEST.getName(), "xxx");
        MiniMaxChatEntity miniMaxChatEntity = new MiniMaxChatEntity(MiniMaxApi.ChatModel.ABAB_6_5_Chat, "xxx.xxx.xxx-xxx-xxx-xxx-xxx-xxx");
        AzureOpenAiChatEntity azureOpenAiChatEntity = new AzureOpenAiChatEntity("gpt-4o-mini", OpenAIServiceVersion.V2024_05_01_PREVIEW, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        Message[] messages = new Message[]{
                new SystemMessage("你是一个诚实的大语言模型，你一定要准确表达出你的真实意思"),
                new UserMessage("你是什么模型？")
        };
        System.out.println(aiChatService.chatString(deepSeekChatEntity, messages));
        System.out.println(aiChatService.chatString(openAiChatEntity, messages));
        System.out.println(aiChatService.chatString(ollamaChatEntity, messages));
        System.out.println(aiChatService.chatString(zhiPuAiChatEntity, messages));
        System.out.println(aiChatService.chatString(mistralAiChatEntity, messages));
        System.out.println(aiChatService.chatString(miniMaxChatEntity, messages));
        System.out.println(aiChatService.chatString(azureOpenAiChatEntity, messages));
    }

    @Test
    public void testStreamByMessages() {
        DeepSeekChatEntity deepSeekChatEntity = new DeepSeekChatEntity("deepseek-r1", "sk-xxx");
        OpenAiChatEntity openAiChatEntity = new OpenAiChatEntity(OpenAiApi.ChatModel.GPT_3_5_TURBO, "sk-xxx");
        OllamaChatEntity ollamaChatEntity = new OllamaChatEntity("qwen2.5:0.5b-instruct-fp16", "http://localhost:11434");
        ZhiPuAiChatEntity zhiPuAiChatEntity = new ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel.GLM_4_Flash.getName(), "xxx.xxx");
        MistralAiChatEntity mistralAiChatEntity = new MistralAiChatEntity(MistralAiApi.ChatModel.MINISTRAL_8B_LATEST.getName(), "xxx");
        MiniMaxChatEntity miniMaxChatEntity = new MiniMaxChatEntity(MiniMaxApi.ChatModel.ABAB_6_5_Chat, "xxx.xxx.xxx-xxx-xxx-xxx-xxx-xxx");
        AzureOpenAiChatEntity azureOpenAiChatEntity = new AzureOpenAiChatEntity("gpt-4o-mini", OpenAIServiceVersion.V2024_05_01_PREVIEW, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        Message[] messages = new Message[]{
                new SystemMessage("你是一个诚实的大语言模型，你一定要准确表达出你的真实意思"),
                new UserMessage("你是什么模型？")
        };
        List<String> deepSeek = aiChatService.chatStream(deepSeekChatEntity, messages).toList();
        for (String s : deepSeek) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> openAi = aiChatService.chatStream(openAiChatEntity, messages).toList();
        for (String s : openAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> ollama = aiChatService.chatStream(ollamaChatEntity, messages).toList();
        for (String s : ollama) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> zhiPuAi = aiChatService.chatStream(zhiPuAiChatEntity, messages).toList();
        for (String s : zhiPuAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> mistralAi = aiChatService.chatStream(mistralAiChatEntity, messages).toList();
        for (String s : mistralAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> miniMax = aiChatService.chatStream(miniMaxChatEntity, messages).toList();
        for (String s : miniMax) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> azureOpenAi = aiChatService.chatStream(azureOpenAiChatEntity, messages).toList();
        for (String s : azureOpenAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testFluxByMessages() {
        DeepSeekChatEntity deepSeekChatEntity = new DeepSeekChatEntity("deepseek-r1", "sk-xxx");
        OpenAiChatEntity openAiChatEntity = new OpenAiChatEntity(OpenAiApi.ChatModel.GPT_3_5_TURBO, "sk-xxx");
        OllamaChatEntity ollamaChatEntity = new OllamaChatEntity("qwen2.5:0.5b-instruct-fp16", "http://localhost:11434");
        ZhiPuAiChatEntity zhiPuAiChatEntity = new ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel.GLM_4_Flash.getName(), "xxx.xxx");
        MistralAiChatEntity mistralAiChatEntity = new MistralAiChatEntity(MistralAiApi.ChatModel.MINISTRAL_8B_LATEST.getName(), "xxx");
        MiniMaxChatEntity miniMaxChatEntity = new MiniMaxChatEntity(MiniMaxApi.ChatModel.ABAB_6_5_Chat, "xxx.xxx.xxx-xxx-xxx-xxx-xxx-xxx");
        AzureOpenAiChatEntity azureOpenAiChatEntity = new AzureOpenAiChatEntity("gpt-4o-mini", OpenAIServiceVersion.V2024_05_01_PREVIEW, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        Message[] messages = new Message[]{
                new SystemMessage("你是一个诚实的大语言模型，你一定要准确表达出你的真实意思"),
                new UserMessage("你是什么模型？")
        };
        aiChatService.chatFlux(deepSeekChatEntity, messages).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(openAiChatEntity, messages).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(ollamaChatEntity, messages).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(zhiPuAiChatEntity, messages).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(mistralAiChatEntity, messages).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(miniMaxChatEntity, messages).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(azureOpenAiChatEntity, messages).doOnNext(System.out::print).blockLast();
        System.out.println();
    }

    @Test
    public void testStringByPrompt() {
        DeepSeekChatEntity deepSeekChatEntity = new DeepSeekChatEntity("deepseek-r1", "sk-xxx");
        OpenAiChatEntity openAiChatEntity = new OpenAiChatEntity(OpenAiApi.ChatModel.GPT_3_5_TURBO, "sk-xxx");
        OllamaChatEntity ollamaChatEntity = new OllamaChatEntity("qwen2.5:0.5b-instruct-fp16", "http://localhost:11434");
        ZhiPuAiChatEntity zhiPuAiChatEntity = new ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel.GLM_4_Flash.getName(), "xxx.xxx");
        MistralAiChatEntity mistralAiChatEntity = new MistralAiChatEntity(MistralAiApi.ChatModel.MINISTRAL_8B_LATEST.getName(), "xxx");
        MiniMaxChatEntity miniMaxChatEntity = new MiniMaxChatEntity(MiniMaxApi.ChatModel.ABAB_6_5_Chat, "xxx.xxx.xxx-xxx-xxx-xxx-xxx-xxx");
        AzureOpenAiChatEntity azureOpenAiChatEntity = new AzureOpenAiChatEntity("gpt-4o-mini", OpenAIServiceVersion.V2024_05_01_PREVIEW, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        Prompt prompt = new Prompt("你是什么模型？");
        System.out.println(aiChatService.chatString(deepSeekChatEntity, prompt));
        System.out.println(aiChatService.chatString(openAiChatEntity, prompt));
        System.out.println(aiChatService.chatString(ollamaChatEntity, prompt));
        System.out.println(aiChatService.chatString(zhiPuAiChatEntity, prompt));
        System.out.println(aiChatService.chatString(mistralAiChatEntity, prompt));
        System.out.println(aiChatService.chatString(miniMaxChatEntity, prompt));
        System.out.println(aiChatService.chatString(azureOpenAiChatEntity, prompt));
    }

    @Test
    public void testStreamByPrompt() {
        DeepSeekChatEntity deepSeekChatEntity = new DeepSeekChatEntity("deepseek-r1", "sk-xxx");
        OpenAiChatEntity openAiChatEntity = new OpenAiChatEntity(OpenAiApi.ChatModel.GPT_3_5_TURBO, "sk-xxx");
        OllamaChatEntity ollamaChatEntity = new OllamaChatEntity("qwen2.5:0.5b-instruct-fp16", "http://localhost:11434");
        ZhiPuAiChatEntity zhiPuAiChatEntity = new ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel.GLM_4_Flash.getName(), "xxx.xxx");
        MistralAiChatEntity mistralAiChatEntity = new MistralAiChatEntity(MistralAiApi.ChatModel.MINISTRAL_8B_LATEST.getName(), "xxx");
        MiniMaxChatEntity miniMaxChatEntity = new MiniMaxChatEntity(MiniMaxApi.ChatModel.ABAB_6_5_Chat, "xxx.xxx.xxx-xxx-xxx-xxx-xxx-xxx");
        AzureOpenAiChatEntity azureOpenAiChatEntity = new AzureOpenAiChatEntity("gpt-4o-mini", OpenAIServiceVersion.V2024_05_01_PREVIEW, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        Prompt prompt = new Prompt("你是什么模型？");
        List<String> deepSeek = aiChatService.chatStream(deepSeekChatEntity, prompt).toList();
        for (String s : deepSeek) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> openAi = aiChatService.chatStream(openAiChatEntity, prompt).toList();
        for (String s : openAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> ollama = aiChatService.chatStream(ollamaChatEntity, prompt).toList();
        for (String s : ollama) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> zhiPuAi = aiChatService.chatStream(zhiPuAiChatEntity, prompt).toList();
        for (String s : zhiPuAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> mistralAi = aiChatService.chatStream(mistralAiChatEntity, prompt).toList();
        for (String s : mistralAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> miniMax = aiChatService.chatStream(miniMaxChatEntity, prompt).toList();
        for (String s : miniMax) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        List<String> azureOpenAi = aiChatService.chatStream(azureOpenAiChatEntity, prompt).toList();
        for (String s : azureOpenAi) {
            System.out.println(s);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testFluxByPrompt() {
        DeepSeekChatEntity deepSeekChatEntity = new DeepSeekChatEntity("deepseek-r1", "sk-xxx");
        OpenAiChatEntity openAiChatEntity = new OpenAiChatEntity(OpenAiApi.ChatModel.GPT_3_5_TURBO, "sk-xxx");
        OllamaChatEntity ollamaChatEntity = new OllamaChatEntity("qwen2.5:0.5b-instruct-fp16", "http://localhost:11434");
        ZhiPuAiChatEntity zhiPuAiChatEntity = new ZhiPuAiChatEntity(ZhiPuAiApi.ChatModel.GLM_4_Flash.getName(), "xxx.xxx");
        MistralAiChatEntity mistralAiChatEntity = new MistralAiChatEntity(MistralAiApi.ChatModel.MINISTRAL_8B_LATEST.getName(), "xxx");
        MiniMaxChatEntity miniMaxChatEntity = new MiniMaxChatEntity(MiniMaxApi.ChatModel.ABAB_6_5_Chat, "xxx.xxx.xxx-xxx-xxx-xxx-xxx-xxx");
        AzureOpenAiChatEntity azureOpenAiChatEntity = new AzureOpenAiChatEntity("gpt-4o-mini", OpenAIServiceVersion.V2024_05_01_PREVIEW, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        Prompt prompt = new Prompt("你是什么模型？");
        aiChatService.chatFlux(deepSeekChatEntity, prompt).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(openAiChatEntity, prompt).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(ollamaChatEntity, prompt).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(zhiPuAiChatEntity, prompt).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(mistralAiChatEntity, prompt).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(miniMaxChatEntity, prompt).doOnNext(System.out::print).blockLast();
        System.out.println();
        aiChatService.chatFlux(azureOpenAiChatEntity, prompt).doOnNext(System.out::print).blockLast();
        System.out.println();
    }

}
