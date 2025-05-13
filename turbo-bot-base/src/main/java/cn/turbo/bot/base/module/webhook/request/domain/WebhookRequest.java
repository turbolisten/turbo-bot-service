package cn.turbo.bot.base.module.webhook.request.domain;

import cn.turbo.bot.base.common.validator.CheckEnum;
import cn.turbo.bot.base.module.webhook.request.WebhookRequestMsgTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * webhook key 请求对象
 *
 * @author huke
 * @date 2025/2/12 21:49
 */
@Data
public class WebhookRequest<T> {

    @NotBlank(message = "key不能为空")
    @Length(max = 32, message = "最长32字符")
    private String key;

    /**
     * 1 文本消息
     * 未来会支持更新类型消息
     */
    @CheckEnum(value = WebhookRequestMsgTypeEnum.class, required = true, message = "消息类型错误")
    private Integer msgType;

    @NotNull(message = "data不能为空")
    private T data;
}
