package cn.turbo.bot.app.module.news;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.turbo.bot.base.config.GlobalExecutor;
import cn.turbo.bot.base.module.news.NewsService;
import cn.turbo.bot.base.module.news.NewsSourceEnum;
import cn.turbo.bot.base.module.user.UserDao;
import cn.turbo.bot.base.module.user.domain.UserConfigDTO;
import cn.turbo.bot.base.module.user.domain.UserEntity;
import cn.turbo.bot.base.module.wxbot.WxBotMsgSendService;
import cn.turbo.bot.base.module.wxbot.WxBotService;
import cn.turbo.bot.base.module.wxbot.api.domain.WxBotSendTextMsgDTO;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotEntity;
import cn.turbo.bot.base.util.SmartDateFormatterEnum;
import cn.turbo.bot.base.util.SmartLocalDateUtil;
import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 资讯推送 定时任务
 *
 * @author huke
 * @date 2025/4/8 22:39
 */
@Service
public class NewsPushTask {

    @Autowired
    private UserDao userDao;

    @Autowired
    private WxBotService wxBotService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private WxBotMsgSendService wxBotMsgSendService;

    /**
     * 每分钟 执行一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void pushNews() {
        // 查询开启资讯推送 并且 在配置的时间段内
        String time = SmartLocalDateUtil.format(LocalTime.now(), SmartDateFormatterEnum.HM);
        List<UserEntity> list = userDao.queryByConfigNewsPush(true, time);
        if (CollUtil.isEmpty(list)) {
            return;
        }
        // 循环推送
        for (UserEntity userEntity : list) {
            GlobalExecutor.getExecutor().execute(() -> this.handlePush(time, userEntity));
        }
    }

    /**
     * 处理资讯推送
     *
     * @param time
     * @param userEntity
     */
    private void handlePush(String time, UserEntity userEntity) {
        UserConfigDTO.ConfigNewsPush newsPush = JSON.parseObject(userEntity.getConfig(), UserConfigDTO.class).getNewsPush();
        if (!newsPush.getEnabledFlag()) {
            return;
        }
        Optional<UserConfigDTO.ConfigNewsPushTime> optional = newsPush.getPushTimeList()
                                                                      .stream()
                                                                      .filter(e -> Objects.equals(e.getTime(), time))
                                                                      .findFirst();
        if (optional.isEmpty()) {
            return;
        }
        // 循环推送 各渠道的资讯
        List<NewsSourceEnum> newsSourceList = optional.get().getNewsSourceList();
        for (NewsSourceEnum newsSourceEnum : newsSourceList) {
            // 查询 build 资讯内容
            String newsContent = newsService.buildHotNewsWxMsg(newsSourceEnum);

            // 发送微信消息
            WxBotEntity wxBot = wxBotService.getFirstWxBot();
            WxBotSendTextMsgDTO textMsgDTO = new WxBotSendTextMsgDTO();
            textMsgDTO.setBotWxId(wxBot.getBotWxId());
            textMsgDTO.setToWxId(userEntity.getWxId());
            textMsgDTO.setContent(newsContent);
            // 避免微信风控 随机延迟时间
            wxBotMsgSendService.sendTextMsg(textMsgDTO, RandomUtil.randomLong(50, 600), TimeUnit.SECONDS);
        }
    }

    public static void main(String[] args) {
        String format = SmartLocalDateUtil.format(LocalTime.now(), SmartDateFormatterEnum.HM);
        System.out.println(format);
    }
}
