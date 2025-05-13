package cn.turbo.bot.base.module.config;

import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.common.BusinessException;
import cn.turbo.bot.base.common.StringConst;
import cn.turbo.bot.base.module.cache.business.CacheBusinessEnum;
import cn.turbo.bot.base.module.cache.business.CacheService;
import cn.turbo.bot.base.module.config.domain.ConfigEntity;
import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统配置业务
 *
 * @author huke
 * @date 2025/2/9 16:14
 */
@Service
public class ConfigService {

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private CacheService cacheService;

    /**
     * 查询 系统参数缓存
     *
     * @param key ConfigKeyEnum
     * @return
     */
    public ConfigEntity queryCache(String key) {
        return CacheService.query(CacheBusinessEnum.SYSTEM_CONFIG, key, (param) -> configDao.selectByKey(key));
    }

    /**
     * 刷新系统设置缓存
     */
    private void removeCache(String key) {
        cacheService.remove(CacheBusinessEnum.SYSTEM_CONFIG, key);
    }

    /**
     * 查询配置缓存参数
     *
     * @param configKey
     * @return
     */
    public String getConfigValue(ConfigKeyEnum configKey) {
        return this.queryCache(configKey.getValue()).getConfigValue();
    }

    /**
     * 根据参数key查询 并转换为对象
     *
     * @param configKey
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getConfigValue2Obj(ConfigKeyEnum configKey, Class<T> clazz) {
        String configValue = this.getConfigValue(configKey);
        return JSON.parseObject(configValue, clazz);
    }

    /**
     * 更新系统配置
     *
     * @param key
     * @param value
     * @return
     */
    public void updateValueByKey(ConfigKeyEnum key, String value) {
        ConfigEntity config = this.queryCache(key.getValue());
        if (null == config) {
            throw new BusinessException("system config key not find: " + key);
        }

        // 更新数据
        Long configId = config.getConfigId();
        ConfigEntity entity = new ConfigEntity();
        entity.setConfigId(configId);
        entity.setConfigValue(value);
        entity.setUpdateTime(LocalDateTime.now());
        configDao.updateById(entity);

        // 刷新缓存
        this.removeCache(key.getValue());
    }

    /**
     * 查询开发者微信id
     *
     * @return
     */
    public List<String> queryDevWxIdList() {
        String configVal = this.getConfigValue(ConfigKeyEnum.DEV_WX_ID);
        return StrUtil.split(configVal, StringConst.SEPARATOR);
    }

}
