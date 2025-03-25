package top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * TTS结果集
 *
 * @author AntonyCheng
 */
@Data
@Accessors(chain = true)
public class TtsResult implements Serializable {

    /**
     * 结果
     */
    private byte[] result;

    /**
     * 耗时（单位：毫秒）
     */
    private Long takeTime;

    /**
     * 文本内容
     */
    private String text;

    public TtsResult(byte[] result, Long takeTime, String text) {
        this.result = result;
        this.takeTime = takeTime;
        this.text = text;
    }

    @Serial
    private static final long serialVersionUID = 1469912701637251218L;

}
