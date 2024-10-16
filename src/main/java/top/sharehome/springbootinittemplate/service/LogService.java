package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.model.dto.log.AdminLogPageDto;
import top.sharehome.springbootinittemplate.model.entity.Log;
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.vo.log.AdminLogExportVo;
import top.sharehome.springbootinittemplate.model.vo.log.AdminLogPageVo;

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
     * @param adminLogPageDto 日志信息查询条件
     * @param pageModel       分页模型
     * @return 分页查询结果
     */
    Page<AdminLogPageVo> adminPageLog(AdminLogPageDto adminLogPageDto, PageModel pageModel);

    /**
     * 管理员删除日志信息
     *
     * @param id 日志ID
     */
    void adminDeleteLog(Long id);

    /**
     * 管理员清空日志信息
     */
    void adminClearLog();

    /**
     * 导出日志表格
     *
     * @return 导出表格
     */
    List<AdminLogExportVo> adminExportExcelList();
}
