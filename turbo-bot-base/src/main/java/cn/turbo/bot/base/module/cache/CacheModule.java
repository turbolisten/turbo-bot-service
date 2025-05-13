package cn.turbo.bot.base.module.cache;


import cn.turbo.bot.base.module.cache.constant.CacheExpirePolicyEnum;

/**
 * 缓存模块 定义
 *
 * @author huke
 * @date 2024/10/24 11:49
 */
public interface CacheModule {

    /**
     * 模块名称
     *
     * @return
     */
    String getModuleName();

    /**
     * 过期策略
     *
     * @return
     */
    CacheExpirePolicyEnum getExpirePolicy();

    /**
     * 过期时间 / 秒
     *
     * @return
     */
    Integer getExpireSecond();
}
