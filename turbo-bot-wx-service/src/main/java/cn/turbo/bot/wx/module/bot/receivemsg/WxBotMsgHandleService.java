package cn.turbo.bot.wx.module.bot.receivemsg;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.common.CommonConst;
import cn.turbo.bot.base.common.RedisConst;
import cn.turbo.bot.base.config.env.SystemEnvDTO;
import cn.turbo.bot.base.module.config.ConfigService;
import cn.turbo.bot.base.module.mail.MailService;
import cn.turbo.bot.base.module.news.NewsService;
import cn.turbo.bot.base.module.news.NewsSourceEnum;
import cn.turbo.bot.base.module.reminder.ReminderDao;
import cn.turbo.bot.base.module.reminder.domain.ReminderEntity;
import cn.turbo.bot.base.module.user.UserService;
import cn.turbo.bot.base.module.wxbot.WxBotChatCheckService;
import cn.turbo.bot.base.module.wxbot.WxBotMsgSendService;
import cn.turbo.bot.base.module.wxbot.WxBotService;
import cn.turbo.bot.base.module.wxbot.api.WxBotApi;
import cn.turbo.bot.base.module.wxbot.api.domain.WxBotSendTextMsgDTO;
import cn.turbo.bot.base.module.wxbot.basic.WxBotUtil;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotEventEnum;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotMsgTypeEnum;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxEmojiEnum;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotEntity;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotMsgPacketDTO;
import cn.turbo.bot.base.util.CommonUtil;
import cn.turbo.bot.base.util.RedisService;
import cn.turbo.bot.base.util.SmartEnumUtil;
import cn.turbo.bot.base.util.SmartLocalDateUtil;
import cn.turbo.bot.wx.module.bot.receivemsg.constant.WxBotUserActionEnum;
import cn.turbo.bot.wx.module.bot.receivemsg.domain.WxBotUserActionDetectResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
    private WxBotUserActionDetectService userActionDetectService;

    @Autowired
    private WxBotApi wxBotApi;

    @Autowired
    private WxBotMsgSendService wxBotMsgSendService;

    @Autowired
    private ReminderDao reminderDao;

    @Autowired
    private WxBotService wxBotService;

    @Autowired
    private WxBotChatCheckService botChatCheckService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private SystemEnvDTO systemEnv;

    @Autowired
    private UserService userService;

    @Autowired
    private NewsService newsService;

    /**
     * 处理消息
     *
     * @param text
     */
    public void handle(String text) {
        // 获取分布式锁 锁保持3秒 足够业务处理了
        String hex = CommonUtil.getMd5().digestHex(text);
        boolean getLock = redisService.getLock(RedisConst.Lock.BOT_MSG_HANDLE + hex, 0, 5000);
        if (!getLock) {
            return;
        }
        // 获取事件类型
        WxBotMsgPacketDTO packetDTO = JSON.parseObject(text, WxBotMsgPacketDTO.class);
        WxBotEventEnum eventEnum = WxBotUtil.getEvent(packetDTO);
        if (null == eventEnum) {
            return;
        }
        // 查询bot
        WxBotEntity wxBot = wxBotService.getWxBot(packetDTO.getCurrentWxId());
        packetDTO.setBotId(wxBot.getBotId());
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
        wxBotService.sendDevAlertMsg(botWxId, "微信Bot离线，已执行推送登录", true);
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
        // 生产环境不处理开发使用的wx消息 开发环境只处理dev消息
        List<String> devWxIdList = configService.queryDevWxIdList();
        String originFromUserName = msgDTO.getFromUserName();
        boolean isDevMsg = devWxIdList.contains(originFromUserName);
        if (systemEnv.getIsProd() && isDevMsg) {
            return;
        }
        if (!systemEnv.getIsProd() && !isDevMsg) {
            return;
        }
        // 处理消息格式 群聊等
        this.handleMsgFormat(msgDTO);
        // 暂时不处理群聊 未at的消息
        if (msgDTO.getFromRoomFlag() && null != msgDTO.getIsAtBot() && !msgDTO.getIsAtBot()) {
            return;
        }
        // 查询用户信息
        UserService.UserInfo userInfo = userService.queryOrAddUser(msgDTO);
        // 处理文本消息
        if (msgTypeEnum == WxBotMsgTypeEnum.TEXT) {
            this.handleText(packetDTO, msgDTO, userInfo);
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
    private void handleText(WxBotMsgPacketDTO packetDTO, WxBotMsgPacketDTO.MsgDTO msgDTO, UserService.UserInfo userInfo) {
        // build 消息
        String currentWxId = packetDTO.getCurrentWxId();
        WxBotSendTextMsgDTO sendTextMsgDTO = new WxBotSendTextMsgDTO();
        sendTextMsgDTO.setBotWxId(currentWxId);
        sendTextMsgDTO.setToWxId(msgDTO.getFromUserOrRoomWxId());
        sendTextMsgDTO.setAtUserList(msgDTO.getAtUserList());

        // 检测是否固定指令
        WxBotUserActionDetectResult detectResult = userActionDetectService.detectIsFixedAction(msgDTO);
        if (null != detectResult) {
            sendTextMsgDTO.setContent(detectResult.getReply());
            wxBotMsgSendService.sendTextMsg(sendTextMsgDTO);
            return;
        }
        // 校验对话是否可用
        boolean canUse = botChatCheckService.checkCanUse(packetDTO, msgDTO);
        if (!canUse) {
            String tips = botChatCheckService.buildNoUseText();
            sendTextMsgDTO.setContent(tips);
            wxBotMsgSendService.sendTextMsg(sendTextMsgDTO);
            return;
        }
        // 识别用户意图 处理用户行为
        detectResult = userActionDetectService.detectByAi(msgDTO, userInfo);
        // null 代表出错
        if (null == detectResult) {
            sendTextMsgDTO.setContent(CommonConst.getErrTips());
            wxBotMsgSendService.sendTextMsg(sendTextMsgDTO);
            return;
        }
        String actionType = detectResult.getActionType();
        // 预约提醒
        String customReply = null;
        if (WxBotUserActionEnum.APPOINTMENT_REMINDER.equalsValue(actionType)) {
            customReply = this.handleReminder(packetDTO, msgDTO, detectResult);
        }
        // 查询热门资讯
        if (WxBotUserActionEnum.HOT_NEWS_QUERY.equalsValue(actionType)) {
            // 不需要at人
            sendTextMsgDTO.setAtUserList(null);
            customReply = this.handleHotNewsQuery(detectResult);
        }
        // 回复消息
        String reply = null == customReply ? detectResult.getReply() : customReply;
        if (StrUtil.isNotBlank(reply)) {
            sendTextMsgDTO.setContent(reply);
            wxBotMsgSendService.sendTextMsg(sendTextMsgDTO);
        }
        // 意见反馈
        if (WxBotUserActionEnum.FEEDBACK.equalsValue(actionType)) {
            this.handleFeedback(packetDTO, msgDTO);
        }
    }

    /**
     * 处理预约提醒
     *
     * @param packetDTO
     * @param msgDTO
     * @param detectResult
     * @return
     */
    private String handleReminder(WxBotMsgPacketDTO packetDTO, WxBotMsgPacketDTO.MsgDTO msgDTO, WxBotUserActionDetectResult detectResult) {
        // 判断预约记录次数
        int maxReminderNum = 5;
        Integer botId = packetDTO.getBotId();
        String fromUserName = msgDTO.getFromUserName();
        String roomId = msgDTO.getRoomId();
        Integer count = reminderDao.countByBotIdAndUserWxIdAndRoomId(botId, fromUserName, roomId, false);
        if (null != count && count >= maxReminderNum) {
            return StrFormatter.format("{}啊~抱歉！赛博鼠鼠的脑容量有限，您的预约提醒已经超过了{}条了，请次数恢复后再告知属下吧{}", WxEmojiEnum.FA_DOU.getValue(), maxReminderNum, WxEmojiEnum.TIAO_SHENG.getValue());
        }
        // 获取解析的事项
        WxBotUserActionDetectResult.Reminder reminder = detectResult.getData().toJavaObject(WxBotUserActionDetectResult.Reminder.class);
        // 去掉秒 精确到分钟即可
        LocalDateTime reminderTime = reminder.getReminderTime().withSecond(0);

        // 保存预约记录
        ReminderEntity reminderEntity = new ReminderEntity();
        reminderEntity.setBotId(botId);
        reminderEntity.setUserWxId(fromUserName);
        reminderEntity.setUserName(msgDTO.getActionNickName());
        reminderEntity.setRoomId(roomId);
        reminderEntity.setReminderContent(reminder.getReminderContent());
        reminderEntity.setReminderTime(reminderTime);
        reminderDao.insert(reminderEntity);
        return null;
    }

    /**
     * 处理意见反馈
     * 发送邮件
     *
     * @param packetDTO
     * @param msgDTO
     */
    private void handleFeedback(WxBotMsgPacketDTO packetDTO, WxBotMsgPacketDTO.MsgDTO msgDTO) {
        String format = """
                        wx-bot: {}
                        时间: {}
                        昵称: {}
                        微信号: {}
                        群聊: {}
                        反馈内容: {}
                        """;
        String content = StrFormatter.format(format, packetDTO.getCurrentWxId(), SmartLocalDateUtil.now(), msgDTO.getActionNickName(), msgDTO.getFromUserName(), msgDTO.getRoomId(), msgDTO.getContent());
        MailService.send(CommonConst.DEV_EMAIL, "WX-BOT-意见反馈", content, false);
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

    /**
     * 查询热门资讯
     *
     * @param detectResult
     * @return
     */
    private String handleHotNewsQuery(WxBotUserActionDetectResult detectResult) {
        // 获取资源来源 默认头条
        JSONObject data = detectResult.getData();
        NewsSourceEnum newsSource = NewsSourceEnum.TOU_TIAO;
        if (null != data) {
            WxBotUserActionDetectResult.HotNewsQuery hotNewsQuery = data.toJavaObject(WxBotUserActionDetectResult.HotNewsQuery.class);
            NewsSourceEnum newsSourceTemp = SmartEnumUtil.getEnumByValue(hotNewsQuery.getNewsSource(), NewsSourceEnum.class);
            newsSource = null != newsSourceTemp ? newsSourceTemp : newsSource;
        }
        // 查询 build 热门资讯微信消息
        return newsService.buildHotNewsWxMsg(newsSource);
    }
}
