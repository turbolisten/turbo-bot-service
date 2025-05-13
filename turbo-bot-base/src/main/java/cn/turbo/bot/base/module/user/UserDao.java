package cn.turbo.bot.base.module.user;

import cn.turbo.bot.base.module.user.domain.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户 dao
 *
 * @author huke
 * @date 2025/2/16 22:36
 */
@Mapper
@Component
public interface UserDao extends BaseMapper<UserEntity> {

    /**
     * 根据微信id查询用户
     *
     * @param wxId
     * @return
     */
    UserEntity selectByWxId(String wxId);

    /**
     * 根据 资讯推送配置 查询用户
     *
     * @param enabledFlag
     * @param time
     * @return
     */
    List<UserEntity> queryByConfigNewsPush(@Param("enabledFlag") Boolean enabledFlag, @Param("time") String time);
}
