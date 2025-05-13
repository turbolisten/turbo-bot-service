package cn.turbo.bot.base.module.user.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户/群聊 信息
 *
 * @author huke
 * @date 2025/2/16 21:34
 */
@Data
public class UserDTO {

    private Integer userId;

    /**
     * 个人/群聊wx id
     */
    private String wxId;

    /**
     * 个人/群聊 昵称
     */
    private String userName;

    /**
     * 资料介绍
     */
    private String userDesc;

    /**
     * 是否群聊
     */
    private Boolean roomFlag;

    private String remark;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;

    private UserConfigDTO userConfig;
}
