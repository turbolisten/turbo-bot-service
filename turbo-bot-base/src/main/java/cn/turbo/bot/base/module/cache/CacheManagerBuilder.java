package cn.turbo.bot.base.module.cache;

import lombok.Getter;

/**
 * 缓存管理参数 builer
 *
 * @author huke
 * @date 2024/10/24 14:43
 */
@Getter
public class CacheManagerBuilder {

    private Integer initialCapacity = null;

    private Integer maxSize = null;

    private Boolean recordStats = null;

    private Boolean weakValues;

    private Boolean softValues;

    public CacheManagerBuilder initialCapacity(Integer initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    public CacheManagerBuilder maxSize(Integer maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public CacheManagerBuilder recordStats() {
        this.recordStats = true;
        return this;
    }

    public CacheManagerBuilder weakValues() {
        this.weakValues = true;
        return this;
    }

    public CacheManagerBuilder softValues() {
        this.softValues = true;
        return this;
    }

    public CacheManager build() {
        return new CacheManager(this);
    }
}
