package cn.turbo.bot.base.module.user;

import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.common.RedisConst;
import cn.turbo.bot.base.common.StringConst;
import cn.turbo.bot.base.config.GlobalExecutor;
import cn.turbo.bot.base.module.cache.business.CacheBusinessEnum;
import cn.turbo.bot.base.module.cache.business.CacheService;
import cn.turbo.bot.base.module.user.domain.UserConfigDTO;
import cn.turbo.bot.base.module.user.domain.UserDTO;
import cn.turbo.bot.base.module.user.domain.UserEntity;
import cn.turbo.bot.base.module.wxbot.WxBotService;
import cn.turbo.bot.base.module.wxbot.api.WxBotApi;
import cn.turbo.bot.base.module.wxbot.api.domain.WxBotContactDTO;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotMsgPacketDTO;
import cn.turbo.bot.base.util.RedisService;
import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户业务
 *
 * @author huke
 * @date 2025/2/16 22:38
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WxBotApi wxBotApi;

    @Autowired
    private WxBotService wxBotService;

    @Autowired
    private CacheService cacheService;

    /**
     * 查询用户信息缓存
     *
     * @param wxId
     * @return
     */
    public UserDTO queryUserCache(String wxId) {
        return CacheService.query(CacheBusinessEnum.USER, wxId, k -> {
            UserEntity userEntity = userDao.selectByWxId(wxId);
            return this.buildUserDTO(userEntity);
        });
    }

    public void putUserCache(String wxId, UserDTO user) {
        CacheService.put(CacheBusinessEnum.USER, wxId, user);
    }

    public void clearUserCache(String wxId) {
        cacheService.remove(CacheBusinessEnum.USER, wxId);
    }

    /**
     * 查询 / 新增用户信息
     *
     * @param msgDTO
     * @return
     */
    public UserInfo queryOrAddUser(WxBotMsgPacketDTO.MsgDTO msgDTO) {
        // 查询/新增群聊
        UserDTO room = null;
        Boolean fromRoomFlag = msgDTO.getFromRoomFlag();
        if (fromRoomFlag) {
            String roomId = msgDTO.getRoomId();
            room = this.queryUserCache(roomId);
            if (null == room) {
                room = redisService.executeWithLock(RedisConst.Lock.ADD_USER + roomId, 3000, 3000, () -> {
                    UserEntity roomEntity = new UserEntity();
                    roomEntity.setWxId(roomId);
                    roomEntity.setUserName(StringConst.EMPTY_STR);
                    roomEntity.setUserDesc(StringConst.EMPTY_STR);
                    roomEntity.setRoomFlag(true);
                    roomEntity.setCreateTime(LocalDateTime.now());
                    userDao.insert(roomEntity);
                    return this.buildUserDTO(roomEntity);
                });
                this.putUserCache(roomId, room);
            }
        }
        // 查询/新增用户
        String fromUserName = msgDTO.getFromUserName();
        UserDTO user = this.queryUserCache(fromUserName);
        if (null == user) {
            user = redisService.executeWithLock(RedisConst.Lock.ADD_USER + fromUserName, 3000, 3000, () -> {
                UserEntity userEntity = new UserEntity();
                userEntity.setWxId(fromUserName);
                userEntity.setUserName(msgDTO.getActionNickName());
                userEntity.setUserDesc(StringConst.EMPTY_STR);
                userEntity.setRoomFlag(false);
                userEntity.setCreateTime(LocalDateTime.now());
                userDao.insert(userEntity);
                return this.buildUserDTO(userEntity);
            });
            this.putUserCache(user.getWxId(), user);
        }
        return new UserInfo(user, room);
    }

    /**
     * 用户信息
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class UserInfo {

        private UserDTO user;

        private UserDTO room;
    }

    private UserDTO buildUserDTO(UserEntity userEntity) {
        if (null == userEntity) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userEntity.getUserId());
        userDTO.setWxId(userEntity.getWxId());
        userDTO.setUserName(userEntity.getUserName());
        userDTO.setUserDesc(userEntity.getUserDesc());
        userDTO.setRoomFlag(userEntity.getRoomFlag());
        userDTO.setRemark(userEntity.getRemark());
        userDTO.setCreateTime(userEntity.getCreateTime());
        userDTO.setUpdateTime(userEntity.getUpdateTime());
        // 配置
        String jsonConfig = userEntity.getConfig();
        if (StrUtil.isNotBlank(jsonConfig)) {
            UserConfigDTO userConfigDTO = JSON.parseObject(jsonConfig, UserConfigDTO.class);
            userDTO.setUserConfig(userConfigDTO);
        }

        // 顺道更新下用户微信信息
        this.updateUserWxInfoAsync(userEntity);
        return userDTO;
    }

    /**
     * 异步
     * 更新用户微信资料
     *
     * @param userEntity
     */
    public void updateUserWxInfoAsync(UserEntity userEntity) {
        GlobalExecutor.getExecutor().execute(() -> {
            // 查询微信资料
            String botWxId = wxBotService.getFirstWxBot().getBotWxId();
            String userWxId = userEntity.getWxId();
            Boolean roomFlag = userEntity.getRoomFlag();
            WxBotContactDTO contactDTO = roomFlag ? wxBotApi.getChatRoomInfo(botWxId, userWxId) : wxBotApi.getContactInfo(botWxId, userWxId);
            if (null == contactDTO) {
                return;
            }
            String nickName = contactDTO.getNickName();
            if (StrUtil.isBlank(nickName) || Objects.equals(nickName, userEntity.getUserName())) {
                return;
            }
            // 更新用户信息
            UserEntity updateUserEntity = new UserEntity();
            updateUserEntity.setUserId(userEntity.getUserId());
            updateUserEntity.setUserName(nickName);
            updateUserEntity.setUpdateTime(LocalDateTime.now());
            userDao.updateById(updateUserEntity);

            this.clearUserCache(userWxId);
        });
    }
}