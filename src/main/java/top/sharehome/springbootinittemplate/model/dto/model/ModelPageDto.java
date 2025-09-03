package top.sharehome.springbootinittemplate.model.dto.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.validate.GetGroup;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;

import java.io.Serial;
import java.io.Serializable;

import static top.sharehome.springbootinittemplate.common.base.Constants.REGEX_MODEL_TYPE_STR;

/**
 * 管理员分页查询模型信息Dto类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ModelPageDto implements Serializable {

    /**
     * 模型类型（eq）
     */
    @NotBlank(message = "类型不能为空", groups = {PostGroup.class})
    @Pattern(regexp = REGEX_MODEL_TYPE_STR, message = "无此模型类型", groups = {GetGroup.class})
    private String type;

    /**
     * 模型服务（eq）
     */
    private String service;

    /**
     * 模型名称（like）
     */
    private String name;

    /**
     * 模型服务URL（like）
     */
    private String baseUrl;

    /**
     * 模型状态（eq）
     */
    private Integer state;

    @Serial
    private static final long serialVersionUID = 1673409342477821184L;

}
