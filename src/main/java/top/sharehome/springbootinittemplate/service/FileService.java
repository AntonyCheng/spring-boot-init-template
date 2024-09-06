package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.model.dto.file.AdminFilePageDto;
import top.sharehome.springbootinittemplate.model.entity.File;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.file.AdminFileExportVo;
import top.sharehome.springbootinittemplate.model.vo.file.AdminFilePageVo;

import java.util.List;

/**
 * 文件服务接口
 *
 * @author AntonyCheng
 */
public interface FileService extends IService<File> {

    /**
     * 管理员分页查询文件信息
     *
     * @param adminFilePageDto 文件信息查询条件
     * @param pageModel        分页模型
     * @return 分页查询结果
     */
    Page<AdminFilePageVo> adminPageFile(AdminFilePageDto adminFilePageDto, PageModel pageModel);

    /**
     * 管理员删除日志信息
     *
     * @param id 文件ID
     */
    void adminDeleteFile(Long id);

    /**
     * 导出文件表格
     *
     * @return 导出表格
     */
    List<AdminFileExportVo> adminExportExcelList();
}
