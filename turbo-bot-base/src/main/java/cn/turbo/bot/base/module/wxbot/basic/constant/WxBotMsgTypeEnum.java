package cn.turbo.bot.base.module.wxbot.basic.constant;


import cn.turbo.bot.base.common.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * bot 消息类型
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
@Getter
@AllArgsConstructor
public enum WxBotMsgTypeEnum implements BaseEnum {

    /**
     * 1 文本消息
     */
    TEXT(1, "文本消息"),

    EVENT(2, "事件消息"),

    IMG(3, "图片消息"),

    USER_CARD(42, "个人名片"),

    EMOTICON(47, "表情图"),

    APP(49, "APP消息"),

    WX_TEAM(-9999, "APP消息"),

    ;

    private final Integer value;

    private final String desc;
}
