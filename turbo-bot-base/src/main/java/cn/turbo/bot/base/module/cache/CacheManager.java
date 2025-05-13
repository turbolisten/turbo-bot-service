package cn.turbo.bot.base.module.cache;

import cn.hutool.core.util.NumberUtil;
import cn.turbo.bot.base.common.StringConst;
import cn.turbo.bot.base.module.cache.constant.CacheExpirePolicyEnum;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 缓存 基础管理
 * 在 Caffeine 缓存的基础上 增加功能
 * 1. 使用 CacheModule 区分不同业务缓存的业务
 * 2. 使用 CacheData 实现自定义元素的过期策略
 * 3. 统计打印 缓存状态
 *
 * @author huke
 * @date 2024/10/24 10:24
 */
public class CacheManager {

    /**
     * 定义全局缓存
     */
    private static Cache<String, CacheData<?>> CACHE = null;

    public CacheManager() {
        this.init(null);
    }

    protected CacheManager(CacheManagerBuilder builder) {
        this.init(builder);
    }

    public static CacheManagerBuilder newBuilder() {
        return new CacheManagerBuilder();
    }

    private void init(CacheManagerBuilder builder) {
        Caffeine<String, CacheData<?>> caffeine = Caffeine.newBuilder()
                // 自定义过期策略
                .expireAfter(new Expiry<String, CacheData<?>>() {
                    @Override
                    public long expireAfterCreate(@NonNull String key, @NonNull CacheData<?> value, long currentTime) {
                        return queryExpire(value, false, null);
                    }

                    @Override
                    public long expireAfterUpdate(@NonNull String key, @NonNull CacheData<?> value, long currentTime, @NonNegative long currentDuration) {
                        return queryExpire(value, false, currentDuration);
                    }

                    @Override
                    public long expireAfterRead(@NonNull String key, @NonNull CacheData<?> value, long currentTime, @NonNegative long currentDuration) {
                        return queryExpire(value, true, currentDuration);
                    }
                });
        if (null != builder) {
            Integer initialCapacity = builder.getInitialCapacity();
            if (null != initialCapacity) {
                caffeine.initialCapacity(initialCapacity);
            }
            Integer maxSize = builder.getMaxSize();
            if (null != maxSize) {
                caffeine.maximumSize(maxSize);
            }
            Boolean recordStats = builder.getRecordStats();
            if (null != recordStats && recordStats) {
                caffeine.recordStats();
            }
            Boolean weakValues = builder.getWeakValues();
            if (null != weakValues && weakValues) {
                caffeine.weakValues();
            }
            Boolean softValues = builder.getSoftValues();
            if (null != softValues && softValues) {
                caffeine.softValues();
            }
        }
        CACHE = caffeine.build();
    }

    /**
     * 判断过期策略
     * 返回过期时间
     *
     * @param cacheData
     * @param isRead
     * @return
     */
    private static long queryExpire(CacheData<?> cacheData, boolean isRead, Long currentDuration) {
        Integer expireSecond = cacheData.getExpireTime();
        CacheExpirePolicyEnum policyEnum = cacheData.getExpirePolicy();
        // 默认 不过期
        if (null == expireSecond || expireSecond <= 0 || CacheExpirePolicyEnum.NEVER_EXPIRE == policyEnum) {
            return Long.MAX_VALUE;
        }
        // 写入后N秒过期
        if (CacheExpirePolicyEnum.EXPIRE_AFTER_WRITE == policyEnum && isRead) {
            return currentDuration;
        }
        // 其他 都按 访问后N秒过期
        return TimeUnit.SECONDS.toNanos(expireSecond);
    }

    /**
     * 查询缓存
     *
     * @param module
     * @param key
     * @return
     */
    public <K, V> V queryPresent(CacheModule module, K key) {
        String cacheKey = buildCacheKey(module, key);
        CacheData<?> cacheData = CACHE.getIfPresent(cacheKey);
        if (null == cacheData) {
            return null;
        }
        return (V) cacheData.getData();
    }

