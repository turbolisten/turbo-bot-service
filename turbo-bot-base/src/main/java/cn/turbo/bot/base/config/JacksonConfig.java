package cn.turbo.bot.base.config;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * JacksonConfig
 *
 * @author huke
 * @date 2025/2/12 21:00
 */
@Configuration
public class JacksonConfig {

    private static final ObjectMapper OBJ_MAPPER;

    static {
        OBJ_MAPPER = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature())
                // 忽略未知字段
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // 忽略空对象
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 创建自定义模块，覆盖 LocalDateTime 的反序列化逻辑
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new MultiDateDeserializer());
        OBJ_MAPPER.registerModule(module);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return OBJ_MAPPER;
    }

    public static ObjectMapper getObjectMapper() {
        return OBJ_MAPPER;
    }
}
