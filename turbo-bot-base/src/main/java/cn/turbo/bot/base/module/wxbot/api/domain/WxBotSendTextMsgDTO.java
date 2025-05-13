package cn.turbo.bot.base.module.wxbot.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 发送 文本消息
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
@Data
public class WxBotSendTextMsgDTO {

    /**
     * 机器人 微信id
     */
    private String botWxId;

    /**
     * 接收消息的群or个人微信id
     */
    private String toWxId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 群聊 需要at的人微信id
     */
    private List<AtUser> atUserList;

    public WxBotSendTextMsgDTO() {
    }

    public WxBotSendTextMsgDTO(String botWxId, String toWxId, String content) {
        this.botWxId = botWxId;
        this.toWxId = toWxId;
        this.content = content;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class AtUser {

        /**
         * 群聊 需要at的人微信id
         */
        private String atUserId;

        /**
         * 群聊 需要at的人微信昵称
         */
        private String nickname;
    }
}
