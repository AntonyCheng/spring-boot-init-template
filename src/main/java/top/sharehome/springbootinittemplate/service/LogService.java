package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.model.dto.log.LogPageDto;
import top.sharehome.springbootinittemplate.model.entity.Log;
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.vo.log.LogExportVo;
import top.sharehome.springbootinittemplate.model.vo.log.LogPageVo;

import java.util.List;

/**
 * 日志服务接口
 *
 * @author AntonyCheng
 */
public interface LogService extends IService<Log> {

    /**
     * 管理员分页查询日志信息
     *
     * @param logPageDto 日志信息查询条件
     * @param pageModel       分页模型
     * @return 分页查询结果
     */
    Page<LogPageVo> pageLog(LogPageDto logPageDto, PageModel pageModel);

    /**
     * 管理员删除日志信息
     *
     * @param id 日志ID
     */
    void deleteLog(Long id);

    /**
     * 管理员清空日志信息
     */
    void clearLog();

    /**
     * 导出日志表格
     *
     * @return 导出表格
     */
    List<LogExportVo> exportExcelList();
}
