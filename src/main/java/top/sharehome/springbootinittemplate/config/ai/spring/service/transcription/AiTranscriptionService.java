package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription;

import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.TranscriptionModelBase;

import java.io.File;
import java.io.InputStream;

/**
 * AI Transcription服务接口
 *
 * @author AntonyCheng
 */
public interface AiTranscriptionService {

    /**
     * AI转录功能
     *
     * @param model transcription模型信息
     * @param file  输入文件
     */
    String transcribe(TranscriptionModelBase model, File file);

    /**
     * AI转录功能
     *
     * @param model transcription模型信息
     * @param file  输入文件
     */
    String transcribe(TranscriptionModelBase model, MultipartFile file);

    /**
     * AI转录功能
     *
     * @param model transcription模型信息
     * @param bytes 输入字节
     */
    String transcribe(TranscriptionModelBase model, byte[] bytes,String fileName);

    /**
     * AI转录功能
     *
     * @param model         transcription模型信息
     * @param inputStream   输入流
     */
    String transcribe(TranscriptionModelBase model, InputStream inputStream,String fileName);

}
