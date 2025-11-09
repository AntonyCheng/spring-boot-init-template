package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.mapper.FileMapper;
import top.sharehome.springbootinittemplate.model.dto.file.FilePageDto;
import top.sharehome.springbootinittemplate.model.entity.File;
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.vo.file.FileExportVo;
import top.sharehome.springbootinittemplate.model.vo.file.FilePageVo;
import top.sharehome.springbootinittemplate.service.FileService;
import top.sharehome.springbootinittemplate.utils.oss.minio.OssMinioUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 文件服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Resource
    private FileMapper fileMapper;

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<FilePageVo> pageFile(FilePageDto filePageDto, PageModel pageModel) {
        Page<File> page = pageModel.build();
        Page<FilePageVo> res = pageModel.build();

        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        fileLambdaQueryWrapper
                .eq(StringUtils.isNotBlank(filePageDto.getSuffix()), File::getSuffix, filePageDto.getSuffix())
                .eq(StringUtils.isNotBlank(filePageDto.getOssType()), File::getOssType, filePageDto.getOssType())
                .like(StringUtils.isNotBlank(filePageDto.getOriginalName()), File::getOriginalName, filePageDto.getOriginalName());
        // 构造查询排序（默认按照创建时间降序排序）
        fileLambdaQueryWrapper.orderByDesc(File::getCreateTime);

        // 分页查询
        fileMapper.selectPage(page, fileLambdaQueryWrapper);

        // 返回值处理（Entity ==> Vo）
        List<FilePageVo> newRecords = page.getRecords().stream().map(file -> new FilePageVo()
                .setId(file.getId())
                .setOriginalName(file.getOriginalName())
                .setSuffix(file.getSuffix())
                .setSize(getFileSizeStr(file.getSize()))
                .setUrl(file.getUrl())
                .setOssType(file.getOssType())
                .setCreateTime(file.getCreateTime())).toList();
        BeanUtils.copyProperties(page, res, "records");
        res.setRecords(newRecords);

        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long id) {
        // 直接使用对象存储工具类删除文件信息相比使用FileMapper而言，前者可以保证对象存储服务器文件也能得到同步删除
        OssMinioUtils.delete(id);
    }

    @Override
    public List<FileExportVo> exportExcelList() {
        List<File> filesInDatabase = fileMapper.selectList(null);
        return filesInDatabase.stream().map(file -> {
            FileExportVo fileExportVo = new FileExportVo();
            fileExportVo.setId(file.getId());
            fileExportVo.setUniqueKey(file.getUniqueKey());
            fileExportVo.setName(file.getName());
            fileExportVo.setOriginalName(file.getOriginalName());
            fileExportVo.setSuffix(file.getSuffix());
            fileExportVo.setSize(getFileSizeStr(file.getSize()));
            fileExportVo.setUrl(file.getUrl());
            fileExportVo.setOssType(file.getOssType());
            fileExportVo.setCreateTime(file.getCreateTime());
            fileExportVo.setUpdateTime(file.getUpdateTime());
            return fileExportVo;
        }).toList();
    }

    @Override
    public void addFile(MultipartFile file) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String filePath = "file/" + date;
        OssMinioUtils.upload(file, filePath);
    }

    /**
     * 计算文件大小以及合理单位
     *
     * @param size 文件实际大小（单位：byte）
     */
    private String getFileSizeStr(int size) {
        if (size >= 1024 * 1024 * 1024) {
            return String.format("%.2fGB", ((double) size) / (1024 * 1024 * 1024));
        } else if (size >= 1024 * 1024) {
            return String.format("%.2fMB", ((double) size) / (1024 * 1024));
        } else if (size >= 1024) {
            return String.format("%.2fKB", ((double) size) / (1024));
        } else {
            return String.format("%dB", size);
        }
    }
}




