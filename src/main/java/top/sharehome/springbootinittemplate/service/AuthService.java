package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthEmailCodeDto;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthLoginDto;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthRegisterDto;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthRetrievePasswordDto;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.vo.auth.AuthLoginVo;

/**
 * 鉴权认证服务接口
 *
 * @author AntonyCheng
 */
public interface AuthService extends IService<User> {

    /**
     * 注册
     *
     * @param authRegisterDto 用户注册Dto类
     */
    void register(AuthRegisterDto authRegisterDto);

    /**
     * 登录
     *
     * @param authLoginDto 用户登录Dto类
     * @return 返回登录用户信息
     */
    AuthLoginVo login(AuthLoginDto authLoginDto);

    /**
     * 校验邮箱验证码
     * @param authRetrievePasswordDto 找回密码Dto类
     */
    void checkEmailCode(AuthRetrievePasswordDto authRetrievePasswordDto);

    /**
     * 获取邮箱验证码
     * @param authEmailCodeDto 邮箱验证码Dto类
     */
    void getEmailCode(AuthEmailCodeDto authEmailCodeDto);

}