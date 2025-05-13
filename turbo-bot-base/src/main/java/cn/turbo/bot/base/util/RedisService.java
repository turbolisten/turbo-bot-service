package cn.turbo.bot.base.util;

import cn.hutool.core.util.ArrayUtil;
import cn.turbo.bot.base.common.BusinessException;
import cn.turbo.bot.base.common.RedisConst;
import cn.turbo.bot.base.module.cache.business.CacheBusinessEnum;
import cn.turbo.bot.base.module.cache.business.CacheService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;
import org.redisson.api.stream.StreamAddArgs;
import org.redisson.api.stream.StreamReadArgs;
import org.redisson.client.RedisException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * redis 工具类
 *
 * @author huke
 * @date 2025/2/11 19:48
 */
@Slf4j
@Component
public class RedisService {

    private final RedissonClient redissonClient;

    public RedisService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    @PreDestroy
    public void destroy() {
        redissonClient.shutdown();
    }

    /**
     * 获取锁 并 执行程序
     *
     * @param lockKey
     * @param waitTime 毫秒
     * @param lockTime 毫秒
     * @param supplier
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long lockTime, Supplier<T> supplier) {
        // 获取锁
        RLock lock = this.tryLock(lockKey, waitTime, lockTime);
        try {
            return supplier.get();
        } finally {
            RedisService.unlock(lock);
        }
    }

    /**
     * 获取锁 并 执行程序
     *
     * @param lockKey
     * @param waitTime 毫秒
     * @param lockTime 毫秒
     * @param runnable
     */
    public void executeWithLock(String lockKey, long waitTime, long lockTime, Runnable runnable) {
        // 获取锁
        RLock lock = this.tryLock(lockKey, waitTime, lockTime);
        try {
            runnable.run();
        } finally {
            RedisService.unlock(lock);
        }
    }

