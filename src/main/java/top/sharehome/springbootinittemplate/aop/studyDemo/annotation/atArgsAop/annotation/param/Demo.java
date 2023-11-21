package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop.annotation.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop.annotation.AtArgsAop;

/**
 * 示例自定义参数类型，用来做@args型的切面类测试。
 *
 * @author AntonyCheng
 */
@AtArgsAop(title = "title", description = "description", isTrue = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Demo {

    private String value;

}