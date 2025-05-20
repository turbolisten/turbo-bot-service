package cn.turbo.bot.base.module.cache.business;

import cn.turbo.bot.base.common.BaseEnum;
import cn.turbo.bot.base.module.cache.CacheModule;
import cn.turbo.bot.base.module.cache.constant.CacheExpirePolicyEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 缓存 业务类型
 *
 * @author huke
 * @Date 2023/08/08
 */
@Getter
@AllArgsConstructor
public enum CacheBusinessEnum implements BaseEnum, CacheModule {

    // --------------------------- 系统 -----------------------------

    SYSTEM_CONFIG(CacheExpirePolicyEnum.EXPIRE_AFTER_WRITE, 300, "系统配置"),

    WX_BOT(CacheExpirePolicyEnum.NEVER_EXPIRE, 1800, "wx bot"),

    ;

    private final CacheExpirePolicyEnum expirePolicy;

    private final Integer expireSecond;

    private final String desc;

    @Override
    public String getValue() {
        return this.name().toLowerCase();
    }


    @Override
    public String getModuleName() {
        return getValue();
    }
}
