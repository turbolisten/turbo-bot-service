package cn.turbo.bot.app.module.webhook;

import cn.turbo.bot.base.common.ResponseDTO;
import cn.turbo.bot.base.module.webhook.key.WebhookKeyDao;
import cn.turbo.bot.base.module.webhook.key.domain.WebhookKeyEntity;
import cn.turbo.bot.base.module.webhook.request.WebhookRequestMsgTypeEnum;
import cn.turbo.bot.base.module.webhook.request.domain.WebhookRequest;
import cn.turbo.bot.base.module.wxbot.WxBotMsgSendService;
import cn.turbo.bot.base.module.wxbot.WxBotService;
import cn.turbo.bot.base.module.wxbot.api.domain.WxBotSendTextMsgDTO;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotConst;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxEmojiEnum;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotEntity;
import cn.turbo.bot.base.util.RedisService;
import cn.turbo.bot.base.util.SmartEnumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * webhook 业务
 *
 * @author huke
 * @date 2025/2/13 21:34
 */
@Service
public class WebhookService {

    @Autowired
    private WebhookKeyDao webhookKeyDao;

    @Autowired
    private WxBotMsgSendService wxBotMsgSendService;

    @Autowired
    private WxBotService wxBotService;

    @Autowired
    private RedisService redisService;

    /**
     * 处理 webhook
     *
     * @param request
     * @return
     */
    public <T> ResponseDTO<String> handle(WebhookRequest<T> request) {
        // 处理限流 每个key 每秒1个
        String key = request.getKey();
        if (!redisService.limiterTryAcquire(key, 1, Duration.ofSeconds(1))) {
            return ResponseDTO.userErrorParam("key too many requests");
        }
        // 校验key 到期时间
        WebhookKeyEntity keyEntity = webhookKeyDao.selectById(key);
        if (null == keyEntity) {
            return ResponseDTO.userErrorParam("key does not exist");
        }
        if (keyEntity.getExpireTime().isBefore(LocalDateTime.now())) {
            return ResponseDTO.userErrorParam("key invalid, please contact the developer");
        }
        if (keyEntity.getDisabledFlag()) {
            return ResponseDTO.userErrorParam("key disabled, please contact the developer");
        }
        // 发送bot消息
        WebhookRequestMsgTypeEnum msgTypeEnum = SmartEnumUtil.getEnumByValue(request.getMsgType(), WebhookRequestMsgTypeEnum.class);
        Integer botId = keyEntity.getBotId();
        WxBotEntity wxBot = wxBotService.getWxBot(botId);
        T data = request.getData();
        // 文本消息
        if (WebhookRequestMsgTypeEnum.TEXT == msgTypeEnum) {
            WxBotSendTextMsgDTO textMsgDTO = new WxBotSendTextMsgDTO();
            textMsgDTO.setBotWxId(wxBot.getBotWxId());
            textMsgDTO.setToWxId(keyEntity.getUserWxId());
            // textMsgDTO.setAtUserList();
            String content = WxEmojiEnum.SHAN_DIAN.getValue()
                             + "webhook push"
                             + WxBotConst.HORIZONTAL_LINE
                             + data;
            textMsgDTO.setContent(content);
            wxBotMsgSendService.sendTextMsg(textMsgDTO);
            return ResponseDTO.ok();
        }
        return ResponseDTO.userErrorParam("msg type not supported");
    }
}
