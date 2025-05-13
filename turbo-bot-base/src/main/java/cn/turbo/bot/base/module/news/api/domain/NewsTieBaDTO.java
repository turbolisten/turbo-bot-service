package cn.turbo.bot.base.module.news.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 贴吧 热议
 *
 * @author huke
 * @date 2025/4/6 20:37
 */
@Data
public class NewsTieBaDTO {

    @JsonProperty("topic_name")
    private String topicName;

    @JsonProperty("topic_desc")
    private String topicDesc;

    @JsonProperty("topic_url")
    private String topicUrl;
}
