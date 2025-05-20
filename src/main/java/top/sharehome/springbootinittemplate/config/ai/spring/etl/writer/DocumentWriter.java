package top.sharehome.springbootinittemplate.config.ai.spring.etl.writer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.writer.FileDocumentWriter;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.List;
import java.util.Objects;

/**
 * 文档写入器
 *
 * @author AntonyCheng
 */
public class DocumentWriter {

    /**
     * 将文本写入文件
     *
     * @param documents     目标文本
     * @param fileName      目标文件路径
     * @param isAppend      是否尾部追加
     */
    public static void writeFile(List<Document> documents, String fileName, Boolean isAppend) {
        if (CollectionUtils.isEmpty(documents)){
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[documents]不能为空");
        }
        if (StringUtils.isBlank(fileName)){
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[fileName]不能为空");
        }
        FileDocumentWriter writer = new FileDocumentWriter(
                fileName,
                true,
                MetadataMode.ALL,
                Objects.nonNull(isAppend) && isAppend);
        writer.accept(documents);
    }

}
