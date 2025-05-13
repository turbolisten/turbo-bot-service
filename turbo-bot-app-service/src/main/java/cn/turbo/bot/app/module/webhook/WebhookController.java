package cn.turbo.bot.app.module.webhook;

import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.common.ResponseDTO;
import cn.turbo.bot.base.module.webhook.request.WebhookRequestMsgTypeEnum;
import cn.turbo.bot.base.module.webhook.request.domain.WebhookRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Webhook
 *
 * @author huke
 * @date 2025/2/12 21:54
 */
@RestController
public class WebhookController {

    @Autowired
    private WebhookService webhookService;

    @GetMapping("/webhook/send")
    public Mono<ResponseDTO<String>> handleWebhook(@RequestParam String key, @RequestParam String text) {
        if (StrUtil.isBlank(key) || StrUtil.isBlank(text)) {
            return Mono.just(ResponseDTO.userErrorParam("param err"));
        }
        WebhookRequest<String> request = new WebhookRequest<>();
        request.setKey(key);
        request.setMsgType(WebhookRequestMsgTypeEnum.TEXT.getValue());
        request.setData(text);
        ResponseDTO<String> responseDTO = webhookService.handle(request);
        return Mono.just(responseDTO);
    }

    @PostMapping(value = "/webhook/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<String>> handleWebhook(@RequestBody @Valid WebhookRequest<?> request) {
        ResponseDTO<String> responseDTO = webhookService.handle(request);
        return Mono.just(responseDTO);
    }

}
