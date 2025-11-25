package top.sharehome.springbootinittemplate.config.mybatisplus.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

/**
 * PostgreSQL vector 类型处理器
 *
 * @author AntonyCheng
 */
@MappedTypes({float[].class})
@MappedJdbcTypes({JdbcType.OTHER})
public class FloatArrayTypeHandler extends BaseTypeHandler<float[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, float[] parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.OTHER);
            return;
        }

        // 构造 vector 类型的字符串格式：[1.0,2.0,3.0]
        StringBuilder sb = new StringBuilder("[");
        for (int j = 0; j < parameter.length; j++) {
            if (j > 0) {
                sb.append(",");
            }
            sb.append(parameter[j]);
        }
        sb.append("]");

        ps.setObject(i, sb.toString(), Types.OTHER);
    }

    @Override
    public float[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return parseVector(value);
    }

    @Override
    public float[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return parseVector(value);
    }

    @Override
    public float[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return parseVector(value);
    }

    /**
     * 解析 vector 类型字符串为 float 数组
     * 输入格式：[1.0,2.0,3.0] 或 (1.0,2.0,3.0)
     */
    private float[] parseVector(String vectorString) {
        if (vectorString == null || vectorString.trim().isEmpty()) {
            return null;
        }

        // 移除首尾的括号或方括号
        String content = vectorString.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1);
        } else if (content.startsWith("(") && content.endsWith(")")) {
            content = content.substring(1, content.length() - 1);
        }

        if (content.trim().isEmpty()) {
            return new float[0];
        }

        // 分割并转换为 float 数组
        String[] parts = content.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }
}