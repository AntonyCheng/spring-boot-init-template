package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.AbstractResource;
import org.springframework.lang.Nullable;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Transcription模型资源处理类
 *
 * @author AntonyCheng
 */
public class TranscriptionResource extends AbstractResource {

    private final InputStream inputStream;

    private final String description;

    private final String fileName;

    private boolean read = false;

    public TranscriptionResource(InputStream inputStream, String fileName) {
        this(inputStream, "", fileName);
    }

    public TranscriptionResource(InputStream inputStream, @Nullable String description, String fileName) {
        if (Objects.isNull(inputStream)) {
            throw new CustomizeReturnException(ReturnCode.FAIL, "数据流为空");
        }
        this.inputStream = inputStream;
        this.description = description != null ? description : "";
        this.fileName = StringUtils.isNotBlank(fileName) ? fileName : "fileName";
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public InputStream getInputStream() throws IOException, IllegalStateException {
        if (this.read) {
            throw new IllegalStateException("数据流无法重复读取");
        }
        this.read = true;
        return this.inputStream;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getFilename() {
        return this.fileName;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || (other instanceof TranscriptionResource that &&
                                  this.inputStream.equals(that.inputStream)));
    }

    @Override
    public int hashCode() {
        return this.inputStream.hashCode();
    }

}
