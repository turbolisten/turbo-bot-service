package cn.turbo.bot.base.module.wxbot.basic.constant;


import cn.turbo.bot.base.common.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * wx bot 事件类型
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
@Getter
@AllArgsConstructor
public enum WxBotEventEnum implements BaseEnum {

    /**
     * 1
     */
    CHATROOM_INVITE_OTHER("ON_EVENT_CHATROOM_INVITE_OTHER", "群聊邀请"),

    MSG_NEW("ON_EVENT_MSG_NEW", "新消息"),

    CONTACT_CHANGE("ON_EVENT_CONTACT_CHANGE", "联系人变更"),

    SNS_NEW("ON_EVENT_SNS_NEW","朋友圈更新"),

    PAT_MSG("ON_EVENT_PAT_MSG","拍一拍"),

    ACCOUNT_OFFLINE("ON_EVENT_ACCOUNT_OFFLINE","账号离线"),

    ;

    private final String value;

    private final String desc;
}
