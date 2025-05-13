package cn.turbo.bot.base.module.wxbot;

import cn.turbo.bot.base.common.RedisConst;
import cn.turbo.bot.base.module.wxbot.api.WxBotApi;
import cn.turbo.bot.base.module.wxbot.api.domain.WxBotSendTextMsgDTO;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotMsgTypeEnum;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotMsgQueueData;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * wx bot 发送消息业务
 * 因为微信风控问题，发消息需要控制间隔 ，尽量不固定的时间
 * 所以用到了队列
 *
 * @author huke
 * @date 2025/4/8 23:30
 */
@Slf4j
@Service
public class WxBotMsgSendService {

    private final RBlockingQueue<WxBotMsgQueueData> blockingQueue;

    private final RDelayedQueue<WxBotMsgQueueData> delayedQueue;

    private final WxBotApi wxBotApi;

    private final ScheduledExecutorService executorService;

    public WxBotMsgSendService(RedissonClient redissonClient, WxBotApi wxBotApi) {
        this.wxBotApi = wxBotApi;

        // 初始化延迟队列
        blockingQueue = redissonClient.getBlockingQueue(RedisConst.Queue.WX_BOT_MSG_DELAYED_QUEUE);
        delayedQueue = redissonClient.getDelayedQueue(blockingQueue);

        // 启动队列消费服务
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.submit(this::consumeLoop);
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 消费队列
     */
    private void consumeLoop() {
        while (true) {
            try {
                WxBotMsgQueueData data = blockingQueue.take();
                log.info("WxBotSendMsgService consume: {}", data);
                // 判断消息类型
                if (WxBotMsgTypeEnum.TEXT == data.getMsgType()) {
                    WxBotSendTextMsgDTO textMsgDTO = JSON.parseObject(data.getMsgData(), WxBotSendTextMsgDTO.class);
                    wxBotApi.sendTextMsg(textMsgDTO);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("WxBotSendMsgService consume error", e);
            } catch (Exception e) {
                log.error("WxBotSendMsgService consume error", e);
            }
        }
    }

    public void sendTextMsg(WxBotSendTextMsgDTO textMsgDTO) {
        wxBotApi.sendTextMsg(textMsgDTO);
    }

    /**
     * 放入队列 延迟发送
     *
     * @param textMsgDTO
     * @param delay
     * @param timeUnit
     */
    public void sendTextMsg(WxBotSendTextMsgDTO textMsgDTO, long delay, TimeUnit timeUnit) {
        WxBotMsgQueueData queue = new WxBotMsgQueueData(WxBotMsgTypeEnum.TEXT, JSON.toJSONString(textMsgDTO));
        delayedQueue.offer(queue, delay, timeUnit);
    }
}
