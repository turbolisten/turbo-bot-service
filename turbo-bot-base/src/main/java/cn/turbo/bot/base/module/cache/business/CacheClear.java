package cn.turbo.bot.base.module.cache.business;

import lombok.Data;

import java.util.List;

/**
 * 缓存清理
 *
 * @author huke
 * @date 2023/08/10 17:45
 */
@Data
public class CacheClear {

    /**
     * 项目
     */
    private String fromProject;

    /**
     * ip
     */
    private String fromIp;

    /**
     * 缓存业务 不能为空
     * {@link CacheBusinessEnum}
     */
    private List<String> cacheBusinessList;

    /**
     * 缓存 key
     * 可 null
     */
    private List<?> cacheKeyList;
}
