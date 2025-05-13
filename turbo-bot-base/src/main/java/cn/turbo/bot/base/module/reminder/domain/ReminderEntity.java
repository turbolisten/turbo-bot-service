package cn.turbo.bot.base.module.reminder.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约提醒 实体类
 *
 * @author huke
 * @date 2025/2/12 08:25
 */
@Data
@TableName("t_reminder")
public class ReminderEntity {

    @TableId(type = IdType.AUTO)
    private Integer reminderId;

    /**
     * bot id
     */
    private Integer botId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户 wx id
     */
    private String userWxId;

    /**
     * 群聊id
     */
    private String roomId;

    /**
     * 提醒内容
     */
    private String reminderContent;

    /**
     * 提醒时间
     */
    private LocalDateTime reminderTime;

    /**
     * 是否处理
     */
    private Boolean handleFlag;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
