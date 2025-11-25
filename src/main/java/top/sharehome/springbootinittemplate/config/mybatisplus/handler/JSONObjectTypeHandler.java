package top.sharehome.springbootinittemplate.config.mybatisplus.handler;

import com.alibaba.fastjson2.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.Objects;

/**
 * Fastjson2 JSONObject 类型处理器
 *
 * @author AntonyCheng
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(JSONObject.class)
public class JSONObjectTypeHandler extends BaseTypeHandler<JSONObject> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONObject parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toJSONString());
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonText = rs.getString(columnName);
        return Objects.isNull(jsonText) ? null : JSONObject.parseObject(jsonText);
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonText = rs.getString(columnIndex);
        return Objects.isNull(jsonText) ? null : JSONObject.parseObject(jsonText);
    }

    @Override
    public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonText = cs.getString(columnIndex);
        return Objects.isNull(jsonText) ? null : JSONObject.parseObject(jsonText);
    }
}