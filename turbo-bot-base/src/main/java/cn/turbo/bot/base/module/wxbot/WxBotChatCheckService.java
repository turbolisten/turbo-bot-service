package cn.turbo.bot.base.module.wxbot;

import cn.turbo.bot.base.common.StringConst;
import cn.turbo.bot.base.module.cache.business.CacheBusinessEnum;
import cn.turbo.bot.base.module.cache.business.CacheService;
import cn.turbo.bot.base.module.config.ConfigKeyEnum;
import cn.turbo.bot.base.module.config.ConfigService;
import cn.turbo.bot.base.module.config.domain.BotChatConfigDTO;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotMsgPacketDTO;
import cn.turbo.bot.base.util.SmartLocalDateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * bot chat 校验业务
 *
 * @author huke
 * @date 2025/2/13 22:20
 */
@Service
public class WxBotChatCheckService {

    @Autowired
    private ConfigService configService;

    /**
     * 校验是否可以继续使用
     *
     * @param packetDTO
     * @param msgDTO
     * @return
     */
    public boolean checkCanUse(WxBotMsgPacketDTO packetDTO, WxBotMsgPacketDTO.MsgDTO msgDTO) {
        Integer botId = packetDTO.getBotId();
        Boolean fromRoomFlag = msgDTO.getFromRoomFlag();
        String userWxId = fromRoomFlag ? msgDTO.getRoomId() : msgDTO.getFromUserName();
        // 是否超过每天限流次数
        int dayChatNum = this.incrementAndGetChatNum(botId, userWxId);
        BotChatConfigDTO botChatConfigDTO = configService.getConfigValue2Obj(ConfigKeyEnum.BOT_CHAT_CONFIG, BotChatConfigDTO.class);
        Integer freeChatNum = fromRoomFlag ? botChatConfigDTO.getFreeRoomChatNum() : botChatConfigDTO.getFreeChatNum();
        return dayChatNum <= freeChatNum;
    }

    /**
     * 递增并获取 对话次数
     * 每日重新计算
     * 缓存在内存 重启服务会重新计算 无伤大雅
     *
     * @param botId
     * @param userWxId
     * @return
     */
    public int incrementAndGetChatNum(Integer botId, String userWxId) {
        String key = botId + StringConst.HORIZONTAL + userWxId + SmartLocalDateUtil.nowDate();
        return CacheService.query(CacheBusinessEnum.CHAT_NUM_CACHE, key,
                                  k -> new AtomicInteger(0)).incrementAndGet();
    }

    /**
     * 生成 提示文案
     *
     * @return
     */
    public String buildNoUseText() {
        BotChatConfigDTO botChatConfigDTO = configService.getConfigValue2Obj(ConfigKeyEnum.BOT_CHAT_CONFIG, BotChatConfigDTO.class);
        return botChatConfigDTO.getApplyTrialText();
    }

}
