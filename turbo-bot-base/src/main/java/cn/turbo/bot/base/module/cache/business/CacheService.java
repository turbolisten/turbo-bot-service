package cn.turbo.bot.base.module.cache.business;

import cn.hutool.core.collection.CollUtil;
import cn.turbo.bot.base.config.env.SystemEnvDTO;
import cn.turbo.bot.base.module.cache.CacheManager;
import cn.turbo.bot.base.util.SmartEnumUtil;
import cn.turbo.bot.base.util.SmartIpUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 缓存业务
 *
 * @author huke
 * @date 2023/8/9 9:54
 */
@Slf4j
@Service
public class CacheService {

    private static final CacheManager CACHE_MANAGER = CacheManager.newBuilder()
                                                                  .initialCapacity(200)
                                                                  .maxSize(50000)
                                                                  .recordStats()
                                                                  .build();

    @Autowired
    private SystemEnvDTO systemEnv;

    /**
     * 手动放入缓存
     *
     * @param businessEnum
     * @param key
     * @param data
     * @param <K,V>
     */
    public static <K, V> void put(CacheBusinessEnum businessEnum, K key, V data) {
        CACHE_MANAGER.put(businessEnum, key, data);
    }

    /**
     * 手动放入缓存
     *
     * @param businessEnum
     * @param map
     * @param <K>
     * @param <V>
     */
    public static <K, V> void putAll(CacheBusinessEnum businessEnum, Map<K, V> map) {
        CACHE_MANAGER.putAll(businessEnum, map);
    }

    /**
     * 查询缓存数据
     *
     * @param businessEnum 业务类型
     * @param key          缓存key 可null
     * @param <K,V>        数据
     * @return
     */
    public static <K, V> V queryPresent(CacheBusinessEnum businessEnum, K key) {
        return CACHE_MANAGER.queryPresent(businessEnum, key);
    }

    /**
     * 查询|放入缓存
     * 【适用于 业务直接用缓存 没有缓存key的情况】
     *
     * @param businessEnum 业务类型
     * @param func         未查询到缓存时，调用的方法
     * @param <K,V>        数据
     * @return
     */
    public static <K, V> V query(CacheBusinessEnum businessEnum, Function<K, V> func) {
        return query(businessEnum, null, func);
    }

    /**
     * 查询|放入缓存
     *
     * @param businessEnum 业务类型
     * @param key          缓存key
     * @param func         未查询到缓存时，调用的方法
     * @param <K,V>        数据
     * @return
     */
    public static <K, V> V query(CacheBusinessEnum businessEnum, K key, Function<K, V> func) {
        return CACHE_MANAGER.query(businessEnum, key, func);
    }

    /**
     * 批量查询缓存
     *
     * @param businessEnum
     * @param keyList
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> queryAllPresent(CacheBusinessEnum businessEnum, Collection<K> keyList) {
        return CACHE_MANAGER.queryAllPresent(businessEnum, keyList);
    }

    /**
     * 批量 查询|放入 缓存
     *
     * @param businessEnum
     * @param keyList
     * @param func
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> queryAll(CacheBusinessEnum businessEnum,
                                            Collection<K> keyList,
                                            Function<Collection<K>, Map<K, V>> func) {
        return CACHE_MANAGER.queryAll(businessEnum, keyList, func);
    }

    /**
     * 清除 全部服务的缓存
     * 用到了 smart reload 机制
     * 实际会调用到方法 {@link #remove4Reload(String)}
     *
     * @param businessEnum
     * @param key
     */
    public void remove(CacheBusinessEnum businessEnum, Object key) {
        this.remove(businessEnum, Lists.newArrayList(key));
    }

    /**
     * 清除 全部服务的缓存
     * 用到了 smart reload 机制
     * 实际会调用到方法 {@link #remove4Reload(String)}
     *
     * @param businessEnum
     * @param keyList
     */
    public void remove(CacheBusinessEnum businessEnum, List<?> keyList) {
        if (CollUtil.isEmpty(keyList)) {
            return;
        }
        // 清除本地缓存
        removeLocal(businessEnum, keyList);

        // 清除其他服务缓存
        this.sendReload(Lists.newArrayList(businessEnum), keyList);
    }

    /**
     * 移除当前进程 整个业务模块的缓存
     *
     * @param list
     */
    public static void removeLocal(List<CacheBusinessEnum> list) {
        CACHE_MANAGER.remove(list);
    }

    /**
     * 清除本地缓存
     *
     * @param businessEnum
     * @param key
     */
    public static void removeLocal(CacheBusinessEnum businessEnum, Object key) {
        CACHE_MANAGER.remove(businessEnum, key);
    }

    /**
     * 清除本地缓存
     *
     * @param businessEnum
     * @param keyList
     */
    public static void removeLocal(CacheBusinessEnum businessEnum, List<?> keyList) {
        CACHE_MANAGER.removeAll(businessEnum, keyList);
    }


    private void sendReload(List<CacheBusinessEnum> cacheBusinessList, List<?> cacheKeyList) {
        // build reload 参数
        CacheClear cacheClear = new CacheClear();
        cacheClear.setFromProject(systemEnv.getProjectName());
        cacheClear.setFromIp(SmartIpUtil.getLocalFirstIp());
        List<String> businessList = cacheBusinessList.stream().distinct().map(CacheBusinessEnum::getValue).collect(Collectors.toList());
        cacheClear.setCacheBusinessList(businessList);
        cacheClear.setCacheKeyList(cacheKeyList);
        String arg = JSON.toJSONString(cacheClear);
        //redisService.sendTopicMsg(RedisConst.Topic.CACHE_TOPIC, arg);
    }

    public void remove4Reload(String arg) {
        CacheClear cacheClear = JSON.parseObject(arg, CacheClear.class);
        String fromProject = cacheClear.getFromProject();
        if (Objects.equals(fromProject, systemEnv.getProjectName()) && Objects.equals(cacheClear.getFromIp(), SmartIpUtil.getLocalFirstIp())) {
            return;
        }
        // 清除缓存
        List<CacheBusinessEnum> cacheBusinessList = cacheClear.getCacheBusinessList()
                                                              .stream().map(e -> SmartEnumUtil.getEnumByValue(e, CacheBusinessEnum.class))
                                                              .collect(Collectors.toList());
        List<?> keyList = cacheClear.getCacheKeyList();
        if (CollectionUtils.isEmpty(keyList)) {
            removeLocal(cacheBusinessList);
        } else {
            removeLocal(cacheBusinessList.get(0), keyList);
        }

        log.info("\r\n=========== cache reload ===========\r\n"
                 + "from project->{},\r\n"
                 + "cache business ->{},\r\n"
                 + "cache key->{},\r\n"
                 + "=========== cache reload ===========\r\n",
                 fromProject, cacheBusinessList, keyList);
    }

    /*    *//**
     * 定时打印缓存状态
     *//*
    @Scheduled(cron = "5 0 9,11,14,17,20 * * *")
    public void printCacheStatsTask() {
        if (!systemEnv.getIsProd()) {
            return;
        }
        String printed = CacheManager.printCacheStats();
        log.error(printed);
    }*/
}
