package cn.turbo.bot.base.module.config.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 配置
 *
 * @author huke
 * @date 2025/2/9 23:15
 */
@Data
@TableName("b_config")
public class ConfigEntity {

    @TableId(type = IdType.AUTO)
    private Long configId;

    /**
     * 参数key
     */
    private String configKey;

    /**
     * 参数的值
     */
    private String configValue;

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 是否公共
     */
    private Boolean publicFlag;

    /**
     * 备注
     */
    private String remark;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