    /**
     * 查询|放入缓存
     *
     * @param module 业务模块
     * @param key    缓存key
     * @param func   查询数据方法
     * @return
     */
    public <K, V> V query(CacheModule module, K key, Function<K, V> func) {
        String cacheKey = buildCacheKey(module, key);
        CacheData<?> cacheData = CACHE.get(cacheKey, (tempK) -> {
            V data = func.apply(key);
            return buildCacheData(module, data);
        });
        if (null == cacheData) {
            return null;
        }
        return (V) cacheData.getData();
    }

    /**
     * 批量查询缓存
     *
     * @param module
     * @param objKeyList
     * @return
     */
    public <K, V> Map<K, V> queryAllPresent(CacheModule module, Collection<K> objKeyList) {
        // 构建缓存key map 方便后续根据key 获取数据
        Map<String, K> cacheKeyMap = buildCacheKeyMap(module, objKeyList);
        return CACHE.getAllPresent(cacheKeyMap.keySet())
                .entrySet().stream().collect(Collectors.toMap(en -> cacheKeyMap.get(en.getKey()),
                        en -> (V) en.getValue().getData()));
    }

    /**
     * 批量查询|放入缓存
     *
     * @param module
     * @param objKeyList
     * @param func
     * @return
     */
    public <K, V> Map<K, V> queryAll(CacheModule module,
                                     Collection<K> objKeyList,
                                     Function<Collection<K>, Map<K, V>> func) {
        // 构建缓存key map 方便后续根据key 获取数据
        Map<String, K> cacheKeyMap = buildCacheKeyMap(module, objKeyList);
        // 查询缓存
        Map<String, CacheData<?>> dataMap = CACHE.getAll(cacheKeyMap.keySet(), subKeyList -> {
            // 获取原始key
            List<K> originKeyList = new ArrayList<>();
            for (String subKey : subKeyList) {
                originKeyList.add(cacheKeyMap.get(subKey));
            }
            // 查询数据 放入缓存
            Map<K, V> map = func.apply(originKeyList);
            return map.entrySet().stream()
                    .collect(Collectors.toMap(en -> findKeyByVal(cacheKeyMap, en.getKey()),
                            en -> buildCacheData(module, en.getValue())));
        });
        return dataMap.entrySet().stream().collect(Collectors.toMap(en -> cacheKeyMap.get(en.getKey()),
                en -> (V) en.getValue().getData()));
    }

    /**
     * build 缓存key 与 原始key 对应map
     *
     * @param module
     * @param objKeyList
     * @param <K>
     * @return
     */
    private <K> Map<String, K> buildCacheKeyMap(CacheModule module, Collection<K> objKeyList) {
        Map<String, K> cacheKeyMap = new HashMap<>(objKeyList.size());
        for (K originKey : objKeyList) {
            String cacheKey = buildCacheKey(module, originKey);
            cacheKeyMap.put(cacheKey, originKey);
        }
        return cacheKeyMap;
    }

