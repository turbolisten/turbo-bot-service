package cn.turbo.bot.base.module.wxbot.basic;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.common.StringConst;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotConst;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotEventEnum;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotMsgTypeEnum;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotMsgPacketDTO;
import cn.turbo.bot.base.util.SmartEnumUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * wx bot util
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
@Slf4j
public class WxBotUtil {

    /**
     * 获取事件类型
     *
     * @param packetDTO
     * @return
     */
    public static WxBotEventEnum getEvent(WxBotMsgPacketDTO packetDTO) {
        JSONObject data = packetDTO.getCurrentPacket().getData();
        String eventName = data.getString("EventName");
        WxBotEventEnum eventEnum = SmartEnumUtil.getEnumByValue(eventName, WxBotEventEnum.class);
        if (null == eventEnum) {
            log.error("WxBot EventName not found: {}", eventName);
            return null;
        }
        return eventEnum;
    }

    /**
     * 获取消息类型
     *
     * @param msgDTO
     * @return
     */
    public static WxBotMsgTypeEnum getMsgType(WxBotMsgPacketDTO.MsgDTO msgDTO) {
        Integer msgType = msgDTO.getMsgType();
        WxBotMsgTypeEnum msgTypeEnum = SmartEnumUtil.getEnumByValue(msgType, WxBotMsgTypeEnum.class);
        if (null != msgTypeEnum) {
            return msgTypeEnum;
        }
        // 判断是否 个人和微信团队消息
        String fromUserId = msgDTO.getFromUserName();
        if (Objects.equals(WxBotConst.WxId.WX_TEAM, fromUserId)) {
            return WxBotMsgTypeEnum.WX_TEAM;
        }
        log.error("WxBot MsgType not found: {}", msgType);
        return null;
    }

    /**
     * 判断消息是否来自 群聊
     *
     * @param fromWxId
     * @return
     */
    public static boolean isFromRoom(String fromWxId) {
        return StrUtil.endWithIgnoreCase(fromWxId, "@chatroom");
    }

    /**
     * 是否 at 自己
     *
     * @param pushContent
     * @return
     */
    public static boolean isAtBot(String pushContent, String msg) {
        if (StrUtil.isBlank(pushContent)) {
            return false;
        }
        if (StrUtil.containsIgnoreCase(msg, "@所有人 ")) {
            return false;
        }
        return StrUtil.contains(pushContent, "在群聊中@了你");
    }

    /**
     * at 正则
     */
    private static final Pattern AT_PATTERN = Pattern.compile("@.+?(\\ |\\s)");

    /**
     * 获取 at 自己的消息
     * 这个方法有待完善
     *
     * @param content
     * @return 没有at 会返回null
     */
    public static String getAtSelfMsg(String content) {
        if (null == content) {
            return null;
        }
        return ReUtil.replaceAll(content, AT_PATTERN, StringConst.EMPTY_STR).trim();
    }

    /**
     * 查询 at自己 的用户昵称
     * 这个方法存在的意义 是因为目前RSBOT 有bug 导致个别情况下获取不到发言人的昵称
     *
     * @param pushContent
     * @return
     */
    public static String getAtUserNickname(String pushContent) {
        if (StrUtil.isBlank(pushContent)) {
            return StringConst.EMPTY_STR;
        }
        int i = pushContent.indexOf("在群聊中@了你");
        if (i == -1) {
            return StringConst.EMPTY_STR;
        }
        return pushContent.substring(0, i);
    }

    public static String handleTextNone(String str) {
        return StrUtil.isBlank(str) ? "无" : str;
    }
}
