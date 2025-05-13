package cn.turbo.bot.base.config.env;

import cn.turbo.bot.base.common.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统环境枚举类
 *
 * @author huke
 * @date 2019年4月11日 17:34:59
 */
@Getter
@AllArgsConstructor
public enum SystemEnvEnum implements BaseEnum {

    /**
     * dev
     */
    DEV("dev", "开发环境"),

    /**
     * sit
     */
    SIT("sit", "测试环境"),

    /**
     * prod
     */
    PROD("prod", "生产环境");

    private final String value;

    private final String desc;
}
