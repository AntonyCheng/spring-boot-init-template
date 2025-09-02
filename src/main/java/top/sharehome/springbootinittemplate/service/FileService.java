package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.model.dto.file.FilePageDto;
import top.sharehome.springbootinittemplate.model.entity.File;
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.vo.file.FileExportVo;
import top.sharehome.springbootinittemplate.model.vo.file.FilePageVo;

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
     * @param filePageDto 文件信息查询条件
     * @param pageModel        分页模型
     * @return 分页查询结果
     */
    Page<FilePageVo> pageFile(FilePageDto filePageDto, PageModel pageModel);

    /**
     * 管理员删除日志信息
     *
     * @param id 文件ID
     */
    void deleteFile(Long id);

    /**
     * 导出文件表格
     *
     * @return 导出表格
     */
    List<FileExportVo> exportExcelList();

    /**
     * 管理员添加文件信息
     *
     * @param file 被添加文件
     */
    void addFile(MultipartFile file);
}
