package cn.turbo.bot.base.module.config;

import cn.turbo.bot.base.common.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ConfigKey
 *
 * @author huke
 * @date 2025/2/9 23:23
 */
@Getter
@AllArgsConstructor
public enum ConfigKeyEnum implements BaseEnum {

    /**
     * AI检测用户行为提示词
     * 付费配置
     */
    AI_DETECT_USER_ACTION_SYSTEM_PROMPT("ai_detect_user_action_system_prompt", "AI检测用户行为提示词"),

    BOT_CHAT_CONFIG("bot_chat_config", "bot对话配置"),

    DEV_WX_ID("dev_wx_id", "开发微信id"),

    ;

    private final String value;

    private final String desc;
}
