package cn.turbo.bot.wx.module.bot.receivemsg.domain;

import cn.turbo.bot.base.module.news.NewsSourceEnum;
import cn.turbo.bot.wx.module.bot.receivemsg.constant.WxBotUserActionEnum;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * wx bot 用户行为识别结果
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
@Data
public class WxBotUserActionDetectResult {

    /**
     * 用户行为
     *
     * @see WxBotUserActionEnum
     */
    private String actionType;

    /**
     * 回复内容
     */
    private String reply;

    /**
     * 额外数据
     */
    private JSONObject data;

    @Data
    public static class Reminder {

        /**
         * 提醒时间
         */
        private LocalDateTime reminderTime;

        /**
         * 提醒内容
         */
        private String reminderContent;
    }

    @Data
    public static class HotNewsQuery {

        /**
         * 资源来源
         *
         * @see NewsSourceEnum
         */
        private String newsSource;
    }
}
