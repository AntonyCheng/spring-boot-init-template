package top.sharehome.springbootinittemplate.utils.document.ppt;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.support.ExcelTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeDocumentException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;

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
            if (Objects.isNull(pptTextarea)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "PptTextarea参数为空");
            }
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (slideList.isEmpty()) {
                addSlide();
            }
            // 创建文本框
            XSLFTextBox textBox = slideList.get(slideIndex).createTextBox();
            // 设置文本位置，默认x0y0w720h60
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
         * 添加表格
         *
         * @param tableMap 表格数据Map
         */
        public Writer addTable(PptTable.TableMap tableMap) {
            return addTable(tableMap, null, null, null, null);
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         * @param rowNum 表格行数
         * @param columnNum 表格列数
         */
        public Writer addTable(PptTable.TableMap tableMap, Integer rowNum, Integer columnNum) {
            return addTable(tableMap, null, null, rowNum, columnNum);
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         * @param columnWidth 表格列宽
         */
        public Writer addTable(PptTable.TableMap tableMap, Integer columnWidth) {
            return addTable(tableMap, null, columnWidth, null, null);
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         * @param columnWidth 表格列宽
         * @param rowNum 表格行数
         * @param columnNum 表格列数
         */
        public Writer addTable(PptTable.TableMap tableMap, Integer columnWidth, Integer rowNum, Integer columnNum) {
            return addTable(tableMap, null, columnWidth, rowNum, columnNum);
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         * @param position 表格位置
         */
        public Writer addTable(PptTable.TableMap tableMap, Rectangle2D.Double position) {
            return addTable(tableMap, position, null, null, null);
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         * @param position 表格位置
         * @param rowNum 表格行数
         * @param columnNum 表格列数
         */
        public Writer addTable(PptTable.TableMap tableMap, Rectangle2D.Double position, Integer rowNum, Integer columnNum) {
            return addTable(tableMap, position, null, rowNum, columnNum);
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         * @param position 表格位置
         * @param columnWidth 表格列宽
         */
        public Writer addTable(PptTable.TableMap tableMap, Rectangle2D.Double position, Integer columnWidth) {
            return addTable(tableMap, position, columnWidth, null, null);
        }

        /**
         * 添加表格
         *
         * @param tableMap 表格数据Map
         * @param position 表格位置
         * @param columnWidth 表格列宽
         * @param rowNum 表格行数
         * @param columnNum 表格列数
         */
        public Writer addTable(PptTable.TableMap tableMap, Rectangle2D.Double position, Integer columnWidth, Integer rowNum, Integer columnNum) {
            return addTable(
                    new PptTable()
                            .setTableMap(tableMap)
                            .setPosition(position)
                            .setColumnWidth(columnWidth)
                            .setRowNum(rowNum)
                            .setColumnNum(columnNum)
            );
        }

        /**
         * 添加表格
         *
         * @param pptTable PPT表格构造类
         */
        public Writer addTable(PptTable pptTable) {
            if (Objects.isNull(pptTable)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "PptTable参数为空");
            }
            // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
            if (slideList.isEmpty()) {
                addSlide();
            }
            // 如果输入预设行列值异常，那就直接返回即可
            if ((Objects.nonNull(pptTable.getRowNum()) && pptTable.getRowNum() <= 0) || (Objects.nonNull(pptTable.getColumnNum()) && pptTable.getColumnNum() <= 0)) {
                return this;
            }
            // 由表格内容获取表格本身的行列值
            Map<Integer, List<String>> map = pptTable.getTableMap().getMap();
            int maxRow = -1;
            int maxColumn = -1;
            for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
                if (entry.getKey() > maxRow) {
                    maxRow = entry.getKey();
                }
                if (entry.getValue().size() > maxColumn) {
                    maxColumn = entry.getValue().size();
                }
            }
            // 如果表格本身无行值，则说明表格内容为空，直接返回即可
            if (maxRow == -1) {
                return this;
            } else {
                // 先把行值从索引值改为真实数值
                maxRow++;
                // 如果表格本身有行值，那就和预设值作比较，判断预设值是否是无效值
                if (Objects.nonNull(pptTable.getRowNum()) && maxRow < pptTable.getRowNum()) {
                    maxRow = pptTable.getRowNum();
                }
                if (Objects.nonNull(pptTable.getColumnNum()) && maxColumn < pptTable.getColumnNum()) {
                    maxColumn = pptTable.getColumnNum();
                }
            }
            XSLFTable table = slideList.get(slideIndex).createTable(maxRow, maxColumn);
            // 设置文本位置，默认x0y0w0h0
            table.setAnchor(Objects.isNull(pptTable.getPosition()) ? new Rectangle2D.Double(0, 0, 720, 60) : pptTable.getPosition());
            // 填充表格
            List<XSLFTableRow> rows = table.getRows();
            for (int i = 0; i < rows.size(); i++) {
                List<XSLFTableCell> cells = rows.get(i).getCells();
                for (XSLFTableCell cell : cells) {
                    cell.setBorderColor(TableCell.BorderEdge.bottom, Color.black);
                    cell.setBorderColor(TableCell.BorderEdge.top, Color.black);
                    cell.setBorderColor(TableCell.BorderEdge.right, Color.black);
                    cell.setBorderColor(TableCell.BorderEdge.left, Color.black);
                }
                List<String> cellData = map.get(i);
                if (Objects.nonNull(cellData)) {
                    for (int j = 0; j < cellData.size(); j++) {
                        XSLFTableCell cell = cells.get(j);
                        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                        cell.setText(cellData.get(j));
                    }
                }
            }
            // 设置表格列宽
            for (int i = 0; i < maxColumn; i++) {
                table.setColumnWidth(i, Objects.isNull(pptTable.getColumnWidth()) || pptTable.getColumnWidth() <= 0 ? (double) 720 / maxColumn : pptTable.getColumnWidth());
            }
            return this;
        }

        /**
         * 添加图像
         *
         * @param multipartFile 图像文件
         */
        public Writer addImage(MultipartFile multipartFile) {
            return addImage(multipartFile, null);
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像数据输入流
         */
        public Writer addImage(InputStream inputStream) {
            return addImage(inputStream, null, null);
        }

        /**
         * 添加图像
         *
         * @param multipartFile 图像文件
         * @param position 图像位置及长宽
         */
        public Writer addImage(MultipartFile multipartFile, Rectangle2D.Double position) {
            try (InputStream inputStream = multipartFile.getInputStream()) {
                String extension = FilenameUtils.getExtension(StringUtils.isNotBlank(multipartFile.getOriginalFilename()) ? multipartFile.getOriginalFilename() : multipartFile.getName());
                PictureData.PictureType pictureType = PictureData.PictureType.valueOf((Objects.equals(extension, "jpg") ? "jpeg" : extension).toUpperCase());
                return addImage(inputStream, position, pictureType);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取图像数据流失败");
            }
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像数据输入流
         * @param position 图像位置及长宽
         */
        public Writer addImage(InputStream inputStream, Rectangle2D.Double position) {
            return addImage(inputStream, position, null);
        }

        /**
         * 添加图像
         *
         * @param inputStream 图像数据输入流
         * @param position 图像位置及长宽
         * @param imageExtension 图像类型
         */
        public Writer addImage(InputStream inputStream, Rectangle2D.Double position, PictureData.PictureType imageExtension) {
            return addImage(
                    new PptImage()
                            .setInputStream(inputStream)
                            .setPosition(position)
                            .setImageExtension(imageExtension)
            );
        }

        /**
         * 添加图像
         *
         * @param pptImage PPT图像构造类
         */
        public Writer addImage(PptImage pptImage) {
            try {
                if (Objects.isNull(pptImage)) {
                    throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "PptImage参数为空");
                }
                // 如果连数据流都没有，那就直接返回即可
                if (Objects.isNull(pptImage.getInputStream())) {
                    return this;
                } else {
                    pptImage.setInputStream(new ByteArrayInputStream(pptImage.getInputStream().readAllBytes()));
                }
                // 如果页面列表中无数据，则自动添加一页，以防开发者因忘记创建页面而出现异常
                if (slideList.isEmpty()) {
                    addSlide();
                }
                // 先获取图像信息
                int imgHeight = ImageIO.read(pptImage.getInputStream()).getHeight();
                pptImage.getInputStream().reset();
                int imgWidth = ImageIO.read(pptImage.getInputStream()).getWidth();
                pptImage.getInputStream().reset();
                // 添加图片
                XSLFPictureData pictureData = document.addPicture(pptImage.getInputStream(), Objects.isNull(pptImage.getImageExtension()) ? PictureData.PictureType.JPEG : pptImage.getImageExtension());
                XSLFPictureShape picture = slideList.get(slideIndex).createPicture(pictureData);
                picture.setAnchor(Objects.isNull(pptImage.getPosition()) ? new Rectangle2D.Double(0, 0, imgWidth, imgHeight) : pptImage.getPosition());
                return this;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "添加图像异常");
            }
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
     * 读取PPT数据内部类，适用基于pptx格式的文件类型（推荐使用）
     */
    public static class XMLReader {

        /**
         * 初始化文档
         */
        private final XMLSlideShow document;

        /**
         * 读取PPT数据内部类构造器
         */
        public XMLReader(InputStream inputStream) {
            try {
                if (Objects.isNull(inputStream) || Objects.equals(inputStream.available(), 0)) {
                    throw new IOException();
                }
                this.document = new XMLSlideShow(inputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "解析文档异常");
            }
        }

        /**
         * 获取PPT文档中的文本数据，并写入响应流
         *
         * @param response 响应
         */
        public void getTextareaResponse(HttpServletResponse response) {
            getTextareaResponse(null, null, response);
        }

        /**
         * 获取PPT文档中的文本数据，并写入响应流
         *
         * @param txtFileName TXT文件名
         * @param response 响应
         */
        public void getTextareaResponse(String txtFileName, HttpServletResponse response) {
            getTextareaResponse(null, txtFileName, response);
        }

        /**
         * 获取PPT文档中的文本数据，并写入响应流
         *
         * @param pageIndex PPT页码索引，从0开始
         * @param txtFileName TXT文件名
         * @param response 响应
         */
        public void getTextareaResponse(Integer pageIndex, String txtFileName, HttpServletResponse response) {
            if (Objects.isNull(response)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "响应为空");
            }
            // 获取响应中的响应流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 预处理TXT类型响应
                handleTxtResponse(txtFileName, response);
                getTextareaTxt(pageIndex, outputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 获取PPT文档中的文本数据，并写入输出流
         *
         * @param outputStream 输出流
         */
        public void getTextareaTxt(OutputStream outputStream) {
            getTextareaTxt(null, outputStream);
        }

        /**
         * 获取PPT文档中的文本数据，并写入输出流
         *
         * @param pageIndex PPT页码索引，从0开始
         * @param outputStream 输出流
         */
        public void getTextareaTxt(Integer pageIndex, OutputStream outputStream) {
            if (Objects.isNull(outputStream)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取文本数据时，输出流不能为空");
            }
            List<String> textareaList = getTextareaList(pageIndex);
            // 遍历文本列表，同时将数据传至输出流
            textareaList.forEach(textarea -> {
                try {
                    outputStream.write(textarea.getBytes(StandardCharsets.UTF_8));
                    outputStream.write('\n');
                } catch (IOException e) {
                    throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取文本数据时，写入输出流异常");
                }
            });
        }

        /**
         * 获取PPT文档中的文本数据
         */
        public List<String> getTextareaList() {
            return getTextareaList(null);
        }

        /**
         * 获取PPT文档中的文本数据
         *
         * @param pageIndex PPT页码索引，从0开始
         */
        public List<String> getTextareaList(Integer pageIndex) {
            try {
                // 创建结果集
                List<String> result = new ArrayList<>();
                List<XSLFSlide> slides = document.getSlides();
                if (Objects.isNull(pageIndex)) {
                    for (XSLFSlide slide : slides) {
                        for (XSLFShape shape : slide.getShapes()) {
                            if (shape instanceof XSLFTextShape textShape) {
                                String text = textShape.getText();
                                if (StringUtils.isNotBlank(text)) {
                                    result.add(text);
                                }
                            }
                        }
                    }
                } else {
                    if (slides.size() <= pageIndex || pageIndex < 0) {
                        throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的文本时，无法解析非法页码数");
                    }
                    XSLFSlide slide = slides.get(pageIndex);
                    for (XSLFShape shape : slide.getShapes()) {
                        if (shape instanceof XSLFTextShape textShape) {
                            String text = textShape.getText();
                            if (StringUtils.isNotBlank(text)) {
                                result.add(text);
                            }
                        }
                    }
                }
                document.close();
                return result;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取文本数据时，关闭输出流异常");
            }
        }

        /**
         * 获取PPT文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param response 响应
         */
        public void getImagesResponse(HttpServletResponse response) {
            getImagesResponse(null, response, null);
        }

        /**
         * 获取PPT文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         */
        public void getImagesResponse(String zipFileName, HttpServletResponse response) {
            getImagesResponse(zipFileName, response, null);
        }

        /**
         * 获取PPT文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         * @param zipLevel 压缩等级：-1~9，理论上等级越高，压缩效率越高，耗时越长
         */
        public void getImagesResponse(String zipFileName, HttpServletResponse response, Integer zipLevel) {
            if (Objects.isNull(response)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "响应为空");
            }
            // 获取响应中的响应流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 预处理ZIP类型响应
                handleZipResponse(zipFileName, response);
                getImagesZip(outputStream, zipLevel);
                // 刷新响应流
                outputStream.flush();
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 从PPT数据流中获取图像数据，压缩后输出到一个输出流中
         *
         * @param outputStream 输出流
         */
        public void getImagesZip(OutputStream outputStream) {
            getImagesZip(outputStream, null);
        }

        /**
         * 从PPT数据流中获取图像数据，压缩后输出到一个输出流中
         *
         * @param outputStream 输出流
         * @param zipLevel     压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getImagesZip(OutputStream outputStream, Integer zipLevel) {
            if (Objects.isNull(outputStream)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的图像时，压缩数据输出流不能为空");
            }
            // 构造ZIP文件输出流
            try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(outputStream)) {
                // 获取图像byte数组
                Map<String, List<byte[]>> imagesByteArray = getImagesByteArray();
                // 设置压缩等级
                zipArchiveOutputStream.setLevel(Objects.isNull(zipLevel) || zipLevel < -1 || zipLevel > 9 ? 5 : zipLevel);
                // 设置压缩方法
                zipArchiveOutputStream.setMethod(ZipEntry.DEFLATED);
                // 准备压缩计数和名称
                int index = 1;
                String uuid = UUID.randomUUID().toString().replace("-", "");
                for (Map.Entry<String, List<byte[]>> stringListEntry : imagesByteArray.entrySet()) {
                    // 根据图像扩展名进行遍历
                    String extension = stringListEntry.getKey();
                    List<byte[]> imageArrayList = stringListEntry.getValue();
                    // 将每张图像byte数组数据传至压缩输出流中
                    for (byte[] image : imageArrayList) {
                        String entryName = uuid + "_" + index + "." + extension;
                        ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                        zipArchiveOutputStream.putArchiveEntry(entry);
                        ByteBuf buf = Unpooled.copiedBuffer(image);
                        buf.readBytes(zipArchiveOutputStream, buf.readableBytes());
                        zipArchiveOutputStream.closeArchiveEntry();
                        index++;
                    }
                }
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的图像时，压缩发生异常");
            }
        }

        /**
         * 获取PPT文档中的图像数据，同时按照扩展名分类，并转换为byte[]集合类型
         */
        public Map<String, List<byte[]>> getImagesByteArray() {
            try {
                // 创建结果集
                Map<String, List<byte[]>> result = new HashMap<>();
                // 获取所有图像信息List
                List<XSLFPictureData> imageInfos = document.getPictureData();
                // 遍历图像信息
                for (XSLFPictureData imageInfo : imageInfos) {
                    // 获取图像byte[]数据
                    byte[] data = imageInfo.getData();
                    // 获取图像扩展名
                    String extension = imageInfo.suggestFileExtension();
                    // 封装结果集
                    if (Objects.isNull(result.get(extension))) {
                        ArrayList<byte[]> value = new ArrayList<>();
                        value.add(data);
                        result.put(extension, value);
                    } else {
                        result.get(extension).add(data);
                    }
                }
                // 关闭文档
                document.close();
                return result;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取图像数据时，关闭输出流异常");
            }
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param response      响应
         */
        public void getTablesResponse(HttpServletResponse response) {
            getTablesResponse(null, response, null, null, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param response      响应
         * @param pageIndex     PPT页码索引，从0开始
         */
        public void getTablesResponse(HttpServletResponse response, Integer pageIndex) {
            getTablesResponse(null, response, pageIndex, null, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response) {
            getTablesResponse(zipFileName, response, null, null, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         * @param pageIndex     PPT页码索引，从0开始
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, Integer pageIndex) {
            getTablesResponse(zipFileName, response, pageIndex, null, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         * @param excelType    表格格式
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, ExcelTypeEnum excelType) {
            getTablesResponse(zipFileName, response, null, excelType, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         * @param pageIndex     PPT页码索引，从0开始
         * @param excelType    表格格式
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, Integer pageIndex, ExcelTypeEnum excelType) {
            getTablesResponse(zipFileName, response, pageIndex, excelType, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         * @param excelType    表格格式
         * @param zipLevel     压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, ExcelTypeEnum excelType, Integer zipLevel) {
            getTablesResponse(zipFileName, response, null, excelType, zipLevel);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         * @param pageIndex     PPT页码索引，从0开始
         * @param excelType    表格格式
         * @param zipLevel     压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, Integer pageIndex, ExcelTypeEnum excelType, Integer zipLevel) {
            if (Objects.isNull(response)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "响应为空");
            }
            // 获取响应中的响应流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 预处理ZIP类型响应
                handleZipResponse(zipFileName, response);
                getTablesZip(outputStream, pageIndex, excelType, zipLevel);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 从PPT数据流中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream  输出流
         */
        public void getTablesZip(OutputStream outputStream) {
            getTablesZip(outputStream, null, null, null);
        }

        /**
         * 从PPT数据流中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream  输出流
         * @param pageIndex     PPT页码索引，从0开始
         */
        public void getTablesZip(OutputStream outputStream, Integer pageIndex) {
            getTablesZip(outputStream, pageIndex, null, null);
        }

        /**
         * 从PPT数据流中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream 输出流
         * @param excelType    表格格式
         */
        public void getTablesZip(OutputStream outputStream, ExcelTypeEnum excelType) {
            getTablesZip(outputStream, null, excelType, null);
        }

        /**
         * 从PPT数据流中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream  输出流
         * @param pageIndex     PPT页码索引，从0开始
         * @param excelType     表格格式
         */
        public void getTablesZip(OutputStream outputStream, Integer pageIndex, ExcelTypeEnum excelType) {
            getTablesZip(outputStream, pageIndex, excelType, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream 输出流
         * @param excelType    表格格式
         * @param zipLevel     压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getTablesZip(OutputStream outputStream, ExcelTypeEnum excelType, Integer zipLevel) {
            getTablesZip(outputStream, null, excelType, zipLevel);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream  输出流
         * @param pageIndex     PPT页码索引，从0开始
         * @param excelType     表格格式
         * @param zipLevel      压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getTablesZip(OutputStream outputStream, Integer pageIndex, ExcelTypeEnum excelType, Integer zipLevel) {
            if (Objects.isNull(outputStream)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的表格时，压缩数据输出流不能为空");
            }
            // 构造ZIP文件输出流
            try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(outputStream)) {
                // 获取图像byte数组
                List<byte[]> tables = getTablesByteArray(excelType, pageIndex);
                // 设置压缩等级
                zipArchiveOutputStream.setLevel(Objects.isNull(zipLevel) || zipLevel < -1 || zipLevel > 9 ? 5 : zipLevel);
                // 设置压缩方法
                zipArchiveOutputStream.setMethod(ZipEntry.DEFLATED);
                // 准备压缩计数和名称
                int index = 1;
                String uuid = UUID.randomUUID().toString().replace("-", "");
                // 将每个表格byte数组数据传至压缩输出流中
                for (byte[] picture : tables) {
                    String entryName = uuid + "_" + index + (Objects.isNull(excelType) ? ExcelTypeEnum.XLSX : excelType).getValue();
                    ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                    zipArchiveOutputStream.putArchiveEntry(entry);
                    ByteBuf buf = Unpooled.copiedBuffer(picture);
                    buf.readBytes(zipArchiveOutputStream, buf.readableBytes());
                    zipArchiveOutputStream.closeArchiveEntry();
                    index++;
                }
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的表格时，压缩发生异常");
            }
        }

        /**
         * 从PPT中获取表格数据流，默认封装成xlsx格式文件Byte[]数据
         * 注意：封装后得到xlsx文件不支持“合并”或者“拆分”的表格，即要求表格每行的列数和每列的行数均相同，否则导出得到的表格会不尽人意，如有这样的需求请使用getTablesText()拿到文本数据后自行填充
         *
         * @param excelType 封装后的Excel文件扩展名
         */
        public List<byte[]> getTablesByteArray(ExcelTypeEnum excelType) {
            return getTablesByteArray(excelType, null);
        }

        /**
         * 从PPT中获取表格数据流，默认封装成xlsx格式文件Byte[]数据
         * 注意：封装后得到xlsx文件不支持“合并”或者“拆分”的表格，即要求表格每行的列数和每列的行数均相同，否则导出得到的表格会不尽人意，如有这样的需求请使用getTablesText()拿到文本数据后自行填充
         *
         * @param excelType 封装后的Excel文件扩展名
         * @param pageIndex PPT页码索引，从0开始
         */
        public List<byte[]> getTablesByteArray(ExcelTypeEnum excelType, Integer pageIndex) {
            try {
                List<byte[]> result = new ArrayList<>();
                List<XSLFSlide> slides = document.getSlides();
                if (Objects.isNull(pageIndex)) {
                    for (XSLFSlide slide : slides) {
                        for (XSLFShape shape : slide.getShapes()) {
                            if (shape instanceof XSLFTable table) {
                                List<List<String>> tableList = new ArrayList<>();
                                for (XSLFTableRow row : table.getRows()) {
                                    List<String> rowData = new ArrayList<>();
                                    for (XSLFTableCell cell : row.getCells()) {
                                        rowData.add(Objects.nonNull(cell.getText()) ? cell.getText() : "");
                                    }
                                    tableList.add(rowData);
                                }
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                EasyExcel.write(byteArrayOutputStream).excelType(Objects.isNull(excelType) ? ExcelTypeEnum.XLSX : excelType).sheet().doWrite(tableList);
                                result.add(byteArrayOutputStream.toByteArray());
                            }
                        }
                    }
                } else {
                    if (slides.size() <= pageIndex || pageIndex < 0) {
                        throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的表格时，无法解析非法页码数");
                    }
                    XSLFSlide slide = slides.get(pageIndex);
                    for (XSLFShape shape : slide.getShapes()) {
                        if (shape instanceof XSLFTable table) {
                            List<List<String>> tableList = new ArrayList<>();
                            for (XSLFTableRow row : table.getRows()) {
                                List<String> rowData = new ArrayList<>();
                                for (XSLFTableCell cell : row.getCells()) {
                                    rowData.add(Objects.nonNull(cell.getText()) ? cell.getText() : "");
                                }
                                tableList.add(rowData);
                            }
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            EasyExcel.write(byteArrayOutputStream).excelType(Objects.isNull(excelType) ? ExcelTypeEnum.XLSX : excelType).sheet().doWrite(tableList);
                            result.add(byteArrayOutputStream.toByteArray());
                        }
                    }
                }
                // 关闭文档
                document.close();
                return result;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取表格数据时，关闭输出流异常");
            }
        }

        /**
         * 从PPT数据流中获取表格文本数据，并封装成TableMap类型对象列表
         */
        public List<PptTable.TableMap> getTablesMaps() {
            return getTablesMaps(null);
        }

        /**
         * 从PPT数据流中获取表格文本数据，并封装成TableMap类型对象列表
         *
         * @param pageIndex PPT页码索引，从0开始
         */
        public List<PptTable.TableMap> getTablesMaps(Integer pageIndex) {
            try {
                // 创建结果集
                List<PptTable.TableMap> result = new ArrayList<>();
                List<XSLFSlide> slides = document.getSlides();
                if (Objects.isNull(pageIndex)) {
                    for (XSLFSlide slide : slides) {
                        for (XSLFShape shape : slide.getShapes()) {
                            if (shape instanceof XSLFTable table) {
                                PptTable.TableMap tableMap = new PptTable.TableMap();
                                // 遍历表格行
                                for (XSLFTableRow row : table.getRows()) {
                                    List<String> rowData = new ArrayList<>();
                                    // 遍历行中的单元格
                                    for (XSLFTableCell cell : row.getCells()) {
                                        // 获取单元格文本内容
                                        rowData.add(Objects.nonNull(cell.getText()) ? cell.getText() : "");
                                    }
                                    tableMap.put(rowData);
                                }
                                result.add(tableMap);
                            }
                        }
                    }
                } else {
                    if (slides.size() <= pageIndex || pageIndex < 0) {
                        throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的表格时，无法解析非法页码数");
                    }
                    XSLFSlide slide = slides.get(pageIndex);
                    for (XSLFShape shape : slide.getShapes()) {
                        if (shape instanceof XSLFTable table) {
                            PptTable.TableMap tableMap = new PptTable.TableMap();
                            // 遍历表格行
                            for (XSLFTableRow row : table.getRows()) {
                                List<String> rowData = new ArrayList<>();
                                // 遍历行中的单元格
                                for (XSLFTableCell cell : row.getCells()) {
                                    // 获取单元格文本内容
                                    rowData.add(Objects.nonNull(cell.getText()) ? cell.getText() : "");
                                }
                                tableMap.put(rowData);
                            }
                            result.add(tableMap);
                        }
                    }
                }
                document.close();
                return result;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取文本数据时，关闭输出流异常");
            }
        }

        /**
         * 处理ContentType是Txt格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private void handleTxtResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName = null;
            if (StringUtils.isBlank(fileName)) {
                realName = UUID.randomUUID().toString().replace("-", "") + ".txt";
            } else {
                realName = fileName + "_" + UUID.randomUUID().toString().replace("-", "") + ".txt";
            }
            String encodeName = URLEncoder
                    .encode(realName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            String contentDispositionValue = "attachment; filename=" + encodeName + ";filename*=utf-8''" + encodeName;
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
            response.setHeader("Content-disposition", contentDispositionValue);
            response.setHeader("download-filename", encodeName);
            response.setContentType("text/plain;charset=UTF-8");
        }

        /**
         * 处理ContentType是Zip格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private void handleZipResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName = null;
            if (StringUtils.isBlank(fileName)) {
                realName = UUID.randomUUID().toString().replace("-", "") + ".zip";
            } else {
                realName = fileName + "_" + UUID.randomUUID().toString().replace("-", "") + ".zip";
            }
            String encodeName = URLEncoder
                    .encode(realName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            String contentDispositionValue = "attachment; filename=" + encodeName + ";filename*=utf-8''" + encodeName;
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
            response.setHeader("Content-disposition", contentDispositionValue);
            response.setHeader("download-filename", encodeName);
            response.setContentType("application/zip;charset=UTF-8");
        }

    }

    /**
     * 读取PPT数据内部类，适用基于ppt格式的文件类型
     */
    public static class HSLFReader {

        /**
         * 初始化文档
         */
        private final HSLFSlideShow document;

        /**
         * 读取PPT数据内部类构造器
         */
        public HSLFReader(InputStream inputStream) {
            try {
                if (Objects.isNull(inputStream) || Objects.equals(inputStream.available(), 0)) {
                    throw new IOException();
                }
                this.document = new HSLFSlideShow(inputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "解析文档异常");
            }
        }

        /**
         * 获取PPT文档中的文本数据，并写入响应流
         *
         * @param response 响应
         */
        public void getTextareaResponse(HttpServletResponse response) {
            getTextareaResponse(null, null, response);
        }

        /**
         * 获取PPT文档中的文本数据，并写入响应流
         *
         * @param txtFileName TXT文件名
         * @param response 响应
         */
        public void getTextareaResponse(String txtFileName, HttpServletResponse response) {
            getTextareaResponse(null, txtFileName, response);
        }

        /**
         * 获取PPT文档中的文本数据，并写入响应流
         *
         * @param pageIndex PPT页码索引，从0开始
         * @param txtFileName TXT文件名
         * @param response 响应
         */
        public void getTextareaResponse(Integer pageIndex, String txtFileName, HttpServletResponse response) {
            if (Objects.isNull(response)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "响应为空");
            }
            // 获取响应中的响应流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 预处理TXT类型响应
                handleTxtResponse(txtFileName, response);
                getTextareaTxt(pageIndex, outputStream);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 获取PPT文档中的文本数据，并写入输出流
         *
         * @param outputStream 输出流
         */
        public void getTextareaTxt(OutputStream outputStream) {
            getTextareaTxt(null, outputStream);
        }

        /**
         * 获取PPT文档中的文本数据，并写入输出流
         *
         * @param pageIndex PPT页码索引，从0开始
         * @param outputStream 输出流
         */
        public void getTextareaTxt(Integer pageIndex, OutputStream outputStream) {
            if (Objects.isNull(outputStream)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取文本数据时，输出流不能为空");
            }
            List<String> textareaList = getTextareaList(pageIndex);
            // 遍历文本列表，同时将数据传至输出流
            textareaList.forEach(textarea -> {
                try {
                    outputStream.write(textarea.getBytes(StandardCharsets.UTF_8));
                    outputStream.write('\n');
                } catch (IOException e) {
                    throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取文本数据时，写入输出流异常");
                }
            });
        }

        /**
         * 获取PPT文档中的文本数据
         */
        public List<String> getTextareaList() {
            return getTextareaList(null);
        }

        /**
         * 获取PPT文档中的文本数据
         *
         * @param pageIndex PPT页码索引，从0开始
         */
        public List<String> getTextareaList(Integer pageIndex) {
            try {
                // 创建结果集
                List<String> result = new ArrayList<>();
                List<HSLFSlide> slides = document.getSlides();
                if (Objects.isNull(pageIndex)) {
                    for (HSLFSlide slide : slides) {
                        for (HSLFShape shape : slide.getShapes()) {
                            if (shape instanceof HSLFTextShape textShape) {
                                String text = textShape.getText();
                                if (StringUtils.isNotBlank(text)) {
                                    result.add(text);
                                }
                            }
                        }
                    }
                } else {
                    if (slides.size() <= pageIndex || pageIndex < 0) {
                        throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的文本时，无法解析非法页码数");
                    }
                    HSLFSlide slide = slides.get(pageIndex);
                    for (HSLFShape shape : slide.getShapes()) {
                        if (shape instanceof HSLFTextShape textShape) {
                            String text = textShape.getText();
                            if (StringUtils.isNotBlank(text)) {
                                result.add(text);
                            }
                        }
                    }
                }
                document.close();
                return result;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取文本数据时，关闭输出流异常");
            }
        }

        /**
         * 获取PPT文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param response 响应
         */
        public void getImagesResponse(HttpServletResponse response) {
            getImagesResponse(null, response, null);
        }

        /**
         * 获取PPT文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         */
        public void getImagesResponse(String zipFileName, HttpServletResponse response) {
            getImagesResponse(zipFileName, response, null);
        }

        /**
         * 获取PPT文档中的图像数据，并且进行压缩，将压缩后的数据转为响应流
         *
         * @param zipFileName 压缩后的文件名
         * @param response 响应
         * @param zipLevel 压缩等级：-1~9，理论上等级越高，压缩效率越高，耗时越长
         */
        public void getImagesResponse(String zipFileName, HttpServletResponse response, Integer zipLevel) {
            if (Objects.isNull(response)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "响应为空");
            }
            // 获取响应中的响应流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 预处理ZIP类型响应
                handleZipResponse(zipFileName, response);
                getImagesZip(outputStream, zipLevel);
                // 刷新响应流
                outputStream.flush();
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 从PPT数据流中获取图像数据，压缩后输出到一个输出流中
         *
         * @param outputStream 输出流
         */
        public void getImagesZip(OutputStream outputStream) {
            getImagesZip(outputStream, null);
        }

        /**
         * 从PPT数据流中获取图像数据，压缩后输出到一个输出流中
         *
         * @param outputStream 输出流
         * @param zipLevel     压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getImagesZip(OutputStream outputStream, Integer zipLevel) {
            if (Objects.isNull(outputStream)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的图像时，压缩数据输出流不能为空");
            }
            // 构造ZIP文件输出流
            try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(outputStream)) {
                // 获取图像byte数组
                Map<String, List<byte[]>> imagesByteArray = getImagesByteArray();
                // 设置压缩等级
                zipArchiveOutputStream.setLevel(Objects.isNull(zipLevel) || zipLevel < -1 || zipLevel > 9 ? 5 : zipLevel);
                // 设置压缩方法
                zipArchiveOutputStream.setMethod(ZipEntry.DEFLATED);
                // 准备压缩计数和名称
                int index = 1;
                String uuid = UUID.randomUUID().toString().replace("-", "");
                for (Map.Entry<String, List<byte[]>> stringListEntry : imagesByteArray.entrySet()) {
                    // 根据图像扩展名进行遍历
                    String extension = stringListEntry.getKey();
                    List<byte[]> imageArrayList = stringListEntry.getValue();
                    // 将每张图像byte数组数据传至压缩输出流中
                    for (byte[] image : imageArrayList) {
                        String entryName = uuid + "_" + index + "." + extension;
                        ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                        zipArchiveOutputStream.putArchiveEntry(entry);
                        ByteBuf buf = Unpooled.copiedBuffer(image);
                        buf.readBytes(zipArchiveOutputStream, buf.readableBytes());
                        zipArchiveOutputStream.closeArchiveEntry();
                        index++;
                    }
                }
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的图像时，压缩发生异常");
            }
        }

        /**
         * 获取PPT文档中的图像数据，同时按照扩展名分类，并转换为byte[]集合类型
         */
        public Map<String, List<byte[]>> getImagesByteArray() {
            try {
                // 创建结果集
                Map<String, List<byte[]>> result = new HashMap<>();
                // 获取所有图像信息List
                List<HSLFPictureData> imageInfos = document.getPictureData();
                // 遍历图像信息
                for (HSLFPictureData imageInfo : imageInfos) {
                    // 获取图像byte[]数据
                    byte[] data = imageInfo.getData();
                    // 获取图像扩展名
                    String extension = imageInfo.getType().extension.replace(".", "");
                    // 封装结果集
                    if (Objects.isNull(result.get(extension))) {
                        ArrayList<byte[]> value = new ArrayList<>();
                        value.add(data);
                        result.put(extension, value);
                    } else {
                        result.get(extension).add(data);
                    }
                }
                // 关闭文档
                document.close();
                return result;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取图像数据时，关闭输出流异常");
            }
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param response      响应
         */
        public void getTablesResponse(HttpServletResponse response) {
            getTablesResponse(null, response, null, null, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param response      响应
         * @param pageIndex     PPT页码索引，从0开始
         */
        public void getTablesResponse(HttpServletResponse response, Integer pageIndex) {
            getTablesResponse(null, response, pageIndex, null, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response) {
            getTablesResponse(zipFileName, response, null, null, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         * @param pageIndex     PPT页码索引，从0开始
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, Integer pageIndex) {
            getTablesResponse(zipFileName, response, pageIndex, null, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         * @param excelType    表格格式
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, ExcelTypeEnum excelType) {
            getTablesResponse(zipFileName, response, null, excelType, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         * @param pageIndex     PPT页码索引，从0开始
         * @param excelType    表格格式
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, Integer pageIndex, ExcelTypeEnum excelType) {
            getTablesResponse(zipFileName, response, pageIndex, excelType, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         * @param excelType    表格格式
         * @param zipLevel     压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, ExcelTypeEnum excelType, Integer zipLevel) {
            getTablesResponse(zipFileName, response, null, excelType, zipLevel);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入响应流
         *
         * @param zipFileName   压缩后的文件名
         * @param response      响应
         * @param pageIndex     PPT页码索引，从0开始
         * @param excelType    表格格式
         * @param zipLevel     压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getTablesResponse(String zipFileName, HttpServletResponse response, Integer pageIndex, ExcelTypeEnum excelType, Integer zipLevel) {
            if (Objects.isNull(response)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "响应为空");
            }
            // 获取响应中的响应流
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                // 预处理ZIP类型响应
                handleZipResponse(zipFileName, response);
                getTablesZip(outputStream, pageIndex, excelType, zipLevel);
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取响应输出流异常");
            }
        }

        /**
         * 从PPT数据流中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream  输出流
         */
        public void getTablesZip(OutputStream outputStream) {
            getTablesZip(outputStream, null, null, null);
        }

        /**
         * 从PPT数据流中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream  输出流
         * @param pageIndex     PPT页码索引，从0开始
         */
        public void getTablesZip(OutputStream outputStream, Integer pageIndex) {
            getTablesZip(outputStream, pageIndex, null, null);
        }

        /**
         * 从PPT数据流中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream 输出流
         * @param excelType    表格格式
         */
        public void getTablesZip(OutputStream outputStream, ExcelTypeEnum excelType) {
            getTablesZip(outputStream, null, excelType, null);
        }

        /**
         * 从PPT数据流中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream  输出流
         * @param pageIndex     PPT页码索引，从0开始
         * @param excelType     表格格式
         */
        public void getTablesZip(OutputStream outputStream, Integer pageIndex, ExcelTypeEnum excelType) {
            getTablesZip(outputStream, pageIndex, excelType, null);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream 输出流
         * @param excelType    表格格式
         * @param zipLevel     压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getTablesZip(OutputStream outputStream, ExcelTypeEnum excelType, Integer zipLevel) {
            getTablesZip(outputStream, null, excelType, zipLevel);
        }

        /**
         * 从PPT中获取表格数据，将所有表格数据压缩后写入输出流
         *
         * @param outputStream  输出流
         * @param pageIndex     PPT页码索引，从0开始
         * @param excelType     表格格式
         * @param zipLevel      压缩等级-1~9，等级越高，压缩效率越高
         */
        public void getTablesZip(OutputStream outputStream, Integer pageIndex, ExcelTypeEnum excelType, Integer zipLevel) {
            if (Objects.isNull(outputStream)) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的表格时，压缩数据输出流不能为空");
            }
            // 构造ZIP文件输出流
            try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(outputStream)) {
                // 获取图像byte数组
                List<byte[]> tables = getTablesByteArray(excelType, pageIndex);
                // 设置压缩等级
                zipArchiveOutputStream.setLevel(Objects.isNull(zipLevel) || zipLevel < -1 || zipLevel > 9 ? 5 : zipLevel);
                // 设置压缩方法
                zipArchiveOutputStream.setMethod(ZipEntry.DEFLATED);
                // 准备压缩计数和名称
                int index = 1;
                String uuid = UUID.randomUUID().toString().replace("-", "");
                // 将每个表格byte数组数据传至压缩输出流中
                for (byte[] picture : tables) {
                    String entryName = uuid + "_" + index + (Objects.isNull(excelType) ? ExcelTypeEnum.XLSX : excelType).getValue();
                    ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                    zipArchiveOutputStream.putArchiveEntry(entry);
                    ByteBuf buf = Unpooled.copiedBuffer(picture);
                    buf.readBytes(zipArchiveOutputStream, buf.readableBytes());
                    zipArchiveOutputStream.closeArchiveEntry();
                    index++;
                }
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的表格时，压缩发生异常");
            }
        }

        /**
         * 从PPT中获取表格数据流，默认封装成xlsx格式文件Byte[]数据
         * 注意：封装后得到xlsx文件不支持“合并”或者“拆分”的表格，即要求表格每行的列数和每列的行数均相同，否则导出得到的表格会不尽人意，如有这样的需求请使用getTablesText()拿到文本数据后自行填充
         *
         * @param excelType 封装后的Excel文件扩展名
         */
        public List<byte[]> getTablesByteArray(ExcelTypeEnum excelType) {
            return getTablesByteArray(excelType, null);
        }

        /**
         * 从PPT中获取表格数据流，默认封装成xlsx格式文件Byte[]数据
         * 注意：封装后得到xlsx文件不支持“合并”或者“拆分”的表格，即要求表格每行的列数和每列的行数均相同，否则导出得到的表格会不尽人意，如有这样的需求请使用getTablesText()拿到文本数据后自行填充
         *
         * @param excelType 封装后的Excel文件扩展名
         * @param pageIndex PPT页码索引，从0开始
         */
        public List<byte[]> getTablesByteArray(ExcelTypeEnum excelType, Integer pageIndex) {
            try {
                List<byte[]> result = new ArrayList<>();
                List<HSLFSlide> slides = document.getSlides();
                if (Objects.isNull(pageIndex)) {
                    for (HSLFSlide slide : slides) {
                        for (HSLFShape shape : slide.getShapes()) {
                            if (shape instanceof HSLFTable table) {
                                List<List<String>> tableList = new ArrayList<>();
                                for (int row = 0; row < table.getNumberOfRows(); row++) {
                                    // 遍历行中的单元格
                                    List<String> rowData = new ArrayList<>();
                                    for (int col = 0; col < table.getNumberOfColumns(); col++) {
                                        // 获取单元格文本内容
                                        HSLFTableCell cell = table.getCell(row, col);
                                        rowData.add(Objects.nonNull(cell.getText()) ? cell.getText() : "");
                                    }
                                    tableList.add(rowData);
                                }
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                EasyExcel.write(byteArrayOutputStream).excelType(Objects.isNull(excelType) ? ExcelTypeEnum.XLSX : excelType).sheet().doWrite(tableList);
                                result.add(byteArrayOutputStream.toByteArray());
                            }
                        }
                    }
                } else {
                    if (slides.size() <= pageIndex || pageIndex < 0) {
                        throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的表格时，无法解析非法页码数");
                    }
                    HSLFSlide slide = slides.get(pageIndex);
                    for (HSLFShape shape : slide.getShapes()) {
                        if (shape instanceof HSLFTable table) {
                            List<List<String>> tableList = new ArrayList<>();
                            for (int row = 0; row < table.getNumberOfRows(); row++) {
                                // 遍历行中的单元格
                                List<String> rowData = new ArrayList<>();
                                for (int col = 0; col < table.getNumberOfColumns(); col++) {
                                    // 获取单元格文本内容
                                    HSLFTableCell cell = table.getCell(row, col);
                                    rowData.add(Objects.nonNull(cell.getText()) ? cell.getText() : "");
                                }
                                tableList.add(rowData);
                            }
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            EasyExcel.write(byteArrayOutputStream).excelType(Objects.isNull(excelType) ? ExcelTypeEnum.XLSX : excelType).sheet().doWrite(tableList);
                            result.add(byteArrayOutputStream.toByteArray());
                        }
                    }
                }
                // 关闭文档
                document.close();
                return result;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取表格数据时，关闭输出流异常");
            }
        }

        /**
         * 从PPT数据流中获取表格文本数据，并封装成TableMap类型对象列表
         */
        public List<PptTable.TableMap> getTablesMaps() {
            return getTablesMaps(null);
        }

        /**
         * 从PPT数据流中获取表格文本数据，并封装成TableMap类型对象列表
         *
         * @param pageIndex PPT页码索引，从0开始
         */
        public List<PptTable.TableMap> getTablesMaps(Integer pageIndex) {
            try {
                // 创建结果集
                List<PptTable.TableMap> result = new ArrayList<>();
                List<HSLFSlide> slides = document.getSlides();
                if (Objects.isNull(pageIndex)) {
                    for (HSLFSlide slide : slides) {
                        for (HSLFShape shape : slide.getShapes()) {
                            if (shape instanceof HSLFTable table) {
                                PptTable.TableMap tableMap = new PptTable.TableMap();
                                for (int row = 0; row < table.getNumberOfRows(); row++) {
                                    // 遍历行中的单元格
                                    List<String> rowData = new ArrayList<>();
                                    for (int col = 0; col < table.getNumberOfColumns(); col++) {
                                        // 获取单元格文本内容
                                        HSLFTableCell cell = table.getCell(row, col);
                                        rowData.add(Objects.nonNull(cell.getText()) ? cell.getText() : "");
                                    }
                                    tableMap.put(rowData);
                                }
                                result.add(tableMap);
                            }
                        }
                    }
                } else {
                    if (slides.size() <= pageIndex || pageIndex < 0) {
                        throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "读取PPT中的表格时，无法解析非法页码数");
                    }
                    HSLFSlide slide = slides.get(pageIndex);
                    for (HSLFShape shape : slide.getShapes()) {
                        if (shape instanceof HSLFTable table) {
                            PptTable.TableMap tableMap = new PptTable.TableMap();
                            for (int row = 0; row < table.getNumberOfRows(); row++) {
                                // 遍历行中的单元格
                                List<String> rowData = new ArrayList<>();
                                for (int col = 0; col < table.getNumberOfColumns(); col++) {
                                    // 获取单元格文本内容
                                    HSLFTableCell cell = table.getCell(row, col);
                                    rowData.add(Objects.nonNull(cell.getText()) ? cell.getText() : "");
                                }
                                tableMap.put(rowData);
                            }
                            result.add(tableMap);
                        }
                    }
                }
                document.close();
                return result;
            } catch (IOException e) {
                throw new CustomizeDocumentException(ReturnCode.PPT_FILE_ERROR, "获取文本数据时，关闭输出流异常");
            }
        }

        /**
         * 处理ContentType是Txt格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private void handleTxtResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName = null;
            if (StringUtils.isBlank(fileName)) {
                realName = UUID.randomUUID().toString().replace("-", "") + ".txt";
            } else {
                realName = fileName + "_" + UUID.randomUUID().toString().replace("-", "") + ".txt";
            }
            String encodeName = URLEncoder
                    .encode(realName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            String contentDispositionValue = "attachment; filename=" + encodeName + ";filename*=utf-8''" + encodeName;
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
            response.setHeader("Content-disposition", contentDispositionValue);
            response.setHeader("download-filename", encodeName);
            response.setContentType("text/plain;charset=UTF-8");
        }

        /**
         * 处理ContentType是Zip格式的响应
         *
         * @param fileName 文件名
         * @param response 响应
         */
        private void handleZipResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
            String realName = null;
            if (StringUtils.isBlank(fileName)) {
                realName = UUID.randomUUID().toString().replace("-", "") + ".zip";
            } else {
                realName = fileName + "_" + UUID.randomUUID().toString().replace("-", "") + ".zip";
            }
            String encodeName = URLEncoder
                    .encode(realName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            String contentDispositionValue = "attachment; filename=" + encodeName + ";filename*=utf-8''" + encodeName;
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
            response.setHeader("Content-disposition", contentDispositionValue);
            response.setHeader("download-filename", encodeName);
            response.setContentType("application/zip;charset=UTF-8");
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

    /**
     * PPT表格构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PptTable {

        /**
         * 表格数据Map
         */
        private PptUtils.PptTable.TableMap tableMap;

        /**
         * 表格位置
         */
        private Rectangle2D.Double position;

        /**
         * 表格行数
         */
        private Integer rowNum;

        /**
         * 表格列数
         */
        private Integer columnNum;

        /**
         * 表格列宽
         */
        private Integer columnWidth;

        /**
         * 表格Map类
         */
        public static class TableMap {

            @Getter
            private Map<Integer, List<String>> map;

            private AtomicInteger index;

            public TableMap() {
                map = new HashMap<>();
                index = new AtomicInteger(0);
            }

            /**
             * 增加
             *
             * @param rowContent 行内容
             */
            public void put(List<String> rowContent) {
                if (Objects.isNull(rowContent)) {
                    rowContent = new ArrayList<>();
                }
                map.put(index.getAndIncrement(), rowContent);
            }

            /**
             * 删除
             *
             * @param index 行索引
             * @return 返回最大行索引，若被删除行索引无效则返回-1
             */
            public int remove(Integer index) {
                if (Objects.isNull(map.remove(index))) {
                    return -1;
                }
                HashMap<Integer, List<String>> newMap = new HashMap<>();
                AtomicInteger newIndex = new AtomicInteger(0);
                for (List<String> value : map.values()) {
                    newMap.put(newIndex.getAndIncrement(), value);
                }
                this.map = newMap;
                this.index = newIndex;
                return this.index.intValue() - 1;
            }

            /**
             * 替换
             *
             * @param index      行索引
             * @param rowContent 行内容
             * @return 返回替换结果，若被替换行索引无效则返回false，其余情况返回true
             */
            public boolean replace(Integer index, List<String> rowContent) {
                if (Objects.isNull(rowContent)) {
                    rowContent = new ArrayList<>();
                }
                return Objects.nonNull(map.replace(index, rowContent));
            }

        }
    }

    /**
     * PPT图像构造类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PptImage {

        /**
         * 图像数据输入流
         */
        private InputStream inputStream;

        /**
         * 图像位置及长宽
         */
        private Rectangle2D.Double position;

        /**
         * 图像类型
         */
        private PictureData.PictureType imageExtension;

    }

}
