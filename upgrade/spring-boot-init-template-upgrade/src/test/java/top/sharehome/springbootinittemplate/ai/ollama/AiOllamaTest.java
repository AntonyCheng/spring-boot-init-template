package top.sharehome.springbootinittemplate.ai.ollama;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 切点参数为execution型的切面测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class AiOllamaTest {

    @Resource
    private OllamaChatClient ollamaChatClient;

    @Test
    public void testAiOllama(){
        ChatResponse call = ollamaChatClient.call(new Prompt(List.of(new SystemMessage("你是一个非常伟大的计算机科学家，名字叫做AntonyCheng"), new UserMessage("你的名字叫什么"))));
        System.out.println(call.toString());
    }

}
