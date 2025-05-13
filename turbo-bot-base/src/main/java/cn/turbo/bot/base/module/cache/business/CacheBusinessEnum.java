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

    USER(CacheExpirePolicyEnum.EXPIRE_AFTER_WRITE, 3600, "用户信息缓存"),

    WX_BOT(CacheExpirePolicyEnum.NEVER_EXPIRE, 1800, "wx bot"),

    CHAT_NUM_CACHE(CacheExpirePolicyEnum.EXPIRE_AFTER_WRITE, 86400, "对话计数缓存"),

    RATE_LIMITER(CacheExpirePolicyEnum.EXPIRE_AFTER_WRITE, 3550, "限流器缓存"),
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
