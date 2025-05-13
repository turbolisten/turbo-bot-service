package cn.turbo.bot.base.module.ai.deepseek;

import cn.turbo.bot.base.common.BusinessException;
import cn.turbo.bot.base.module.ai.basic.AIConst;
import cn.turbo.bot.base.module.ai.basic.domain.AIChatRequest;
import cn.turbo.bot.base.module.ai.basic.domain.AIMessage;
import cn.turbo.bot.base.module.ai.basic.domain.AIResponse;
import cn.turbo.bot.base.module.ai.deepseek.constant.DeepSeekConst;
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
 * DeepSeek Api
 *
 * @author huke
 * @date 2025/2/6 21:28
 */
@Slf4j
@Service
public class DeepSeekApi {

    private final WebClient webClient;

    private static final Long TIMEOUT = 60000L;

    public DeepSeekApi() {
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
                        .baseUrl(DeepSeekConst.BASE_URL)
                        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + DeepSeekConst.KEY)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build();
    }

    public Mono<AIResponse> chatCompletion(AIChatRequest request) {
        return webClient.post()
                        .uri("/chat/completions")
                        .bodyValue(request)
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                response -> response.bodyToMono(String.class)
                                                    .flatMap(error -> Mono.error(new BusinessException("DeepSeek API Error: " + error)))
                                 )
                        .bodyToMono(AIResponse.class)
                        .doOnNext(res -> log.info("DeepSeek API Response: {}", res))
                        .doOnError(e -> log.error("DeepSeek API Call Failed", e))
                        .onErrorResume(e -> {
                            log.error("DeepSeek API err: ", e);
                            return Mono.error(e);
                        });
    }

    public static void main(String[] args) {
        // 消息
        AIMessage message1 = new AIMessage(AIConst.ROLE_USER, "你好啊",null);
        List<AIMessage> messages = List.of(message1);

        // 请求
        AIChatRequest request = new AIChatRequest(DeepSeekConst.MODEL_R1, messages, 0.7, null);

        DeepSeekApi deepSeekApi = new DeepSeekApi();
        Mono<AIResponse> mono = deepSeekApi.chatCompletion(request);
        System.out.println(mono.block());
    }
}
