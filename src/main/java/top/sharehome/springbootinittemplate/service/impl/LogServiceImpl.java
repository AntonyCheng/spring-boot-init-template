package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.springbootinittemplate.config.log.enums.OperatorEnum;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeTransactionException;
import top.sharehome.springbootinittemplate.mapper.LogMapper;
import top.sharehome.springbootinittemplate.mapper.UserMapper;
import top.sharehome.springbootinittemplate.model.dto.log.AdminLogPageDto;
import top.sharehome.springbootinittemplate.model.entity.Log;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.log.AdminLogPageVo;
import top.sharehome.springbootinittemplate.service.LogService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 日志服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

    @Resource
    private LogMapper logMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(readOnly = true, rollbackFor = CustomizeTransactionException.class)
    public Page<AdminLogPageVo> adminPageLog(AdminLogPageDto adminLogPageDto, PageModel pageModel) {
        Page<Log> page = pageModel.build();
        Page<AdminLogPageVo> res = pageModel.build();

        LambdaQueryWrapper<Log> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        logLambdaQueryWrapper
                .eq(Objects.nonNull(adminLogPageDto.getOperator()), Log::getOperator, adminLogPageDto.getOperator())
                .eq(StringUtils.isNotBlank(adminLogPageDto.getRequestMethod()), Log::getRequestMethod, adminLogPageDto.getRequestMethod())
                .eq(Objects.nonNull(adminLogPageDto.getResult()), Log::getResult, adminLogPageDto.getResult())
                .like(StringUtils.isNotBlank(adminLogPageDto.getUri()), Log::getUri, adminLogPageDto.getUri())
                .like(StringUtils.isNotBlank(adminLogPageDto.getDescription()), Log::getDescription, adminLogPageDto.getDescription())
                .like(StringUtils.isNotBlank(adminLogPageDto.getMethod()), Log::getMethod, adminLogPageDto.getMethod())
                .like(StringUtils.isNotBlank(adminLogPageDto.getLocation()), Log::getLocation, adminLogPageDto.getLocation());
        if (StringUtils.isNotBlank(adminLogPageDto.getUserAccount())) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.like(User::getAccount, adminLogPageDto.getUserAccount());
            List<Long> userIdList = userMapper.selectList(userLambdaQueryWrapper).stream().map(User::getId).collect(Collectors.toList());
            if (userIdList.isEmpty()) {
                return res;
            } else {
                logLambdaQueryWrapper.in(Log::getUserId, userIdList);
            }
        }
        // 构造查询排序（默认按照创建时间升序排序）
        logLambdaQueryWrapper.orderByAsc(Log::getCreateTime);

        // 分页查询
        logMapper.selectPage(page, logLambdaQueryWrapper);

        // 返回值处理（Entity ==> Vo）
        List<AdminLogPageVo> newRecords = page.getRecords().stream().map(log -> {
            AdminLogPageVo adminLogPageVo = new AdminLogPageVo();
            adminLogPageVo.setId(log.getId());
            adminLogPageVo.setUri(log.getUri());
            adminLogPageVo.setDescription(log.getDescription());
            adminLogPageVo.setOperator(OperatorEnum.getLabelByValue(log.getOperator()));
            adminLogPageVo.setRequestMethod(log.getRequestMethod());
            adminLogPageVo.setMethod(log.getMethod());
            adminLogPageVo.setUserAccount(userMapper.selectById(log.getUserId()).getAccount());
            adminLogPageVo.setIp(log.getIp());
            adminLogPageVo.setLocation(log.getLocation());
            adminLogPageVo.setParam(log.getParam());
            adminLogPageVo.setResult(log.getResult());
            adminLogPageVo.setJson(log.getJson());
            adminLogPageVo.setTime(log.getTime());
            adminLogPageVo.setCreateTime(log.getCreateTime());
            return adminLogPageVo;
        }).collect(Collectors.toList());
        BeanUtils.copyProperties(page, res, "records");
        res.setRecords(newRecords);

        return res;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void adminClearLog() {
        LambdaQueryWrapper<Log> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.isNotNull(Log::getId);
        logMapper.delete(logLambdaQueryWrapper);
    }

}




