package cn.turbo.bot.base.module.webhook.request;


import cn.turbo.bot.base.common.BaseEnum;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotMsgTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * webhook 消息类型
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
@Getter
@AllArgsConstructor
public enum WebhookRequestMsgTypeEnum implements BaseEnum {

    /**
     * 1 文本消息
     */
    TEXT(1, "文本消息", WxBotMsgTypeEnum.TEXT),

    ;

    private final Integer value;

    private final String desc;

    private final WxBotMsgTypeEnum botMsgType;
}
