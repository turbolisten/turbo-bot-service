package cn.turbo.bot.base.module.ai.basic.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * AI 响应结果
 *
 * @author huke
 * @date 2025/2/6 21:25
 */
@Data
public class AIResponse {

    private String id;
    private List<Choice> choices;

    @Data
    public static class Choice {
        private AIMessage message;
        private Integer index;

        @JsonProperty("finish_reason")
        private String finishReason;
    }
}
