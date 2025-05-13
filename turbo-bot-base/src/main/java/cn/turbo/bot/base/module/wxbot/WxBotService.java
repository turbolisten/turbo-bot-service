package cn.turbo.bot.base.module.wxbot;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import cn.turbo.bot.base.module.cache.business.CacheBusinessEnum;
import cn.turbo.bot.base.module.cache.business.CacheService;
import cn.turbo.bot.base.module.config.ConfigService;
import cn.turbo.bot.base.module.wxbot.api.domain.WxBotSendTextMsgDTO;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxEmojiEnum;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotEntity;
import cn.turbo.bot.base.util.SmartLocalDateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * wx bot
 *
 * @author huke
 * @date 2025/2/12 21:23
 */
@Slf4j
@Service
public class WxBotService implements CommandLineRunner {

    @Autowired
    private WxBotDao wxBotDao;

    @Autowired
    private ConfigService cconfigService;

    @Autowired
    private WxBotMsgSendService wxBotMsgSendService;

    @Override
    public void run(String... args) {
        // 初始化bot缓存
        List<WxBotEntity> list = this.queryAll();
        log.info("==================== init wx bot {} =================", list.size());
    }

    public List<WxBotEntity> queryAll() {
        return CacheService.query(CacheBusinessEnum.WX_BOT, null,
                                  (id) -> wxBotDao.selectList(null));
    }

    public WxBotEntity getFirstWxBot() {
        return CollUtil.getFirst(this.queryAll());
    }

    public WxBotEntity getWxBot(String botWxId) {
        Optional<WxBotEntity> optional = this.queryAll().stream().filter(e -> Objects.equals(e.getBotWxId(), botWxId)).findFirst();
        if (optional.isEmpty()) {
            throw new RuntimeException("not find wx bot: " + botWxId);
        }
        return optional.get();
    }

    public WxBotEntity getWxBot(Integer botId) {
        Optional<WxBotEntity> optional = this.queryAll().stream().filter(e -> Objects.equals(e.getBotId(), botId)).findFirst();
        if (optional.isEmpty()) {
            throw new RuntimeException("not find wx bot: " + botId);
        }
        return optional.get();
    }

    /**
     * 向开发者发送提醒消息
     *
     * @param content
     */
    public void sendDevAlertMsg(String content) {
        WxBotEntity botEntity = this.queryAll().get(0);
        this.sendDevAlertMsg(botEntity.getBotWxId(), content, true);
    }

    public void sendDevAlertMsg(String content, boolean realTime) {
        WxBotEntity botEntity = this.queryAll().get(0);
        this.sendDevAlertMsg(botEntity.getBotWxId(), content, realTime);
    }

    /**
     * 向开发者发送提醒消息
     *
     * @param botWxId
     * @param content
     * @param realTime
     */
    public void sendDevAlertMsg(String botWxId, String content, boolean realTime) {
        // 默认发到第一个id
        String devWxId = cconfigService.queryDevWxIdList().get(0);

        // send bot msg
        content = StrUtil.format("{}【系统消息】{}\n{}\n{}",
                                 WxEmojiEnum.SHAN_DIAN.getValue(),
                                 WxEmojiEnum.SHAN_DIAN.getValue(),
                                 SmartLocalDateUtil.now(),
                                 content);
        WxBotSendTextMsgDTO msgDTO = new WxBotSendTextMsgDTO();
        msgDTO.setBotWxId(botWxId);
        msgDTO.setToWxId(devWxId);
        //sendTextMsgDTO.setAtUserList(null);
        msgDTO.setContent(content);

        if (realTime) {
            wxBotMsgSendService.sendTextMsg(msgDTO);
        } else {
            // 避免微信风控 随机延迟时间
            wxBotMsgSendService.sendTextMsg(msgDTO, RandomUtil.randomLong(50, 1000), TimeUnit.SECONDS);
        }
    }

    /**
     * 定时发送bot状态
     */
    @SneakyThrows
    @Scheduled(cron = "15 10 7-23 * * ?")
    public void sendStatusTask() {
        String runtimeInfo = SystemUtil.getRuntimeInfo().toString();
        String content = StrUtil.format("{}", runtimeInfo);
        this.sendDevAlertMsg(content, false);
    }

    public static void main(String[] args) {
        System.out.println(SystemUtil.getMaxMemory());
        System.out.println(SystemUtil.getRuntimeInfo());
    }
}
