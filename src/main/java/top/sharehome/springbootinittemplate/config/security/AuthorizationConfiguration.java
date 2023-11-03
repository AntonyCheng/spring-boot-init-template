package top.sharehome.springbootinittemplate.config.security;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.mapper.UserMapper;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static top.sharehome.springbootinittemplate.common.base.Constants.USER_ROLE_KEY;

/**
 * SaToken鉴权配置
 * 这里应该会依靠loginId对数据库进行相关查询，得到的结果放入结果集中，
 * 该模板不会单独规划处一个权限或者角色数据库表样式，而是将角色内嵌于模板SQL中t_user表内，因为该模板主要针对于不同的中小系统而设计的，不同系统都有不同的权限和角色分配，
 * 用与不用SaToken鉴权完全取决于系统本身业务，所以此处@Component注解打开与否完全取决于开发者；
 *
 * @author AntonyCheng
 */
@Component
@ConditionalOnProperty(prefix = "sa-token",name = "isAuthorization",havingValue = "true")
public class AuthorizationConfiguration implements StpInterface {

    /**
     * 重写权限方法
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 返回结果
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 根据SaToken权限配置文档：https://sa-token.cc/doc.html#/use/jur-auth
        // 由于此处设计主要针对于接口权限，所以权限通常有多个，上帝权限和个别极端情况除外
        // 需要使用"."将每一级权限给分离开来，支持权限通配符操作，"*"表示上帝权限
        return Collections.singletonList("*");
    }

    /**
     * 重写角色方法
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 返回结果
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 根据SaToken权限配置文档：https://sa-token.cc/doc.html#/use/jur-auth
        // 由于此处设计主要针对于用户角色，所以角色通常只有一个，个别情况除外
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        String userRole = session.get(USER_ROLE_KEY, "");
        return Collections.singletonList(userRole);
    }

}