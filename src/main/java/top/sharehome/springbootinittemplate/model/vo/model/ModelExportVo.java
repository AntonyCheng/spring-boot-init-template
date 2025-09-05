package top.sharehome.springbootinittemplate.model.vo.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员导出模型表格Vo类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelExportVo implements Serializable {

    /**
     * 类型（对话-chat，向量-embedding，图片-image，语音转文字-transcription，文字转语音-tts）
     */
    private String type;

    @Serial
    private static final long serialVersionUID = -4537844212690141842L;

}