    /**
     * 根据 map val 查询对应 key
     *
     * @param map
     * @param val
     * @param <K>
     * @return
     */
    private <K> K findKeyByVal(Map<K, ?> map, Object val) {
        return map.entrySet().stream()
                .filter(en -> Objects.equals(en.getValue(), val))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    /**
     * 放入缓存
     *
     * @param module
     * @param key
     * @param data
     */
    public <K, V> void put(CacheModule module, K key, V data) {
        CacheData<V> cacheData = buildCacheData(module, data);
        if (null == cacheData) {
            return;
        }
        String cacheKey = buildCacheKey(module, key);
        CACHE.put(cacheKey, cacheData);
    }

    /**
     * 批量放入缓存
     *
     * @param module
     * @param map
     */
    public <K, V> void putAll(CacheModule module, Map<K, V> map) {
        Map<String, CacheData<V>> cacheMap = map.entrySet().stream().collect(Collectors.toMap(en -> buildCacheKey(module, en.getKey()),
                en -> buildCacheData(module, en.getValue())));
        CACHE.putAll(cacheMap);
    }

    /**
     * 清除缓存
     *
     * @param module
     * @param key
     */
    public <K> void remove(CacheModule module, K key) {
        String cacheKey = buildCacheKey(module, key);
        CACHE.invalidate(cacheKey);
    }

    /**
     * 移除缓存 移除整个业务模块下的缓存
     *
     * @param module
     */
    public void remove(CacheModule module) {
        CACHE.asMap()
                .keySet()
                .stream()
                .filter(k -> k.startsWith(buildCacheKey(module, null)))
                .forEach(CACHE::invalidate);
    }

    /**
     * 移除缓存 移除整个业务模块下的缓存
     *
     * @param moduleList
     */
    public void remove(List<? extends CacheModule> moduleList) {
        List<String> startKeyList = moduleList.stream().map(c -> buildCacheKey(c, null)).distinct().collect(Collectors.toList());
        CACHE.asMap()
                .keySet()
                .stream()
                .filter(k -> startKeyList.stream().anyMatch(k::startsWith))
                .forEach(CACHE::invalidate);
    }

    /**
     * 清除缓存
     *
     * @param module
     * @param keyList
     */
    public <K> void removeAll(CacheModule module, List<K> keyList) {
        List<String> cacheKeyList = keyList.stream()
                .distinct()
                .map(k -> CacheManager.buildCacheKey(module, k))
                .collect(Collectors.toList());
        CACHE.invalidateAll(cacheKeyList);
    }

    /**
     * 固定格式的 key
     * 缓存模块|缓存key
     */
    private static final String CACHE_KEY_FORMAT = "%s|%s";

    protected static String buildCacheKey(CacheModule module, Object key) {
        key = null == key ? StringConst.EMPTY_STR : key;
        return String.format(CACHE_KEY_FORMAT, module.getModuleName(), key);
    }

    public static <T> CacheData<T> buildCacheData(CacheModule module, T data) {
        return buildCacheData(module, data, null);
    }

    /**
     * build 缓存数据
     *
     * @param module
     * @param data
     * @param <T>
     * @return
     */
    public static <T> CacheData<T> buildCacheData(CacheModule module, T data, Integer expireSecond) {
        if (null == data) {
            return null;
        }
        expireSecond = null == expireSecond ? module.getExpireSecond() : expireSecond;
        CacheExpirePolicyEnum expirePolicy = module.getExpirePolicy();
        // 自定义元素过期策略
        if (data instanceof CacheData) {
            CacheData<?> customData = (CacheData<?>) data;
            expireSecond = customData.getExpireTime();
            expirePolicy = customData.getExpirePolicy();
        }
        return new CacheData<>(expirePolicy, expireSecond, data);
    }

    public CacheStats stats() {
        return CACHE.stats();
    }

    public String printCacheStats() {
        CacheStats stats = stats();
        return String.format("\r\n==== CacheStats ==== \r\n" +
                        "estimatedSize=%s\r\n" +
                        "requestCount=%s\r\n" +
                        "hitCount=%s\r\n" +
                        "hitRate=%s\r\n" +
                        "missCount=%s\r\n" +
                        "missRate=%s\r\n" +
                        "loadCount=%s\r\n" +
                        "evictionCount=%s\r\n",
                CACHE.estimatedSize(),
                stats.requestCount(),
                stats.hitCount(),
                NumberUtil.formatPercent(stats.hitRate(), 1),
                stats.missCount(),
                NumberUtil.formatPercent(stats.missRate(), 1),
                stats.loadCount(),
                stats.evictionCount());
    }

    @SneakyThrows
    public static void main(String[] args) {
        CacheModule cacheModule = new CacheModule() {
            @Override
            public String getModuleName() {
                return "test";
            }

            @Override
            public CacheExpirePolicyEnum getExpirePolicy() {
                return CacheExpirePolicyEnum.EXPIRE_AFTER_WRITE;
            }

            @Override
            public Integer getExpireSecond() {
                return 3;
            }
        };

        CacheManager cacheManager = new CacheManager();

        String key = "k1";
        String val = "v1";
        String key2 = "k2";
        String val2 = "v2";
        String key3 = "k3";
        String val3 = "v3";

        cacheManager.put(cacheModule, key, val);
        cacheManager.put(cacheModule, key2, val2);
        cacheManager.put(cacheModule, key3, val3);

        Map<String, String> map = cacheManager.queryAll(cacheModule, Lists.newArrayList(key, key2, "333"),
                (keyList) -> keyList.stream().collect(Collectors.toMap(Function.identity(), k -> "v" + k)));
        System.out.println(map);


    }
}
