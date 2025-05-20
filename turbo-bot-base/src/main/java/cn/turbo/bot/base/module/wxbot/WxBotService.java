package cn.turbo.bot.base.module.wxbot;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import cn.turbo.bot.base.module.cache.business.CacheBusinessEnum;
import cn.turbo.bot.base.module.cache.business.CacheService;
import cn.turbo.bot.base.module.wxbot.api.WxBotApi;
import cn.turbo.bot.base.module.wxbot.api.domain.WxBotSendTextMsgDTO;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxEmojiEnum;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotEntity;
import cn.turbo.bot.base.util.SmartLocalDateUtil;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private WxBotApi wxBotApi;

    @Value("${wx-bot.wx-id}")
    private String wxId;

    @Value("${wx-bot.name}")
    private String botName;

    @Override
    public void run(String... args) {
        // 初始化bot缓存
        List<WxBotEntity> list = this.queryAll();
        log.info("==================== init wx bot {} =================", list.size());
    }

    public List<WxBotEntity> queryAll() {
        return CacheService.query(CacheBusinessEnum.WX_BOT, null,
                                  (id) -> {
                                      WxBotEntity wxBotEntity = new WxBotEntity();
                                      wxBotEntity.setBotWxId(wxId);
                                      wxBotEntity.setBotName(botName);
                                      return Lists.newArrayList(wxBotEntity);
                                  });
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

    public void sendDevAlertMsg(String content) {
        WxBotEntity botEntity = this.getFirstWxBot();
        this.sendDevAlertMsg(botEntity.getBotWxId(), content);
    }

    /**
     * 向开发者发送提醒消息
     *
     * @param botWxId
     * @param content
     */
    public void sendDevAlertMsg(String botWxId, String content) {
        // 默认发到第一个id
        String devWxId = this.getFirstWxBot().getBotWxId();

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
        wxBotApi.sendTextMsg(msgDTO);
    }

    /**
     * 定时发送bot状态
     */
    @SneakyThrows
    @Scheduled(cron = "15 10 7-23 * * ?")
    public void sendStatusTask() {
        String runtimeInfo = SystemUtil.getRuntimeInfo().toString();
        String content = StrUtil.format("{}", runtimeInfo);
        this.sendDevAlertMsg(content);
    }

    public static void main(String[] args) {
        System.out.println(SystemUtil.getMaxMemory());
        System.out.println(SystemUtil.getRuntimeInfo());
    }
}
