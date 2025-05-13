package cn.turbo.bot.base.module.ai.basic.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 消息对象
 *
 * @author huke
 * @date 2025/2/6 21:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIMessage {

    private String role;

    private String content;

    @JsonProperty("reasoning_content")
    private String reasoningContent;
}
