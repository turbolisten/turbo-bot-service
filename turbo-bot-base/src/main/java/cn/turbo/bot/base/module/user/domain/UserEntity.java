package cn.turbo.bot.base.module.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户/群聊 实体类
 *
 * @author huke
 * @date 2025/2/16 22:34
 */
@Data
@TableName("t_user")
public class UserEntity {

    @TableId(type = IdType.AUTO)
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

    /**
     * json 配置
     *
     * @see UserConfigDTO
     */
    private String config;

    private String remark;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
