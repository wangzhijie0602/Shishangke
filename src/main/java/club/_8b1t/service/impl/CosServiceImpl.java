package club._8b1t.service.impl;

import club._8b1t.config.CosClientConfig;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import club._8b1t.mapper.CosMapper;
import club._8b1t.service.CosService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CosServiceImpl implements CosService {

    @Resource
    private CosMapper cosMapper;

    @Resource
    private CosClientConfig cosClientConfig;

    private static final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "png", "jpg", "webp");
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    @Override
    public String uploadAvatar(Long userId, MultipartFile multipartFile) {
        return uploadImage(multipartFile, String.format("/avatar/%s", userId));
    }

    @Override
    public String uploadDishImage(Long userId, MultipartFile file) {
        return uploadImage(file, String.format("/dish/%s", userId));
    }

    @Override
    public String uploadImage(MultipartFile multipartFile, String directory) {
        if (multipartFile.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        // 1. 校验文件大小
        long fileSize = multipartFile.getSize();
        if (fileSize > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 2MB");
        }

        // 2. 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (!ALLOW_FORMAT_LIST.contains(fileSuffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误，仅支持：" + String.join(", ", ALLOW_FORMAT_LIST));
        }

        // 3. 生成文件路径
        String uuid = RandomUtil.randomString(16);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, fileSuffix);
        String uploadPath = String.format("%s/%s", directory, uploadFilename).replaceAll("//", "/");

        // 4. 上传文件
        File file = null;
        try {
            // 5. 创建临时文件
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);

            // 6. 上传图片到对象存储
            cosMapper.putObject(uploadPath, file);

            return cosClientConfig.getHost() + "/" + uploadPath;

        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 7. 清理临时文件
            if (file != null && !file.delete()) {
                log.error("临时文件删除失败, filepath = {}", file.getAbsolutePath());
            }
        }
    }
}
