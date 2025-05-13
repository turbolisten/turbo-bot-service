package cn.turbo.bot.base.module.wxbot.basic.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * wx bot
 *
 * @author huke
 * @date 2025/2/12 20:25
 */
@Data
@TableName("t_wx_bot")
public class WxBotEntity {

    @TableId(type = IdType.AUTO)
    private Integer botId;

    /**
     * bot wx id
     */
    private String botWxId;

    private String botName;

    private String remark;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
