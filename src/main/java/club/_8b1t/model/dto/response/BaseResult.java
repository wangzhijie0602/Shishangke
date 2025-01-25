package club._8b1t.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用返回实体类
 *
 * @author 8bit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResult<T> {

    private Integer code;
    private String msg;
    private T data;

}