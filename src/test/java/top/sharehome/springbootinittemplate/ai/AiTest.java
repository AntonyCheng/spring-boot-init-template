package top.sharehome.springbootinittemplate.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring AI测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class AiTest {

    @Resource
    private OllamaChatClient ollamaChatClient;

    @Test
    public void testOllamaAi() {
        System.out.println(ollamaChatClient.call("你好"));
    }

}
