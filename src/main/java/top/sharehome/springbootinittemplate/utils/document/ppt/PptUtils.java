package top.sharehome.springbootinittemplate.utils.document.ppt;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.sl.usermodel.TableCell;
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
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
         * @param rowNum 表格行数
         * @param columnNum 表格列数
         */
        public Writer addTable(PptTable.TableMap tableMap, Integer rowNum, Integer columnNum) {
            return addTable(
                    new PptTable()
                            .setTableMap(tableMap)
                            .setRowNum(rowNum)
                            .setColumnNum(columnNum)
            );
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
            return addTable(
                    new PptTable()
                            .setTableMap(tableMap)
                            .setColumnWidth(columnWidth)
                            .setRowNum(rowNum)
                            .setColumnNum(columnNum)
            );
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
            return addTable(
                    new PptTable()
                            .setTableMap(tableMap)
                            .setPosition(position)
                            .setRowNum(rowNum)
                            .setColumnNum(columnNum)
            );
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
                if (Objects.nonNull(cellData)){
                    for (int j = 0; j < cellData.size(); j++) {
                        XSLFTableCell cell = cells.get(j);
                        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                        cell.setText(cellData.get(j));
                    }
                }
            }
            // 设置表格列宽
            for (int i = 0; i < maxColumn; i++) {
                table.setColumnWidth(i, Objects.isNull(pptTable.getColumnWidth()) || pptTable.getColumnWidth() <= 0 ? (double) 720 /maxColumn : pptTable.getColumnWidth());
            }
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

}
