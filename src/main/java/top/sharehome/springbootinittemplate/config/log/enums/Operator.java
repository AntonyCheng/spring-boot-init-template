package top.sharehome.springbootinittemplate.config.log.enums;

import com.google.common.base.Objects;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 日志操作类型枚举类
 *
 * @author AntonyCheng
 */
@Getter
public enum Operator {

    OTHER("其他操作", 0),

    INSERT("新增操作", 1),

    DELETE("删除操作", 2),

    QUERY("查询操作", 3),

    UPDATE("修改操作", 4),

    IMPORT("导入操作", 5),

    EXPORT("导出操作", 6);

    private final String OperatorLabel;

    private final Integer OperatorValue;

    Operator(String operatorLabel, Integer operatorValue) {
        this.OperatorLabel = operatorLabel;
        this.OperatorValue = operatorValue;
    }

    public static String getLabelByValue(Integer operatorValue) {
        List<Operator> list = Arrays.stream(Operator.values()).filter(operatorEnum -> Objects.equal(operatorEnum.getOperatorValue(), operatorValue)).toList();
        if (list.isEmpty()) {
            return "UNKNOWN";
        }
        return list.get(0).getOperatorLabel();
    }

}
