package top.sharehome.springbootinittemplate.config.ai.spring.etl.reader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.jsoup.config.JsoupDocumentReaderConfig;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * 文档读取器
 *
 * @author AntonyCheng
 */
public class DocumentReader {

    /**
     * 读取JSON
     *
     * @param inputStream   文档输入流
     * @param keys          需要提取的key
     */
    public static List<Document> readJson(InputStream inputStream, String... keys) {
        if (Objects.isNull(inputStream)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[inputStream]不能为空");
        }
        Resource resource = new InputStreamResource(inputStream);
        return new JsonReader(resource, keys).get();
    }

    /**
     * 读取Text
     *
     * @param inputStream   文档输入流
     */
    public static List<Document> readText(InputStream inputStream) {
        if (Objects.isNull(inputStream)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[inputStream]不能为空");
        }
        Resource resource = new InputStreamResource(inputStream);
        return new TextReader(resource).get();
    }

    /**
     * 读取HTML
     *
     * @param inputStream   文档输入流
     * @param selector      CSS选择器，默认body
     * @param charset       文档编码，默认UTF-8
     */
    public static List<Document> readHtml(InputStream inputStream, String selector, String charset) {
        if (Objects.isNull(inputStream)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[inputStream]不能为空");
        }
        Resource resource = new InputStreamResource(inputStream);
        JsoupDocumentReaderConfig config = JsoupDocumentReaderConfig.builder()
                .selector(StringUtils.isBlank(selector) ? "body" : selector)
                .charset(StringUtils.isBlank(charset) ? StandardCharsets.UTF_8.name() : charset)
                .includeLinkUrls(true)
                .build();
        return new JsoupDocumentReader(resource, config).get();
    }

    /**
     * 读取Markdown
     *
     * @param inputStream       文档输入流
     * @param includeHorizontal 水平线是否独立存在，默认false
     * @param includeCodeBlock  代码块是否独立存在，默认false
     * @param includeBlockquote 引用是否独立存在，默认false
     */
    public static List<Document> readMarkdown(InputStream inputStream, Boolean includeHorizontal, Boolean includeCodeBlock, Boolean includeBlockquote) {
        if (Objects.isNull(inputStream)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[inputStream]不能为空");
        }
        Resource resource = new InputStreamResource(inputStream);
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(Objects.isNull(includeHorizontal) ? Boolean.FALSE : includeHorizontal)
                .withIncludeCodeBlock(Objects.isNull(includeCodeBlock) ? Boolean.FALSE : includeHorizontal)
                .withIncludeBlockquote(Objects.isNull(includeBlockquote) ? Boolean.FALSE : includeHorizontal)
                .build();
        return new MarkdownDocumentReader(resource, config).get();
    }

    /**
     * 读取PDF
     *
     * @param inputStream   文档输入流
     */
    public static List<Document> readPdf(InputStream inputStream) {
        if (Objects.isNull(inputStream)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[inputStream]不能为空");
        }
        Resource resource = new InputStreamResource(inputStream);
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(
                        ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .withLeftAlignment(true)
                                .build()
                )
                .withPagesPerDocument(1)
                .build();
        return new PagePdfDocumentReader(resource, config).get();
    }

    /**
     * 读取Tika所能够解析的文档，将所有内容放入一个Document
     *
     * @param inputStream   文档输入流
     */
    public static List<Document> readTika(InputStream inputStream) {
        if (Objects.isNull(inputStream)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[inputStream]不能为空");
        }
        Resource resource = new InputStreamResource(inputStream);
        return new TikaDocumentReader(resource).get();
    }

}
