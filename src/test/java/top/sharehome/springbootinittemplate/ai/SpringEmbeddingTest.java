package top.sharehome.springbootinittemplate.ai;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.OpenAIServiceVersion;
import com.azure.core.credential.AzureKeyCredential;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingModel;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingOptions;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.minimax.MiniMaxEmbeddingModel;
import org.springframework.ai.minimax.MiniMaxEmbeddingOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;
import org.springframework.ai.mistralai.MistralAiEmbeddingOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.support.RetryTemplate;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.AiEmbeddingService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingResult;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.entity.*;

import java.util.Arrays;
import java.util.List;

/**
 * Spring AI Embedding测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class SpringEmbeddingTest {

    @Resource
    private AiEmbeddingService aiEmbeddingService;

    /**
     * 测试OpenAiEmbedding
     */
    @Test
    public void testOpenAiEmbedding() {
        OpenAiEmbeddingEntity entity = new OpenAiEmbeddingEntity(OpenAiApi.EmbeddingModel.TEXT_EMBEDDING_ADA_002, "xxx-xxx");
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(entity.getBaseUrl())
                .apiKey(entity.getApiKey())
                .build();
        OpenAiEmbeddingModel embeddingModel = new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED, OpenAiEmbeddingOptions.builder()
                .model(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of("Hello World", "你好,世界！"));
        embeddingResponse.getResults().forEach(result -> System.out.println(Arrays.toString(result.getOutput())));
    }

    /**
     * 测试AzureOpenAiEmbedding
     */
    @Test
    public void testAzureOpenAiEmbedding() {
        AzureOpenAiEmbeddingEntity entity = new AzureOpenAiEmbeddingEntity("text-embedding-3-small", OpenAIServiceVersion.getLatest(), "xxx", "https://xxx-xxx-xxx.cognitiveservices.azure.com/");
        OpenAIClientBuilder clientBuilder = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(entity.getApiKey()))
                .endpoint(entity.getEndpoint())
                .serviceVersion(OpenAIServiceVersion.getLatest());
        AzureOpenAiEmbeddingModel embeddingModel = new AzureOpenAiEmbeddingModel(clientBuilder.buildClient(), MetadataMode.EMBED, AzureOpenAiEmbeddingOptions.builder()
                .deploymentName(entity.getModel())
                .build(), ObservationRegistry.NOOP);
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of("Hello World", "你好,世界！"));
        embeddingResponse.getResults().forEach(result -> System.out.println(Arrays.toString(result.getOutput())));
    }

    /**
     * 测试MistralAiEmbedding
     */
    @Test
    public void testMistralAiEmbedding() {
        MistralAiEmbeddingEntity entity = new MistralAiEmbeddingEntity(MistralAiApi.EmbeddingModel.EMBED, "xxx");
        MistralAiApi mistralAiApi = MistralAiApi.builder().apiKey(entity.getApiKey()).build();
        MistralAiEmbeddingModel embeddingModel = new MistralAiEmbeddingModel(mistralAiApi, MetadataMode.EMBED, MistralAiEmbeddingOptions.builder()
                .withEncodingFormat("float")
                .withModel(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of("Hello World", "你好,世界！"));
        embeddingResponse.getResults().forEach(result -> System.out.println(Arrays.toString(result.getOutput())));
    }

    /**
     * 测试MiniMaxEmbedding
     */
    @Test
    public void testMiniMaxEmbedding() {
        MiniMaxEmbeddingEntity entity = new MiniMaxEmbeddingEntity(MiniMaxApi.EmbeddingModel.Embo_01, "xxx.xxx");
        MiniMaxApi miniMaxApi = new MiniMaxApi(entity.getApiKey());
        MiniMaxEmbeddingModel embeddingModel = new MiniMaxEmbeddingModel(miniMaxApi, MetadataMode.EMBED, MiniMaxEmbeddingOptions.builder()
                .model(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of("Hello World", "你好,世界！"));
        embeddingResponse.getResults().forEach(result -> System.out.println(Arrays.toString(result.getOutput())));
    }

    /**
     * 测试OllamaEmbedding
     */
    @Test
    public void testOllamaEmbedding() {
        OllamaEmbeddingEntity entity = new OllamaEmbeddingEntity("nomic-embed-text", "http://localhost:11434");
        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl(entity.getBaseUrl())
                .build();
        OllamaEmbeddingModel embeddingModel = new OllamaEmbeddingModel(ollamaApi, OllamaEmbeddingOptions.builder()
                .model(entity.getModel())
                .build(), ObservationRegistry.NOOP, ModelManagementOptions.defaults());
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of("Hello World", "你好,世界！"));
        embeddingResponse.getResults().forEach(result -> System.out.println(Arrays.toString(result.getOutput())));
    }

    /**
     * 测试ZhiPuAiEmbedding
     */
    @Test
    public void testZhiPuAiEmbedding() {
        ZhiPuAiEmbeddingEntity entity = new ZhiPuAiEmbeddingEntity(ZhiPuAiApi.EmbeddingModel.Embedding_3, "xxx.xxx");
        ZhiPuAiApi zhiPuAiApi = ZhiPuAiApi.builder().apiKey(entity.getApiKey()).build();
        ZhiPuAiEmbeddingModel embeddingModel = new ZhiPuAiEmbeddingModel(zhiPuAiApi, MetadataMode.EMBED, ZhiPuAiEmbeddingOptions.builder()
                .model(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of("Hello World", "你好,世界！"));
        embeddingResponse.getResults().forEach(result -> System.out.println(Arrays.toString(result.getOutput())));
    }

    @Test
    public void testEmbedToArray() {
        OpenAiEmbeddingEntity openAiEmbeddingEntity = new OpenAiEmbeddingEntity(OpenAiApi.EmbeddingModel.TEXT_EMBEDDING_ADA_002, "xxx-xxx");
        AzureOpenAiEmbeddingEntity azureOpenAiEmbeddingEntity = new AzureOpenAiEmbeddingEntity("text-embedding-3-small", OpenAIServiceVersion.getLatest(), "xxx", "https://xxx-xxx-xxx.cognitiveservices.azure.com/");
        MistralAiEmbeddingEntity mistralAiEmbeddingEntity = new MistralAiEmbeddingEntity(MistralAiApi.EmbeddingModel.EMBED, "xxx");
        MiniMaxEmbeddingEntity miniMaxEmbeddingEntity = new MiniMaxEmbeddingEntity(MiniMaxApi.EmbeddingModel.Embo_01, "xxx.xxx");
        OllamaEmbeddingEntity ollamaEmbeddingEntity = new OllamaEmbeddingEntity("nomic-embed-text", "http://localhost:11434");
        ZhiPuAiEmbeddingEntity zhiPuAiEmbeddingEntity = new ZhiPuAiEmbeddingEntity(ZhiPuAiApi.EmbeddingModel.Embedding_3, "xxx.xxx");
        String text = "Hello,World! 你好，世界！";
        System.out.println(Arrays.toString(aiEmbeddingService.embedToArray(openAiEmbeddingEntity, text)));
        System.out.println("************************************************************************");
        System.out.println(Arrays.toString(aiEmbeddingService.embedToArray(azureOpenAiEmbeddingEntity, text)));
        System.out.println("************************************************************************");
        System.out.println(Arrays.toString(aiEmbeddingService.embedToArray(mistralAiEmbeddingEntity, text)));
        System.out.println("************************************************************************");
        System.out.println(Arrays.toString(aiEmbeddingService.embedToArray(miniMaxEmbeddingEntity, text)));
        System.out.println("************************************************************************");
        System.out.println(Arrays.toString(aiEmbeddingService.embedToArray(ollamaEmbeddingEntity, text)));
        System.out.println("************************************************************************");
        System.out.println(Arrays.toString(aiEmbeddingService.embedToArray(zhiPuAiEmbeddingEntity, text)));
    }

    @Test
    public void testEmbedToArrayList() {
        OpenAiEmbeddingEntity openAiEmbeddingEntity = new OpenAiEmbeddingEntity(OpenAiApi.EmbeddingModel.TEXT_EMBEDDING_ADA_002, "xxx-xxx");
        AzureOpenAiEmbeddingEntity azureOpenAiEmbeddingEntity = new AzureOpenAiEmbeddingEntity("text-embedding-3-small", OpenAIServiceVersion.getLatest(), "xxx", "https://xxx-xxx-xxx.cognitiveservices.azure.com/");
        MistralAiEmbeddingEntity mistralAiEmbeddingEntity = new MistralAiEmbeddingEntity(MistralAiApi.EmbeddingModel.EMBED, "xxx");
        MiniMaxEmbeddingEntity miniMaxEmbeddingEntity = new MiniMaxEmbeddingEntity(MiniMaxApi.EmbeddingModel.Embo_01, "xxx.xxx");
        OllamaEmbeddingEntity ollamaEmbeddingEntity = new OllamaEmbeddingEntity("nomic-embed-text", "http://localhost:11434");
        ZhiPuAiEmbeddingEntity zhiPuAiEmbeddingEntity = new ZhiPuAiEmbeddingEntity(ZhiPuAiApi.EmbeddingModel.Embedding_3, "xxx.xxx");
        String[] text = {"Hello,World!", "你好，世界！"};
        List<float[]> openAiResList = aiEmbeddingService.embedToArrayList(openAiEmbeddingEntity, text);
        System.out.println(openAiResList);
        System.out.println("************************************************************************");
        List<float[]> azureOpenAiResList = aiEmbeddingService.embedToArrayList(azureOpenAiEmbeddingEntity, text);
        System.out.println(azureOpenAiResList);
        System.out.println("************************************************************************");
        List<float[]> mistralAiResList = aiEmbeddingService.embedToArrayList(mistralAiEmbeddingEntity, text);
        System.out.println(mistralAiResList);
        System.out.println("************************************************************************");
        List<float[]> miniMaxResList = aiEmbeddingService.embedToArrayList(miniMaxEmbeddingEntity, text);
        System.out.println(miniMaxResList);
        System.out.println("************************************************************************");
        List<float[]> ollamaResList = aiEmbeddingService.embedToArrayList(ollamaEmbeddingEntity, text);
        System.out.println(ollamaResList);
        System.out.println("************************************************************************");
        List<float[]> zhiPuAiResList = aiEmbeddingService.embedToArrayList(zhiPuAiEmbeddingEntity, text);
        System.out.println(zhiPuAiResList);
    }

    @Test
    public void testEmbedToEmbeddingList() {
        OpenAiEmbeddingEntity openAiEmbeddingEntity = new OpenAiEmbeddingEntity(OpenAiApi.EmbeddingModel.TEXT_EMBEDDING_ADA_002, "xxx-xxx");
        AzureOpenAiEmbeddingEntity azureOpenAiEmbeddingEntity = new AzureOpenAiEmbeddingEntity("text-embedding-3-small", OpenAIServiceVersion.getLatest(), "xxx", "https://xxx-xxx-xxx.cognitiveservices.azure.com/");
        MistralAiEmbeddingEntity mistralAiEmbeddingEntity = new MistralAiEmbeddingEntity(MistralAiApi.EmbeddingModel.EMBED, "xxx");
        MiniMaxEmbeddingEntity miniMaxEmbeddingEntity = new MiniMaxEmbeddingEntity(MiniMaxApi.EmbeddingModel.Embo_01, "xxx.xxx");
        OllamaEmbeddingEntity ollamaEmbeddingEntity = new OllamaEmbeddingEntity("nomic-embed-text", "http://localhost:11434");
        ZhiPuAiEmbeddingEntity zhiPuAiEmbeddingEntity = new ZhiPuAiEmbeddingEntity(ZhiPuAiApi.EmbeddingModel.Embedding_3, "xxx.xxx");
        String[] text = {"Hello,World!", "你好，世界！"};
        List<Embedding> openAiResList = aiEmbeddingService.embedToEmbeddingList(openAiEmbeddingEntity, text);
        System.out.println(openAiResList);
        System.out.println("************************************************************************");
        List<Embedding> azureOpenAiResList = aiEmbeddingService.embedToEmbeddingList(azureOpenAiEmbeddingEntity, text);
        System.out.println(azureOpenAiResList);
        System.out.println("************************************************************************");
        List<Embedding> mistralAiResList = aiEmbeddingService.embedToEmbeddingList(mistralAiEmbeddingEntity, text);
        System.out.println(mistralAiResList);
        System.out.println("************************************************************************");
        List<Embedding> miniMaxResList = aiEmbeddingService.embedToEmbeddingList(miniMaxEmbeddingEntity, text);
        System.out.println(miniMaxResList);
        System.out.println("************************************************************************");
        List<Embedding> ollamaResList = aiEmbeddingService.embedToEmbeddingList(ollamaEmbeddingEntity, text);
        System.out.println(ollamaResList);
        System.out.println("************************************************************************");
        List<Embedding> zhiPuAiResList = aiEmbeddingService.embedToEmbeddingList(zhiPuAiEmbeddingEntity, text);
        System.out.println(zhiPuAiResList);
    }

    @Test
    public void testEmbedToResult() {
        OpenAiEmbeddingEntity openAiEmbeddingEntity = new OpenAiEmbeddingEntity(OpenAiApi.EmbeddingModel.TEXT_EMBEDDING_ADA_002, "xxx-xxx");
        AzureOpenAiEmbeddingEntity azureOpenAiEmbeddingEntity = new AzureOpenAiEmbeddingEntity("text-embedding-3-small", OpenAIServiceVersion.getLatest(), "xxx", "https://xxx-xxx-xxx.cognitiveservices.azure.com/");
        MistralAiEmbeddingEntity mistralAiEmbeddingEntity = new MistralAiEmbeddingEntity(MistralAiApi.EmbeddingModel.EMBED, "xxx");
        MiniMaxEmbeddingEntity miniMaxEmbeddingEntity = new MiniMaxEmbeddingEntity(MiniMaxApi.EmbeddingModel.Embo_01, "xxx.xxx");
        OllamaEmbeddingEntity ollamaEmbeddingEntity = new OllamaEmbeddingEntity("nomic-embed-text", "http://localhost:11434");
        ZhiPuAiEmbeddingEntity zhiPuAiEmbeddingEntity = new ZhiPuAiEmbeddingEntity(ZhiPuAiApi.EmbeddingModel.Embedding_3, "xxx.xxx");
        String[] text = {"Hello,World!", "你好，世界！"};
        EmbeddingResult openAiRes = aiEmbeddingService.embedToResult(openAiEmbeddingEntity, text);
        System.out.println(openAiRes);
        System.out.println("************************************************************************");
        EmbeddingResult azureOpenAiRes = aiEmbeddingService.embedToResult(azureOpenAiEmbeddingEntity, text);
        System.out.println(azureOpenAiRes);
        System.out.println("************************************************************************");
        EmbeddingResult mistralAiRes = aiEmbeddingService.embedToResult(mistralAiEmbeddingEntity, text);
        System.out.println(mistralAiRes);
        System.out.println("************************************************************************");
        EmbeddingResult miniMaxRes = aiEmbeddingService.embedToResult(miniMaxEmbeddingEntity, text);
        System.out.println(miniMaxRes);
        System.out.println("************************************************************************");
        EmbeddingResult ollamaRes = aiEmbeddingService.embedToResult(ollamaEmbeddingEntity, text);
        System.out.println(ollamaRes);
        System.out.println("************************************************************************");
        EmbeddingResult zhiPuAiRes = aiEmbeddingService.embedToResult(zhiPuAiEmbeddingEntity, text);
        System.out.println(zhiPuAiRes);
    }

}
