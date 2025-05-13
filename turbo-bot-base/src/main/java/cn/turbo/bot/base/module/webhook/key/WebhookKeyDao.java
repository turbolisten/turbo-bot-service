package cn.turbo.bot.base.module.webhook.key;

import cn.turbo.bot.base.module.webhook.key.domain.WebhookKeyEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * webhook key dao
 *
 * @author huke
 * @date 2025/2/13 21:33
 */
@Mapper
@Component
public interface WebhookKeyDao extends BaseMapper<WebhookKeyEntity> {
}
