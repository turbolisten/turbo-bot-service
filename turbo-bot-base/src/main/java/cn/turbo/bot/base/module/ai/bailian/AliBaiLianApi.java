package cn.turbo.bot.base.module.ai.bailian;

import cn.turbo.bot.base.module.ai.bailian.constant.AliBaiLianConst;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.multimodalconversation.AudioParameters;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.utils.JsonUtils;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 阿里百炼 Api
 *
 * @author huke
 * @date 2025/2/6 21:28
 */
@Slf4j
@Service
public class AliBaiLianApi {

    /**
     * 模型调用
     */
    private final Generation gen;

    /**
     * 应用调用
     */
    private final Application application;

    private static final RateLimiter RATE_LIMITER = RateLimiter.create(AliBaiLianConst.REQUESTS_PER_MINUTE / 60);

    public AliBaiLianApi() {
        // init
        gen = new Generation();
        application = new Application();
    }

    /**
     * 模型 对话
     *
     * @param param
     * @return
     */
    public GenerationResult chatCompletion(GenerationParam param) {
        // 限流
        RATE_LIMITER.acquire();
        param.setApiKey(AliBaiLianConst.KEY);
        try {
            return gen.call(param);
        } catch (Exception e) {
            log.error("AliBaiLianApi chatCompletion error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 百炼应用 调用
     *
     * @param param
     */
    public ApplicationResult appChatCompletion(ApplicationParam param) {
        // 限流
        RATE_LIMITER.acquire();
        param.setApiKey(AliBaiLianConst.KEY);
        try {
            ApplicationResult result = application.call(param);
            log.info("AliBaiLianApi appChatCompletion: {}", result);
            return result;
        } catch (Exception e) {
            log.error("AliBaiLianApi appChatCompletion error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        /*StopWatch stopWatch = new StopWatch();
        stopWatch.start("deepseek");
        ApplicationParam param = ApplicationParam.builder()
                                                 .appId(AliBaiLianConst.APP_ID_DEEP_SEEK_R1)
                                                 .prompt("你是谁？")
                                                 .build();
        AliBaiLianApi api = new AliBaiLianApi();
        ApplicationResult result = api.appChatCompletion(param);
        System.out.println(result);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));

        stopWatch.start("qianwen");
        param = ApplicationParam.builder()
                                .appId(AliBaiLianConst.APP_ID_QWEN_MAX)
                                .prompt("你是谁？")
                                .build();
        result = api.appChatCompletion(param);
        System.out.println(result);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));*/

/*        GenerationParam param = GenerationParam.builder()
                                               .model("deepseek-r1")
                                               .prompt("哪吒2今天的票房")
                                               .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                                               .build();
        AliBaiLianApi api = new AliBaiLianApi();
        GenerationResult result = api.chatCompletion(param);
        System.out.println("思考过程：");
        System.out.println(result.getOutput().getChoices().get(0).getMessage().getReasoningContent());
        System.out.println("回复内容：");
        System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent());*/

        MultiModalConversation conv = new MultiModalConversation();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                                                                       .apiKey(AliBaiLianConst.KEY)
                                                                       .model("qwen-tts")
                                                                       .text("今天是个好天气，处处好风光")
                                                                       .voice(AudioParameters.Voice.CHELSIE)
                                                                       .build();
        MultiModalConversationResult result = conv.call(param);
        System.out.println(JsonUtils.toJson(result));
    }
}
