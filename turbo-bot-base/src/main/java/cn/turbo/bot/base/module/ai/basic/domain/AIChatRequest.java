package cn.turbo.bot.base.module.ai.basic.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 请求对象
 *
 * @author huke
 * @date 2025/2/6 21:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIChatRequest {

    private String model;
    private List<AIMessage> messages;
    private Double temperature;

    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseFormat {

        private String type;

    }
}
