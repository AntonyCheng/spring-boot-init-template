package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.sharehome.springbootinittemplate.model.entity.File;
import top.sharehome.springbootinittemplate.service.FileService;
import top.sharehome.springbootinittemplate.mapper.FileMapper;
import org.springframework.stereotype.Service;

/**
 * 文件服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService{

}




