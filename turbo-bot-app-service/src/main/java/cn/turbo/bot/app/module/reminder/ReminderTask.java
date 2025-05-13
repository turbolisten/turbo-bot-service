package cn.turbo.bot.app.module.reminder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.common.RedisConst;
import cn.turbo.bot.base.common.StringConst;
import cn.turbo.bot.base.config.GlobalExecutor;
import cn.turbo.bot.base.module.reminder.ReminderDao;
import cn.turbo.bot.base.module.reminder.domain.ReminderEntity;
import cn.turbo.bot.base.module.wxbot.WxBotMsgSendService;
import cn.turbo.bot.base.module.wxbot.WxBotService;
import cn.turbo.bot.base.module.wxbot.api.domain.WxBotSendTextMsgDTO;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxEmojiEnum;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotEntity;
import cn.turbo.bot.base.util.RedisService;
import cn.turbo.bot.base.util.SmartDateFormatterEnum;
import cn.turbo.bot.base.util.SmartLocalDateUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 提醒事项 定时任务
 *
 * @author huke
 * @date 2025/2/12 22:24
 */
@Service
public class ReminderTask {

    @Autowired
    private ReminderDao reminderDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WxBotMsgSendService wxBotMsgSendService;

    @Autowired
    private WxBotService wxBotService;

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void reminderTask() {
        Integer startReminderId = null;
        LocalDateTime now = LocalDateTime.now();
        String maxTime = SmartLocalDateUtil.format(now, SmartDateFormatterEnum.YMD_HM) + ":00";
        while (true) {
            // 查询未处理的事项
            List<ReminderEntity> list = reminderDao.queryByHandleFlag(startReminderId, false, maxTime, 200);
            if (CollUtil.isEmpty(list)) {
                break;
            }
            startReminderId = CollUtil.getLast(list).getReminderId();
            // 线程池执行处理
            list.forEach(e -> GlobalExecutor.getExecutor().execute(() -> handle(e, now)));
        }
    }

    /**
     * 处理发送提醒事项
     *
     * @param entity
     */
    private void handle(ReminderEntity entity, LocalDateTime time) {
        // 获取锁
        Integer reminderId = entity.getReminderId();
        boolean lock = redisService.getLock(RedisConst.Lock.REMINDER_HANDLE + reminderId, 0L, 8000L);
        if (!lock) {
            return;
        }
        // 再次查询处理状态
        entity = reminderDao.selectById(reminderId);
        if (null == entity || entity.getHandleFlag()) {
            return;
        }
        // 提醒时间 未超1个小时才发消息提醒
        LocalDateTime reminderTime = entity.getReminderTime();
        if (ChronoUnit.MINUTES.between(reminderTime, time) < 60) {
            this.sendWxBotMsg(entity);
        }
        // 更新处理状态
        entity = new ReminderEntity();
        entity.setReminderId(reminderId);
        entity.setHandleFlag(true);
        entity.setUpdateTime(time);
        reminderDao.updateById(entity);
    }

    /**
     * 发送微信机器人消息
     *
     * @param entity
     */
    private void sendWxBotMsg(ReminderEntity entity) {
        // 查询bot信息
        WxBotEntity wxBot = wxBotService.getWxBot(entity.getBotId());
        // 构建提醒消息
        String reminderContent = this.buildReminderContent(entity);
        // 发送wx bot消息
        String userWxId = entity.getUserWxId();
        WxBotSendTextMsgDTO textMsgDTO = new WxBotSendTextMsgDTO();
        textMsgDTO.setBotWxId(wxBot.getBotWxId());
        textMsgDTO.setToWxId(userWxId);
        textMsgDTO.setContent(reminderContent);
        // 判断是否群聊
        String roomId = entity.getRoomId();
        if (StrUtil.isNotBlank(roomId)) {
            List<WxBotSendTextMsgDTO.AtUser> atUsers = Lists.newArrayList(new WxBotSendTextMsgDTO.AtUser(userWxId, entity.getUserName()));
            textMsgDTO.setToWxId(roomId);
            textMsgDTO.setAtUserList(atUsers);
        }
        wxBotMsgSendService.sendTextMsg(textMsgDTO);
    }

