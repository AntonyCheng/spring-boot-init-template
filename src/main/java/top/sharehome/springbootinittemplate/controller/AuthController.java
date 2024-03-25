package top.sharehome.springbootinittemplate.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.config.captcha.annotation.EnableCaptcha;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthLoginDto;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthRegisterDto;
import top.sharehome.springbootinittemplate.model.vo.auth.AuthLoginVo;
import top.sharehome.springbootinittemplate.service.AuthService;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 鉴权认证控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/auth")
@SaCheckLogin
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 注册
     *
     * @param authRegisterDto 用户注册Dto类
     * @return 返回注册结果
     */
    @PostMapping("/register")
    @EnableCaptcha
    public R<String> register(@RequestBody @Validated({PostGroup.class}) AuthRegisterDto authRegisterDto) {
        if (!StringUtils.equals(authRegisterDto.getPassword(), authRegisterDto.getCheckPassword())) {
            throw new CustomizeReturnException(ReturnCode.PASSWORD_AND_SECONDARY_PASSWORD_NOT_SAME);
        }
        authService.register(authRegisterDto);
        return R.ok("注册成功");
    }

    /**
     * 登录
     *
     * @param authLoginDto 用户登录Dto类
     * @return 返回登录用户信息
     */
    @PostMapping("/login")
    @EnableCaptcha
    public R<Map<String, Object>> login(@RequestBody @Validated({PostGroup.class}) AuthLoginDto authLoginDto) {
        AuthLoginVo loginUser = authService.login(authLoginDto);
        LoginUtils.login(loginUser);
        return R.okWithToken("登录成功", loginUser);
    }

    /**
     * 获取登录信息
     *
     * @return 返回登录用户信息结果
     */
    @GetMapping("/info")
    public R<AuthLoginVo> info() {
        LoginUtils.syncLoginUser();
        return R.ok(LoginUtils.getLoginUser());
    }

    /**
     * 退出
     *
     * @return 返回退出结果
     */
    @DeleteMapping("/logout")
    public R<String> logout() {
        LoginUtils.logout();
        return R.ok("退出成功");
    }

}