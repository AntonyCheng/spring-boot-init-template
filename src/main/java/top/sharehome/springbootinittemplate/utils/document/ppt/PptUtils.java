package top.sharehome.springbootinittemplate.utils.document.ppt;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.*;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeDocumentException;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * PPT工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class PptUtils {

    /**
     * 写入PPT数据内部类
     */
    public static class Writer {

        /**
         * 初始化文档
         */
        private final XMLSlideShow document;

        /**
         * 幻灯片管理
         */
        private final List<XSLFSlide> slideList;

        /**
         * 当前幻灯片索引
         */
        private int slideIndex;

        /**
         * 写入PPT数据内部类构造器
         */
        public Writer() {
            document = new XMLSlideShow();
            this.slideList = new ArrayList<>();
            // 因为是索引，从0开始，所以初始化为-1
            this.slideIndex = -1;
        }

        /**
         * 添加幻灯片
         */
        public Writer addSlide() {
            // 创建新页面
            XSLFSlide slide = document.createSlide();
            // 添加到Slide列表中
            slideList.add(slide);
            // Slide页码索引加一
            this.slideIndex++;
            return this;
        }

        /**
         * 添加文本
         *
         * @param textarea 文本内容
         */
        public Writer addTextarea(String textarea) {
            return addTextarea(textarea, null, null, null, null, null);
        }

        /**
         * 添加文本
         *
         * @param textarea 文本内容
         * @param position 文本位置
         */
        public Writer addTextarea(String textarea, Rectangle2D.Double position) {
            return addTextarea(textarea, position, null, null, null, null);
        }

        /**
         * 添加文本
         *
         * @param textarea 文本内容
         * @param position 文本位置
         * @param fontSize 字号
         */
        public Writer addTextarea(String textarea, Rectangle2D.Double position, Double fontSize) {
            return addTextarea(textarea, position, fontSize, null, null, null);
        }

        /**
         * 添加文本
         *
         * @param textarea 文本内容
         * @param position 文本位置
         * @param fontSize 字号
         * @param fontColor 字体颜色
         */
        public Writer addTextarea(String textarea, Rectangle2D.Double position, Double fontSize, Color fontColor) {
            return addTextarea(textarea, position, fontSize, fontColor, null, null);
        }

        /**
         * 添加文本
         *
         * @param textarea 文本内容
         * @param position 文本位置
         * @param fontSize 字号
         * @param fontColor 字体颜色
         * @param isBold 是否粗体
         * @param isItalic 是否斜体
         */
        public Writer addTextarea(String textarea, Rectangle2D.Double position, Double fontSize, Color fontColor, Boolean isBold, Boolean isItalic) {
            return addTextarea(
                    new PptTextarea()
                            .setTextContent(textarea)
                            .setPosition(position)
                            .setFontSize(fontSize)
                            .setFontColor(fontColor)
                            .setIsBold(isBold)
                            .setIsItalic(isItalic)
            );
        }

        /**
         * 添加文本
         *
         * @param pptTextarea PPT文本构造类
         */
        public Writer addTextarea(PptTextarea pptTextarea) {
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (slideList.isEmpty()) {
                addSlide();
            }
            // 创建文本框
            XSLFTextBox textBox = slideList.get(slideIndex).createTextBox();
            // 设置文本位置，默认x0y0h0w0
            textBox.setAnchor(Objects.isNull(pptTextarea.getPosition()) ? new Rectangle2D.Double(0, 0, 720, 60) : pptTextarea.getPosition());
            // 清空原本存在的""内容
            textBox.clearText();
            // 设置文本对齐初始位置
            textBox.setVerticalAlignment(VerticalAlignment.TOP);
            XSLFTextRun textRun = textBox.addNewTextParagraph().addNewTextRun();
            // 设置文本
            textRun.setText(Objects.isNull(pptTextarea.getTextContent()) ? "" : pptTextarea.getTextContent());
            // 设置文本字体，字体默认SimHei，即黑体
            textRun.setFontFamily(Objects.isNull(pptTextarea.getFontFamily()) ? "SimHei" : pptTextarea.getFontFamily());
            // 设置文本字号，默认25.0
            textRun.setFontSize(Objects.isNull(pptTextarea.getFontSize()) ? 18 : pptTextarea.getFontSize());
            // 设置文本颜色，默认黑色
            textRun.setFontColor(Objects.isNull(pptTextarea.getFontColor()) ? Color.BLACK : pptTextarea.getFontColor());
            // 设置文本是否粗体，默认否
            textRun.setBold(Objects.isNull(pptTextarea.getIsBold()) ? Boolean.FALSE : pptTextarea.getIsBold());
            // 设置文本是否斜体，默认否
            textRun.setItalic(Objects.isNull(pptTextarea.getIsItalic()) ? Boolean.FALSE : pptTextarea.getIsItalic());
            // 设置文本是否有下划线，默认否
            textRun.setUnderlined(Objects.isNull(pptTextarea.getIsUnderline()) ? Boolean.FALSE : pptTextarea.getIsUnderline());
            return this;
        }

        /**
         * 将PPT写入响应流
         *
         * @param fileName 响应文件名
         * @param response 响应流
         */
        public void doWrite(String fileName, HttpServletResponse response) {
            try {
                handlePptResponse(fileName, response);
                ServletOutputStream outputStream = response.getOutputStream();
                doWrite(outputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 将PPT写入输出流
         *
         * @param outputStream 输出流
         */
        public void doWrite(OutputStream outputStream) {
            try (outputStream) {
                document.write(outputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "写入输出流异常");
            }
        }

        /**
         * 处理ContentType是PPT格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private void handlePptResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName = StringUtils.isBlank(fileName) ?
                    UUID.randomUUID().toString().replace("-", "") + ".pptx" :
                    fileName + "_" + UUID.randomUUID().toString().replace("-", "") + ".pptx";

            String encodeName = URLEncoder.encode(realName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            String contentDispositionValue = "attachment; filename=" + encodeName + ";filename*=utf-8''" + encodeName;
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
            response.setHeader("Content-disposition", contentDispositionValue);
            response.setHeader("download-filename", encodeName);
            response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation;charset=UTF-8");
        }

    }

    /**
     * PPT文本构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PptTextarea {

        /**
         * 文本文字内容
         */
        private String textContent;

        /**
         * 文本位置
         */
        private Rectangle2D.Double position;

        /**
         * 文本字体
         */
        private String fontFamily;

        /**
         * 文本字号
         */
        private Double fontSize;

        /**
         * 文本文字颜色
         */
        private Color fontColor;

        /**
         * 文本是否加粗
         */
        private Boolean isBold;

        /**
         * 文本是否斜体
         */
        private Boolean isItalic;

        /**
         * 文本下划线类型
         */
        private Boolean isUnderline;

    }
}
