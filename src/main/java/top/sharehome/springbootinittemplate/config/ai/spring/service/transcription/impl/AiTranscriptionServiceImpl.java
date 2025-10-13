package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.impl;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Arrays;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.AiTranscriptionService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.manager.TranscriptionManager;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.TranscriptionModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.resource.TranscriptionResource;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.*;
import java.util.Objects;

/**
 * AI Transcription服务接口实现类
 *
 * @author AntonyCheng
 */
@Service
public class AiTranscriptionServiceImpl implements AiTranscriptionService {

    @Override
    public String transcribe(TranscriptionModelBase model, File file) {
        if (Objects.isNull(file) || !file.exists()) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[file]不能为空");
        }
        try (FileInputStream inputStream = new FileInputStream(file);) {
            return transcribe(model, inputStream, file.getName());
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[file]存在异常");
        }
    }

    @Override
    public String transcribe(TranscriptionModelBase model, MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[file]不能为空");
        }
        try (InputStream inputStream = file.getInputStream()) {
            return transcribe(model, inputStream, StringUtils.isNotBlank(file.getOriginalFilename()) ? file.getOriginalFilename() : file.getName());
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[file]存在异常");
        }
    }

    @Override
    public String transcribe(TranscriptionModelBase model, byte[] bytes, String fileName) {
        if (Arrays.isNullOrEmpty(bytes)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[bytes]不能为空");
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            return transcribe(model, inputStream,fileName);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[bytes]存在异常");
        }
    }

    @Override
    public String transcribe(TranscriptionModelBase model, InputStream inputStream, String fileName) {
        if (Objects.isNull(inputStream)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[inputStream]不能为空");
        }
        return TranscriptionManager.getImageModel(model)
                .call(new AudioTranscriptionPrompt(new TranscriptionResource(inputStream, fileName)))
                .getResult()
                .getOutput();
    }

}
