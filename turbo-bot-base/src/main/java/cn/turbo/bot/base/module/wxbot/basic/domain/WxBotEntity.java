package cn.turbo.bot.base.module.wxbot.basic.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * wx bot
 *
 * @author huke
 * @date 2025/2/12 20:25
 */
@Data
public class WxBotEntity {

    /**
     * bot wx id
     */
    private String botWxId;

    private String botName;

    private String remark;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
