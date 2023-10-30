package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.model.dto.user.UserLoginDto;
import top.sharehome.springbootinittemplate.model.dto.user.UserRegisterDto;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.vo.user.UserLoginVo;

/**
 * 用户服务类
 *
 * @author AntonyCheng
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterDto
     */
    void register(UserRegisterDto userRegisterDto);

    /**
     * 用户登陆
     *
     * @param userLoginDto
     * @return
     */
    UserLoginVo login(UserLoginDto userLoginDto);
}
