package club._8b1t.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface CosService {

    /**
     * 上传用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 文件访问URL
     */
    String uploadAvatar(Long userId, MultipartFile file);
}
