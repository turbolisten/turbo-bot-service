package cn.turbo.bot.base.module.webhook.key.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * webhook key 实体类
 *
 * @author huke
 * @date 2025/2/12 21:49
 */
@Data
@TableName("t_webhook_key")
public class WebhookKeyEntity {

    @TableId(type = IdType.INPUT)
    private String apiKey;

    /**
     * bot id
     */
    private Integer botId;

    /**
     * 用户wx id/群聊id
     */
    private String userWxId;

    /**
     * 用户/群 名称
     */
    private String userName;

    /**
     * 到期时间
     */
    private LocalDateTime expireTime;

    /**
     * 禁用状态
     */
    private Boolean disabledFlag;

    private String remark;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;

}
