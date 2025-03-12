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

    @Override
    public String uploadAvatar(Long userId, MultipartFile multipartFile) {

        if (multipartFile.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        // 1. 校验文件大小
        long fileSize = multipartFile.getSize();
        if (fileSize > 2 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 2MB");
        }

        // 2. 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许上传的文件后缀列表（或者集合）
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "png", "jpg", "webp");
        if (!ALLOW_FORMAT_LIST.contains(fileSuffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
        }

        // 3. 拼接文件路径，规则/avatar/用户id/时间_随机16位字符串.扩展名
        String uuid = RandomUtil.randomString(16);
        // 自己拼接文件上传路径，而不是使用原始文件名称，可以增强安全性
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                fileSuffix);
        String uploadPath = String.format("/avatar/%s/%s", userId, uploadFilename);
        // 4. 上传文件
        File file = null;
        try {

            // 5. 创建临时文件，获取文件到服务器
            file = File.createTempFile(uploadPath, null);
            // 处理文件
            multipartFile.transferTo(file);

            // 6. 上传图片到对象存储
            cosMapper.putObject(uploadPath, file);

            return cosClientConfig.getHost() + "/" + uploadPath;

        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 7. 临时文件清理
            if (file != null) {
                // 删除临时文件
                boolean deleteResult = file.delete();
                if (!deleteResult) {
                    log.error("file删除错误, filepath = {}", file.getAbsolutePath());
                }
            }
        }
    }
}
