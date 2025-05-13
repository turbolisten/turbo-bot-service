package cn.turbo.bot.base.module.config;

import cn.turbo.bot.base.module.config.domain.ConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * ConfigDao
 *
 * @author huke
 * @date 2025/2/9 16:15
 */
@Mapper
@Component
public interface ConfigDao extends BaseMapper<ConfigEntity> {

    /**
     * 根据key查询获取数据
     *
     * @param key
     * @return
     */
    ConfigEntity selectByKey(String key);
}