    /**
     * 构建提醒消息
     *
     * @param entity
     * @return
     */
    private String buildReminderContent(ReminderEntity entity) {
        String format = """
                        {}叮咚~ {}
                        【时间】{}
                        【事项】{}
                        {}
                        """;
        String prefix = StrUtil.isBlank(entity.getRoomId()) ? StringConst.EMPTY_STR : "\n";
        prefix = prefix + WxEmojiEnum.STAR.getValue();
        // 生成标题 随机固定列表
        List<String> titleList = Lists.newArrayList("鸡血满满提醒您 " + WxEmojiEnum.GE_BO_JI_ROU.getValue(),
                                                    "宇宙级重要通知 " + WxEmojiEnum.STAR.getValue(),
                                                    "超级无敌提醒大法 " + WxEmojiEnum.HUO_JIAN.getValue(),
                                                    "疯狂日程提醒 " + WxEmojiEnum.HUO_JIAN.getValue(),
                                                    "时间管理大师提醒您 " + WxEmojiEnum.TAI_YANG.getValue(),
                                                    "二次元提醒君 " + WxEmojiEnum.HUO_JIAN.getValue(),
                                                    "元宇宙挖洞大师提醒 " + WxEmojiEnum.HUO_JIAN.getValue(),
                                                    "赛博打洞专家提醒 " + WxEmojiEnum.HUO_JIAN.getValue(),
                                                    "赛博玄学大师提醒 " + WxEmojiEnum.RAINBOW.getValue(),
                                                    "电子尖叫鼠提醒 " + WxEmojiEnum.HUO_JIAN.getValue(),
                                                    "防PUA盾牌鼠提醒 " + WxEmojiEnum.HUO_JIAN.getValue(),
                                                    "超级英雄觉醒提醒 " + WxEmojiEnum.SHAN_DIAN.getValue(),
                                                    "神秘探险家通知 " + WxEmojiEnum.SHAN_DIAN.getValue(),
                                                    "星际旅行者指南提醒 " + WxEmojiEnum.SHAN_DIAN.getValue(),
                                                    "星际快递员送达 " + WxEmojiEnum.HUO_JIAN.getValue(),
                                                    "海盗船长命令 " + WxEmojiEnum.NAN_GUA_TOU.getValue(),
                                                    "超级英雄紧急通知 " + WxEmojiEnum.GE_BO_JI_ROU.getValue(),
                                                    "星际电台广播提醒您 " + WxEmojiEnum.STAR.getValue(),
                                                    "时光旅行者提醒 " + WxEmojiEnum.HUO_JIAN.getValue());
        String title = RandomUtil.randomEle(titleList);
        // 结尾
        List<String> endList = Lists.newArrayList("完成任务后记得奖励自己一根冰棍，或者两根也行！" + WxEmojiEnum.JI_DONG.getValue(),
                                                  "今天的任务完成了吗？没完成的话，小心被外星人抓走哦！" + WxEmojiEnum.TAI_YANG.getValue(),
                                                  "如果你觉得累了，就躺下来假装自己是个沙发，休息一下！" + WxEmojiEnum.WO_SHOU.getValue(),
                                                  "今天的任务完成后，你会得到一个惊喜哦，比如一张彩票！" + WxEmojiEnum.TAI_YANG.getValue(),
                                                  "无论多忙，别忘了照顾好自己，比如多吃点好吃的！" + WxEmojiEnum.RAINBOW.getValue(),
                                                  "任务完成后，记得大喊一声“爷青回”，庆祝一下！" + WxEmojiEnum.SHAN_DIAN.getValue(),
                                                  "相信自己，你可以做到的，除非你变成了土豆！" + WxEmojiEnum.QI_QIU.getValue(),
                                                  "完成任务后，别忘了说一句“真相只有一个！" + WxEmojiEnum.SHAN_DIAN.getValue(),
                                                  "任务完成后，记得大喊一声“我变强了！" + WxEmojiEnum.GE_BO_JI_ROU.getValue(),
                                                  "任务完成，经验值+100，金币+50，继续努力升级吧！" + WxEmojiEnum.JI.getValue(),
                                                  "今天的任务完成后，你会发现自己变得更强大了，燃烧吧，小宇宙！" + WxEmojiEnum.HU_TOU.getValue(),
                                                  "任务完成，你的热血值+100，继续前进，永不放弃！" + WxEmojiEnum.JI_DONG.getValue(),
                                                  "任务完成后，记得大喊一声“绝绝子”，庆祝一下 " + WxEmojiEnum.JI_DONG.getValue(),
                                                  "如果你觉得累了，就去跳个广场舞放松一下，反正没人认识你！" + WxEmojiEnum.ZHUAN_QUAN.getValue(),
                                                  "任务完成后，记得奖励自己一个“灵魂拷问”，比如“我是谁，我在哪，我要干什么？" + WxEmojiEnum.JI_DONG.getValue(),
                                                  "相信自己，你可以做到的，就像“锦鲤”一样好运连连！" + WxEmojiEnum.ZHUAN_QUAN.getValue(),
                                                  "任务完成后，记得给自己一个“咸鱼翻身”的机会，继续加油！" + WxEmojiEnum.JI_DONG.getValue(),
                                                  "任务完成后，记得大喊一声“我太难了”，然后继续努力！" + WxEmojiEnum.ZHUAN_QUAN.getValue(),
                                                  "任务完成后，记得来一句“有内味了”，证明你是个潮流达人！" + WxEmojiEnum.SHAN_DIAN.getValue(),
                                                  "完成任务后，别忘了来个“奥利给”，给自己加油打气！" + WxEmojiEnum.GE_BO_JI_ROU.getValue(),
                                                  "任务完成后，记得给自己点个赞，或者来个双击666！" + WxEmojiEnum.JI_DONG.getValue());
        String end = RandomUtil.randomEle(endList);
        // 提醒时间
        String time = SmartLocalDateUtil.format(entity.getReminderTime(), SmartDateFormatterEnum.YMD_HM);
        return StrFormatter.format(format, prefix, title, time, entity.getReminderContent(), end);
    }
}
