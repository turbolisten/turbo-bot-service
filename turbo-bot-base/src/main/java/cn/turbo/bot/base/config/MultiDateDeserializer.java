package cn.turbo.bot.base.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 时间序列化
 *
 * @author huke
 * @date 2025/2/15 22:24
 */
public class MultiDateDeserializer extends JsonDeserializer<LocalDateTime> {

    // 定义支持的日期格式列表
    private static final DateTimeFormatter[] FORMATTERS = {
            DatePattern.NORM_DATETIME_FORMATTER,
            // 默认 ISO 格式（带 T）
            DateTimeFormatter.ISO_LOCAL_DATE_TIME};

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateStr = p.getText();
        // 依次尝试所有格式，直到解析成功
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // 忽略错误，继续尝试下一个格式
            }
        }
        throw new IOException("无法解析日期时间: " + dateStr);
    }
}
