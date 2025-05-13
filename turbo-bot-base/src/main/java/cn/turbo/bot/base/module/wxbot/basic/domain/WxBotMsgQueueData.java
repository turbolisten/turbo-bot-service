package cn.turbo.bot.base.module.wxbot.basic.domain;

import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotMsgTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * wx bot 消息队列
 *
 * @author huke
 * @date 2025/4/9 21:43
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WxBotMsgQueueData {

    private WxBotMsgTypeEnum msgType;

    private String msgData;
}
