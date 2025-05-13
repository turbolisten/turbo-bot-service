package cn.turbo.bot.base.module.cache;

import cn.turbo.bot.base.module.cache.constant.CacheExpirePolicyEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 缓存 包装对象
 *
 * @author huke
 * @date 2024/10/24 10:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheData<T> {

    /**
     * 过期策略
     * 默认 永不过期
     */
    private CacheExpirePolicyEnum expirePolicy;

    /**
     * 过期时间 秒
     */
    private Integer expireTime;

    /**
     * 缓存数据
     */
    private T data;
}
