package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.mapper.LogMapper;
import top.sharehome.springbootinittemplate.mapper.UserMapper;
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.dto.log.LogPageDto;
import top.sharehome.springbootinittemplate.model.entity.Log;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.vo.log.LogExportVo;
import top.sharehome.springbootinittemplate.model.vo.log.LogPageVo;
import top.sharehome.springbootinittemplate.service.LogService;

import java.util.List;
import java.util.Objects;

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
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<LogPageVo> pageLog(LogPageDto logPageDto, PageModel pageModel) {
        Page<Log> page = pageModel.build();
        Page<LogPageVo> res = pageModel.build();

        LambdaQueryWrapper<Log> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        logLambdaQueryWrapper
                .eq(Objects.nonNull(logPageDto.getOperator()), Log::getOperator, logPageDto.getOperator())
                .eq(StringUtils.isNotBlank(logPageDto.getRequestMethod()), Log::getRequestMethod, logPageDto.getRequestMethod())
                .eq(Objects.nonNull(logPageDto.getResult()), Log::getResult, logPageDto.getResult())
                .like(StringUtils.isNotBlank(logPageDto.getUri()), Log::getUri, logPageDto.getUri())
                .like(StringUtils.isNotBlank(logPageDto.getDescription()), Log::getDescription, logPageDto.getDescription())
                .like(StringUtils.isNotBlank(logPageDto.getMethod()), Log::getMethod, logPageDto.getMethod())
                .like(StringUtils.isNotBlank(logPageDto.getLocation()), Log::getLocation, logPageDto.getLocation());
        if (StringUtils.isNotBlank(logPageDto.getUserAccount())) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.like(User::getAccount, logPageDto.getUserAccount());
            List<Long> userIdList = userMapper.selectList(userLambdaQueryWrapper).stream().map(User::getId).toList();
            if (userIdList.isEmpty()) {
                return res;
            } else {
                logLambdaQueryWrapper.in(Log::getUserId, userIdList);
            }
        }
        // 构造查询排序（默认按照创建时间降序排序）
        logLambdaQueryWrapper.orderByDesc(Log::getCreateTime);

        // 分页查询
        logMapper.selectPage(page, logLambdaQueryWrapper);

        // 返回值处理（Entity ==> Vo）
        List<LogPageVo> newRecords = page.getRecords().stream().map(log -> new LogPageVo()
                .setId(log.getId())
                .setUri(log.getUri())
                .setDescription(log.getDescription())
                .setOperator(Operator.getLabelByValue(log.getOperator()))
                .setRequestMethod(log.getRequestMethod())
                .setMethod(log.getMethod())
                .setUserAccount(Objects.equals(log.getUserId(), Constants.NULL_ID) ? "该操作不记录用户信息" : (Objects.nonNull(userMapper.selectById(log.getUserId())) ? userMapper.selectById(log.getUserId()).getAccount() : "用户信息不存在"))
                .setIp(log.getIp())
                .setLocation(log.getLocation())
                .setParam(log.getParam())
                .setResult(log.getResult())
                .setJson(log.getJson())
                .setTime(log.getTime())
                .setCreateTime(log.getCreateTime())).toList();
        BeanUtils.copyProperties(page, res, "records");
        res.setRecords(newRecords);

        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLog(Long id) {
        int deleteResult = logMapper.deleteById(id);
        if (deleteResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearLog() {
        LambdaQueryWrapper<Log> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.isNotNull(Log::getId);
        logMapper.delete(logLambdaQueryWrapper);
    }

    @Override
    public List<LogExportVo> exportExcelList() {
        List<Log> logsInDatabase = logMapper.selectList(null);
        return logsInDatabase.stream().map(log -> {
            LogExportVo logExportVo = new LogExportVo();
            logExportVo.setId(log.getId());
            logExportVo.setUri(log.getUri());
            logExportVo.setDescription(log.getDescription());
            logExportVo.setOperator(Operator.getLabelByValue(log.getOperator()));
            logExportVo.setRequestMethod(log.getRequestMethod());
            logExportVo.setMethod(log.getMethod());
            logExportVo.setUserAccount(Objects.equals(log.getUserId(), Constants.NULL_ID) ? "该操作不记录用户信息" : (Objects.nonNull(userMapper.selectById(log.getUserId())) ? userMapper.selectById(log.getUserId()).getAccount() : "用户信息不存在"));
            logExportVo.setIp(log.getIp());
            logExportVo.setLocation(log.getLocation());
            logExportVo.setParam(log.getParam());
            logExportVo.setResult(log.getResult() == 0 ? "正常" : "异常");
            logExportVo.setJson(log.getJson());
            logExportVo.setTime(log.getTime());
            logExportVo.setCreateTime(log.getCreateTime());
            logExportVo.setUpdateTime(log.getUpdateTime());
            return logExportVo;
        }).toList();
    }

}




