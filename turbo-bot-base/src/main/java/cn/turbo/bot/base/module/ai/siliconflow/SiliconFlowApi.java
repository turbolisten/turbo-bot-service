package cn.turbo.bot.base.module.ai.siliconflow;

import cn.turbo.bot.base.common.BusinessException;
import cn.turbo.bot.base.module.ai.basic.AIConst;
import cn.turbo.bot.base.module.ai.basic.domain.AIChatRequest;
import cn.turbo.bot.base.module.ai.basic.domain.AIMessage;
import cn.turbo.bot.base.module.ai.basic.domain.AIResponse;
import cn.turbo.bot.base.module.ai.siliconflow.constant.SiliconFlowConst;
import com.google.common.util.concurrent.RateLimiter;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;

/**
 * SiliconFlow Api
 *
 * @author huke
 * @date 2025/2/6 21:28
 */
@Slf4j
@Service
public class SiliconFlowApi {

    private final WebClient webClient;

    private static final Long TIMEOUT = 60000L;

    private static final RateLimiter RATE_LIMITER = RateLimiter.create(SiliconFlowConst.REQUESTS_PER_MINUTE / 60);

    public SiliconFlowApi() {
        // init
        this.webClient = this.initWebClient();
    }

    /**
     * 初始化 WebClient 使用非阻塞特性提升并发性能
     *
     * @return
     */
    private WebClient initWebClient() {
        HttpClient httpClient = HttpClient.create()
                                          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT.intValue())
                                          .responseTimeout(Duration.ofMillis(TIMEOUT));

        return WebClient.builder()
                        .clientConnector(new ReactorClientHttpConnector(httpClient))
                        .baseUrl(SiliconFlowConst.BASE_URL)
                        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + SiliconFlowConst.KEY)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build();
    }

    public Mono<AIResponse> chatCompletion(AIChatRequest request) {
        // 限流
        RATE_LIMITER.acquire();
        return webClient.post()
                        .uri("/chat/completions")
                        .bodyValue(request)
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                response -> response.bodyToMono(String.class)
                                                    .flatMap(error -> Mono.error(new BusinessException("SiliconFlow API Error: " + error)))
                                 )
                        .bodyToMono(AIResponse.class)
                        .doOnNext(res -> log.info("SiliconFlow API Response: {}", res))
                        .doOnError(e -> log.error("SiliconFlow API Call Failed", e))
                        .onErrorResume(e -> {
                            log.error("SiliconFlow API err: ", e);
                            return Mono.error(e);
                        });
    }

    public static void main(String[] args) {
        // 消息
        AIMessage message1 = new AIMessage(AIConst.ROLE_USER, "你好啊,你叫什么名字", null);
        List<AIMessage> messages = List.of(message1);

        // 请求
        AIChatRequest request = new AIChatRequest(SiliconFlowConst.MODEL, messages, 0.7, null);

        SiliconFlowApi siliconFlowApi = new SiliconFlowApi();
        AIResponse res = siliconFlowApi.chatCompletion(request).block();
        System.out.println(res.getChoices().get(0).getMessage().getContent());
    }
}
