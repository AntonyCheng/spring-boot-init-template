//package top.sharehome.springbootinittemplate.config.ai.ollama;
//
//import jakarta.annotation.PostConstruct;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.ollama.OllamaChatClient;
//import org.springframework.ai.ollama.api.OllamaApi;
//import org.springframework.ai.ollama.api.OllamaOptions;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Conditional;
//import org.springframework.context.annotation.Configuration;
//import top.sharehome.springbootinittemplate.config.ai.ollama.condition.AiOllamaCondition;
//import top.sharehome.springbootinittemplate.config.ai.ollama.properties.AiOllamaProperties;
//
///**
// * AI Ollama配置
// *
// * @author AntonyCheng
// */
//@Configuration
//@EnableConfigurationProperties(AiOllamaProperties.class)
//@AllArgsConstructor
//@Slf4j
//@Conditional(AiOllamaCondition.class)
//public class AiOllamaConfiguration {
//
//    private final AiOllamaProperties aiOllamaProperties;
//
////    @Bean
//    public OllamaChatClient ollamaChatClient() {
//        OllamaApi ollamaApi = new OllamaApi(aiOllamaProperties.getUrl());
//        return new OllamaChatClient(ollamaApi)
//                .withDefaultOptions(OllamaOptions.create()
//                        .withModel(aiOllamaProperties.getModelName())
//                        .withTemperature(aiOllamaProperties.getTemperature())
//                        .withTopK(aiOllamaProperties.getTopK())
//                        .withTopP(aiOllamaProperties.getTopP())
//                        .withRepeatPenalty(aiOllamaProperties.getRepeatPenalty()));
//    }
//
//    /**
//     * 依赖注入日志输出
//     */
//    @PostConstruct
//    private void initDi() {
//        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
//    }
//
//}
