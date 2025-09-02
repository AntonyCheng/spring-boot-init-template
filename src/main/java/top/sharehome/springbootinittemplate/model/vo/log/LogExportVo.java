package top.sharehome.springbootinittemplate.model.vo.log;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员导出日志表格Vo类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogExportVo implements Serializable {

    /**
     * ID
     */
    @ExcelProperty(value = "日志ID")
    private Long id;

    /**
     * 接口URI
     */
    @ExcelProperty(value = "接口URI")
    private String uri;

    /**
     * 操作描述
     */
    @ExcelProperty(value = "操作描述")
    private String description;

    /**
     * 操作类型（0其他1增2删3查4改5导入6导出）
     */
    @ExcelProperty(value = "操作类型")
    private String operator;

    /**
     * 请求方法（RESTFul风格）
     */
    @ExcelProperty(value = "请求方法")
    private String requestMethod;

    /**
     * 方法名称
     */
    @ExcelProperty(value = "方法名称")
    private String method;

    /**
     * 操作用户账号
     */
    @ExcelProperty(value = "操作用户账号")
    private String userAccount;

    /**
     * 操作用户IP
     */
    @ExcelProperty(value = "操作用户IP")
    private String ip;

    /**
     * 操作用户地点
     */
    @ExcelProperty(value = "操作用户地点")
    private String location;

    /**
     * 操作参数
     */
    @ExcelProperty(value = "操作参数")
    private String param;

    /**
     * 操作结果（0正常1异常）
     */
    @ExcelProperty(value = "操作结果")
    private String result;

    /**
     * 响应内容
     */
    @ExcelProperty(value = "响应内容")
    private String json;

    /**
     * 接口访问耗时
     */
    @ExcelProperty(value = "接口访问耗时（ms）")
    private Long time;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @Serial
    private static final long serialVersionUID = -2789791299561159518L;

}
