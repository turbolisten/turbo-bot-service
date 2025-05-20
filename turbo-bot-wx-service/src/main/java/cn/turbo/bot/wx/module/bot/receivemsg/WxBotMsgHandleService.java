package cn.turbo.bot.wx.module.bot.receivemsg;

import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.module.wxbot.WxBotService;
import cn.turbo.bot.base.module.wxbot.api.WxBotApi;
import cn.turbo.bot.base.module.wxbot.api.domain.WxBotSendTextMsgDTO;
import cn.turbo.bot.base.module.wxbot.basic.WxBotUtil;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotEventEnum;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotMsgTypeEnum;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotMsgPacketDTO;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 微信消息 处理
 *
 * @author huke
 * @date 2025/2/7 22:06
 */
@Slf4j
@Service
public class WxBotMsgHandleService {

    @Autowired
    private WxBotApi wxBotApi;

    @Autowired
    private WxBotService wxBotService;

    /**
     * 处理消息
     *
     * @param text
     */
    public void handle(String text) {
        // 获取事件类型
        WxBotMsgPacketDTO packetDTO = JSON.parseObject(text, WxBotMsgPacketDTO.class);
        WxBotEventEnum eventEnum = WxBotUtil.getEvent(packetDTO);
        if (null == eventEnum) {
            return;
        }
        // 新消息
        if (WxBotEventEnum.MSG_NEW == eventEnum) {
            this.handleNewMsg(packetDTO);
        }
        // 账号离线
        if (WxBotEventEnum.ACCOUNT_OFFLINE == eventEnum) {
            this.handleAccountOffline(packetDTO);
        }
        // 联系人变更
        if (WxBotEventEnum.CONTACT_CHANGE == eventEnum) {
            this.handleContactChange(packetDTO);
        }
    }

    /**
     * 处理掉线
     * 推送登录
     *
     * @param packetDTO
     */
    private void handleAccountOffline(WxBotMsgPacketDTO packetDTO) {
        // 获取bot wx id
        String botWxId = packetDTO.getCurrentWxId();
        wxBotApi.pushLogin(botWxId);

        // 发送微信告警消息
        wxBotService.sendDevAlertMsg(botWxId, "微信Bot离线，已执行推送登录");
    }

    /**
     * 处理新消息
     *
     * @param packetDTO
     */
    private void handleNewMsg(WxBotMsgPacketDTO packetDTO) {
        // 转换消息对象
        WxBotMsgPacketDTO.MsgDTO msgDTO = packetDTO.getCurrentPacket().getData().getObject("AddMsg", WxBotMsgPacketDTO.MsgDTO.class);
        // 获取消息类型
        WxBotMsgTypeEnum msgTypeEnum = WxBotUtil.getMsgType(msgDTO);
        if (null == msgTypeEnum) {
            return;
        }
        // 不处理bot发的消息
        if (Objects.equals(packetDTO.getCurrentWxId(), msgDTO.getFromUserName())) {
            return;
        }
        /*// 生产环境不处理开发使用的wx消息 开发环境只处理dev消息
        List<String> devWxIdList = configService.queryDevWxIdList();
        String originFromUserName = msgDTO.getFromUserName();
        boolean isDevMsg = devWxIdList.contains(originFromUserName);
        if (systemEnv.getIsProd() && isDevMsg) {
            return;
        }
        if (!systemEnv.getIsProd() && !isDevMsg) {
            return;
        }*/
        // 处理消息格式 群聊等
        this.handleMsgFormat(msgDTO);
        // 暂时不处理群聊 未at的消息
        if (msgDTO.getFromRoomFlag() && null != msgDTO.getIsAtBot() && !msgDTO.getIsAtBot()) {
            return;
        }
        // 处理文本消息
        if (msgTypeEnum == WxBotMsgTypeEnum.TEXT) {
            this.handleText(packetDTO, msgDTO);
        }
        // TODO 处理 其他消息格式
    }

    /**
     * 处理消息格式
     *
     * @param msgDTO
     */
    private void handleMsgFormat(WxBotMsgPacketDTO.MsgDTO msgDTO) {
        // 提前处理 判断是否群聊 等
        String userOrRoomWxId = msgDTO.getFromUserName();
        msgDTO.setFromUserOrRoomWxId(userOrRoomWxId);
        boolean roomFlag = WxBotUtil.isFromRoom(userOrRoomWxId);
        msgDTO.setFromRoomFlag(roomFlag);
        if (roomFlag) {
            msgDTO.setRoomId(userOrRoomWxId);
            // 群聊时 未at bot 不处理
            String content = msgDTO.getContent();
            boolean isAtBot = WxBotUtil.isAtBot(msgDTO.getPushContent(), content);
            msgDTO.setIsAtBot(isAtBot);
            if (!isAtBot) {
                return;
            }
            // 获取at自己的人昵称
            String actionNickName = msgDTO.getActionNickName();
            if (StrUtil.isBlank(actionNickName)) {
                actionNickName = WxBotUtil.getAtUserNickname(msgDTO.getPushContent());
                msgDTO.setActionNickName(actionNickName);
            }
            // 获取用户
            String actionUserName = msgDTO.getActionUserName();
            msgDTO.setAtUserList(Lists.newArrayList(new WxBotSendTextMsgDTO.AtUser(actionUserName, actionNickName)));
            // 处理消息
            content = WxBotUtil.getAtSelfMsg(content);
            msgDTO.setContent(content);
            msgDTO.setFromUserName(actionUserName);
        }
    }

    /**
     * 处理文本消息
     *
     * @param packetDTO
     * @param msgDTO
     */
    private void handleText(WxBotMsgPacketDTO packetDTO, WxBotMsgPacketDTO.MsgDTO msgDTO) {
        // build 消息
        String currentWxId = packetDTO.getCurrentWxId();

        /**
         * TODO 自定义收到消息后的处理逻辑
         * 此处 默认回复收到
         */
        WxBotSendTextMsgDTO sendTextMsgDTO = new WxBotSendTextMsgDTO();
        sendTextMsgDTO.setBotWxId(currentWxId);
        sendTextMsgDTO.setToWxId(msgDTO.getFromUserOrRoomWxId());
        sendTextMsgDTO.setAtUserList(msgDTO.getAtUserList());
        sendTextMsgDTO.setContent("收到收到");
        wxBotApi.sendTextMsg(sendTextMsgDTO);
    }

    /**
     * 处理联系人变更
     *
     * @param packetDTO
     */
    private void handleContactChange(WxBotMsgPacketDTO packetDTO) {
        // 转换消息对象
        WxBotMsgPacketDTO.ContactDTO contactDTO = packetDTO.getCurrentPacket().getData().getObject("Contact", WxBotMsgPacketDTO.ContactDTO.class);
        // 发送变动消息 目前存在问题消息会重复发送多遍 只能通过有头像的处理一次了
        if (StrUtil.isNotBlank(contactDTO.getBigHeadImgUrl())) {
            String format = StrUtil.format("【好友变动】\n【id】{}\n【名称】{}", contactDTO.getUserName(), contactDTO.getNickName());
            wxBotService.sendDevAlertMsg(format);
        }
    }
}
