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

    /**
     * 通用图片上传
     * @param file 图片文件
     * @param directory 存储目录
     * @return 文件访问URL
     */
    String uploadImage(MultipartFile file, String directory);

    /**
     * 上传菜品图片
     * @param file 菜品图片文件
     * @return 文件访问URL
     */
    String uploadDishImage(Long userId, MultipartFile file);
}
