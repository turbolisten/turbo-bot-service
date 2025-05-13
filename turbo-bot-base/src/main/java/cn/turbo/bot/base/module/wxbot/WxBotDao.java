package cn.turbo.bot.base.module.wxbot;

import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * wx bot dao
 *
 * @author huke
 * @date 2025/2/12 22:55
 */
@Component
@Mapper
public interface WxBotDao extends BaseMapper<WxBotEntity> {

    /**
     * 根据微信id查询
     *
     * @param wxId
     * @return
     */
    WxBotEntity selectByWxId(String wxId);
}
