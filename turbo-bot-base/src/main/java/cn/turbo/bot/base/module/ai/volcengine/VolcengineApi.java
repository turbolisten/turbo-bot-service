package cn.turbo.bot.base.module.ai.volcengine;

import com.google.common.util.concurrent.RateLimiter;
import com.volcengine.ark.runtime.model.completion.chat.*;
import com.volcengine.ark.runtime.service.ArkService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 火山方舟
 *
 * @author huke
 * @date 2025/2/9 20:42
 */
@Slf4j
@Service
public class VolcengineApi {

    private static final RateLimiter RATE_LIMITER = RateLimiter.create(33);

    private static ArkService arkService;

    public VolcengineApi() {
        int processors = Runtime.getRuntime().availableProcessors();
        ConnectionPool connectionPool = new ConnectionPool(processors * 4, 60, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher(Executors.newVirtualThreadPerTaskExecutor());
        arkService = ArkService.builder()
                               .retryTimes(1)
                               .timeout(Duration.ofSeconds(180))
                               .connectTimeout(Duration.ofSeconds(30))
                               .dispatcher(dispatcher)
                               .connectionPool(connectionPool)
                               .baseUrl(VolcengineConst.BASE_URL)
                               .apiKey(VolcengineConst.API_KEY)
                               .build();
    }

    @PreDestroy
    public void destroy() {
        if (null != arkService) {
            arkService.shutdownExecutor();
        }
    }

    /**
     * 对话
     *
     * @param request
     * @return
     */
    public List<ChatCompletionChoice> chatCompletion(ChatCompletionRequest request) {
        RATE_LIMITER.acquire();
        // 发送请求
        ChatCompletionResult result = arkService.createChatCompletion(request);
        List<ChatCompletionChoice> choices = result.getChoices();
        log.info("VolcengineApi chatCompletion: {}", choices);
        return choices;
    }

    public static void main(String[] args) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = ChatMessage.builder()
                                               .role(ChatMessageRole.SYSTEM)
                                               .content("你是个微信聊天AI助手")
                                               .build();
        messages.add(systemMessage);
        ChatMessage userMessage = ChatMessage.builder()
                                             .role(ChatMessageRole.USER)
                                             .content("介绍下自己")
                                             .build();
        messages.add(userMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                                                             .model(VolcengineConst.MODEL_DOUBAO15_PRO)
                                                             .messages(messages)
                                                             .build();
        VolcengineApi volcengineApi = new VolcengineApi();
        List<ChatCompletionChoice> choices = volcengineApi.chatCompletion(request);
        choices.forEach(e -> System.out.println(e.getMessage().getContent()));
        volcengineApi.destroy();
    }

}
