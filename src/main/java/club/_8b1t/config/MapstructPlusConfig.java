package club._8b1t.config;

import io.github.linpeilie.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动注入Mapstruct Plus
 *
 * @author 8bit
 */
@Configuration
public class MapstructPlusConfig {

    @Bean
    public Converter converter() {
        return new Converter();
    }
}
