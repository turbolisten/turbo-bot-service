package cn.turbo.bot.app.module.user;

import cn.hutool.core.collection.CollUtil;
import cn.turbo.bot.base.module.user.UserDao;
import cn.turbo.bot.base.module.user.UserService;
import cn.turbo.bot.base.module.user.domain.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时更新 用户微信信息
 *
 * @author huke
 * @date 2025/4/9 21:20
 */
@Service
public class UserWxInfoUpdateTask {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    // @Scheduled(initialDelay = 5_000, fixedDelay = 3600_000)
    public void updateUserWxInfo() {
        List<UserEntity> list = userDao.selectList(null);
        if (CollUtil.isEmpty(list)) {
            return;
        }
        list.forEach(e -> userService.updateUserWxInfoAsync(e));
    }

}
