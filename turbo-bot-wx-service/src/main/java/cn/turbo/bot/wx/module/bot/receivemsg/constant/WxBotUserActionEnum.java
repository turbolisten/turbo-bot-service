package cn.turbo.bot.wx.module.bot.receivemsg.constant;

import cn.turbo.bot.base.common.BaseEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 用户指令 枚举类
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
@Getter
@AllArgsConstructor
public enum WxBotUserActionEnum implements BaseEnum {

    /**
     * chat
     */
    CHAT("chat", "聊天", null),

    APPOINTMENT_REMINDER("appointment_reminder", "预约提醒", null),

    WEBHOOK_APPLY("webhook_apply", "申请webhook", null),

    FEEDBACK("feedback", "意见反馈", null),

    FIXED_COMMAND_QUERY_ID("fixed_command_query_id", "查询id", Lists.newArrayList("查询id", "查询ID")),

    HOT_NEWS_QUERY("hot_news_query", "查询热门资讯", null),
    ;

    private final String value;

    private final String desc;

    private final List<String> commandList;
}