    /**
     * 获取锁
     *
     * @param lockKey
     * @param waitTime
     * @param lockTime
     * @return
     */
    public boolean getLock(String lockKey, long waitTime, long lockTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, lockTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("Redisson tryLock", e);
        }
        return false;
    }

    /**
     * 尝试获取锁
     * 最多等待 waitTime 毫秒
     * 获取锁成功后占用 lockTime 毫秒
     * ps:需要手动解锁 lock.unlock()
     *
     * @param lockKey
     * @param waitTime 毫秒
     * @param lockTime 毫秒
     * @return
     */
    public RLock tryLock(String lockKey, long waitTime, long lockTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean getLock = lock.tryLock(waitTime, lockTime, TimeUnit.MILLISECONDS);
            if (getLock) {
                return lock;
            }
        } catch (InterruptedException e) {
            log.error("Redisson tryLock", e);
        }
        throw new BusinessException("业务繁忙,请稍后重试~");
    }

    public static void unlock(RLock lock) {
        if (null != lock && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 获取 id 生成器
     * nextId 可生成连续不重复的id
     *
     * @param key
     * @return
     */
    public RIdGenerator idGenerator(String key) {
        return redissonClient.getIdGenerator(key);
    }

    /**
     * 存放任意数据类型
     *
     * @param key
     * @param v
     * @param <T>
     */
    public <T> void putObj(String key, T v) {
        redissonClient.getBucket(key).set(v);
    }

    /**
     * 存放任意数据类型
     *
     * @param key
     * @param v
     * @param duration
     * @param <T>
     */
    public <T> void putObj(String key, T v, Duration duration) {
        redissonClient.getBucket(key).set(v, duration);
    }

    /**
     * 获取任意数据类型
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return 如果没有找到则返回null
     */
    public <T> T getObj(String key, Class<T> clazz) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    /**
     * 根据key删除
     *
     * @param key
     */
    public void delObj(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 根据key删除
     *
     * @param key
     */
    public void delObj(String... key) {
        redissonClient.getKeys().delete(key);
    }

    /**
     * 获取过期时间 毫秒
     *
     * @param key
     * @return
     */
    public Long getExpireTimeMillisecond(String key) {
        return redissonClient.getKeys().remainTimeToLive(key);
    }

    /**
     * 获取过期时间 秒
     *
     * @param key
     * @return
     */
    public Long getExpireTimeSecond(String key) {
        return this.getExpireTimeMillisecond(key) / 1000;
    }

    /**
     * 放入 list 设置过期时间
     *
     * @param key
     * @param list
     * @return
     */
    public <V> void putList(String key, List<V> list, Duration duration) {
        RList<V> rList = redissonClient.getList(key);
        rList.addAll(list);
        rList.expire(duration);
    }

    /**
     * 获取 list
     *
     * @param key
     * @param clazz
     * @param <V>
     * @return
     */
    public <V> RList<V> getList(String key, Class<V> clazz) {
        return redissonClient.getList(key);
    }

    /**
     * 创建一个消息监听器
     *
     * @param topicName
     */
    public void addTopicListener(String topicName, MessageListener<String> listener) {
        RTopic topic = redissonClient.getTopic(topicName);
        topic.addListener(String.class, listener);
    }

    /**
     * 发送 订阅消息
     *
     * @param topicName
     * @param msg
     */
    public void sendTopicMsg(String topicName, String msg) {
        // 创建一个消息监听器 只监听当前服务相关的消息
        RTopic topic = redissonClient.getTopic(topicName);
        topic.publish(msg);
    }

    /**
     * 添加 stream
     *
     * @param streamKey
     * @param map
     * @param <K>
     * @param <V>
     */
    public <K, V> StreamMessageId addStream(String streamKey, Map<K, V> map) {
        RStream<K, V> stream = redissonClient.getStream(streamKey);
        return stream.add(StreamAddArgs.entries(map));
    }

    /**
     * 读取 stream
     *
     * @param streamKey
     * @param count
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> Map<StreamMessageId, Map<K, V>> readStream(String streamKey, Integer count) {
        // 指定读取数量
        RStream<K, V> stream = redissonClient.getStream(streamKey);
        StreamReadArgs readArgs = StreamReadArgs.greaterThan(StreamMessageId.ALL)
                                                .count(count)
                                                .timeout(Duration.ofSeconds(5));
        return stream.read(readArgs);
    }

    /**
     * 删除 stream 消息
     *
     * @param streamKey
     * @param messageIdList
     * @return 删除的数量
     */
    public long delStreamMsg(String streamKey, Collection<StreamMessageId> messageIdList) {
        RStream<Object, Object> stream = redissonClient.getStream(streamKey);
        StreamMessageId[] array = ArrayUtil.toArray(messageIdList, StreamMessageId.class);
        return stream.remove(array);
    }

    /**
     * 获取或创建一个基于 key 的限流器
     *
     * @param key 限流的 key
     * @return RRateLimiter 实例
     */
    public RRateLimiter getRateLimiter(String key) {
        return redissonClient.getRateLimiter(key);
    }

    /**
     * 处理限流
     *
     * @param key
     * @return
     */
    public boolean limiterTryAcquire(String key, int rate, Duration rateInterval) {
        // 特别注意：本地缓存时间 必须小于 限流器存活时间
        RRateLimiter limiter = CacheService.query(CacheBusinessEnum.RATE_LIMITER, key, k -> {
            String redisKey = RedisConst.Limiter.WEBHOOK_KEY + key;
            RRateLimiter rateLimiter = this.getRateLimiter(redisKey);
            // 最多每秒1次 每次请求都会存活时间都会延后1小时
            rateLimiter.trySetRate(RateType.OVERALL, rate, rateInterval, Duration.ofHours(1));
            return rateLimiter;
        });
        try {
            return limiter.tryAcquire();
        } catch (RedisException e) {
            // 限流器不存在 可能是与本地缓存时间未对齐 移除重新处理
            if (!limiter.isExists()) {
                CacheService.removeLocal(CacheBusinessEnum.RATE_LIMITER, key);
                return this.limiterTryAcquire(key, rate, rateInterval);
            }
        }
        return true;
    }
}
