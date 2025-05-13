package cn.turbo.bot.wx.module.bot.receivemsg;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.common.RedisConst;
import cn.turbo.bot.base.common.StringConst;
import cn.turbo.bot.base.module.ai.bailian.AliBaiLianApi;
import cn.turbo.bot.base.module.ai.bailian.constant.AliBaiLianConst;
import cn.turbo.bot.base.module.ai.basic.AIConst;
import cn.turbo.bot.base.module.ai.basic.domain.AIChatRequest;
import cn.turbo.bot.base.module.ai.basic.domain.AIMessage;
import cn.turbo.bot.base.module.ai.basic.domain.AIResponse;
import cn.turbo.bot.base.module.ai.siliconflow.SiliconFlowApi;
import cn.turbo.bot.base.module.ai.siliconflow.constant.SiliconFlowConst;
import cn.turbo.bot.base.module.ai.volcengine.VolcengineApi;
import cn.turbo.bot.base.module.ai.volcengine.VolcengineConst;
import cn.turbo.bot.base.module.config.ConfigKeyEnum;
import cn.turbo.bot.base.module.config.ConfigService;
import cn.turbo.bot.base.module.user.UserService;
import cn.turbo.bot.base.module.user.domain.UserDTO;
import cn.turbo.bot.base.module.wxbot.WxBotService;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxEmojiEnum;
import cn.turbo.bot.base.module.wxbot.basic.domain.WxBotMsgPacketDTO;
import cn.turbo.bot.base.util.RedisService;
import cn.turbo.bot.base.util.SmartLocalDateUtil;
import cn.turbo.bot.wx.module.bot.receivemsg.constant.WxBotUserActionEnum;
import cn.turbo.bot.wx.module.bot.receivemsg.domain.WxBotUserActionDetectResult;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChoice;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * wx bot 用户行为识别
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
@Service
public class WxBotUserActionDetectService implements CommandLineRunner {

    @Autowired
    private AliBaiLianApi aliBaiLianApi;

    @Autowired
    private SiliconFlowApi siliconFlowApi;

    @Autowired
    private VolcengineApi volcengineApi;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WxBotService wxBotService;

    @Override
    public void run(String... args) {
        // this.buildSystemPrompt();
    }

    /**
     * 检查是否为固定指令
     *
     * @param msgDTO
     * @return
     */
    protected WxBotUserActionDetectResult detectIsFixedAction(WxBotMsgPacketDTO.MsgDTO msgDTO) {
        // 循环遍历
        return Stream.of(WxBotUserActionEnum.values())
                     .filter(e -> CollUtil.isNotEmpty(e.getCommandList()) && e.getCommandList().contains(StrUtil.trim(msgDTO.getContent())))
                     .findFirst()
                     .map(e -> {
                         // 返回检测结果
                         WxBotUserActionDetectResult detectResult = new WxBotUserActionDetectResult();
                         detectResult.setActionType(e.getValue());
                         // build 回复
                         detectResult.setReply(this.buildFixedContent(msgDTO, e));
                         return detectResult;
                     }).orElse(null);
    }

    /**
     * 调用AI
     * 根据 文本 识别用户对话意图
     *
     * @param msgDTO
     * @param userInfo
     * @return
     */
    protected WxBotUserActionDetectResult detectByAi(WxBotMsgPacketDTO.MsgDTO msgDTO, UserService.UserInfo userInfo) {
        try {
            return useAliBaiLian(msgDTO, userInfo);
        } catch (Exception e) {
            // 发送告警信息
            wxBotService.sendDevAlertMsg(ExceptionUtil.stacktraceToString(e, 1000));
            return null;
        }
    }

    /**
     * 使用 阿里云百炼 api
     *
     * @param msgDTO
     * @param userInfo
     * @return
     */
    private WxBotUserActionDetectResult useAliBaiLian(WxBotMsgPacketDTO.MsgDTO msgDTO, UserService.UserInfo userInfo) {
        /**
         * 查询历史对话 sessionId 拼接规则：ToUserName-FromUserName
         * 用于云端托管的多轮对话，自动维护对话上下文。 有效期1小时，最大历史轮数50
         */
        String userOrRoomWxId = msgDTO.getFromUserOrRoomWxId();
        String cacheKey = StrFormatter.format(RedisConst.Key.AI_CHAT_SESSION + "{}-{}", msgDTO.getToUserName(), userOrRoomWxId);
        String sessionId = redisService.getObj(cacheKey, String.class);
        // 构建用户消息 便于AI识别
        UserDTO user = userInfo.getUser();
        UserDTO room = userInfo.getRoom();
        String format = """
                        当前时间：{}
                        消息：{}
                        用户昵称：{}
                        用户介绍：{}
                        """;
        String userPrompt = StrFormatter.format(format,
                                                SmartLocalDateUtil.now(),
                                                msgDTO.getContent(),
                                                msgDTO.getActionNickName(),
                                                user.getUserDesc()
                                               );
        if (msgDTO.getFromRoomFlag() && null != room) {
            userPrompt = userPrompt + StrFormatter.format("群聊介绍：群名-{}，群介绍-{}", room.getUserName(), room.getUserDesc());
        }
        // 构建AI参数 默认使用 通义百炼应用
        ApplicationParam param = ApplicationParam.builder()
                                                 .appId(AliBaiLianConst.APP_ID_QWEN_MAX)
                                                 .prompt(userPrompt)
                                                 .sessionId(sessionId)
                                                 .build();
        // 发送请求 缓存 sessionId  阿里云端session存活时间1小时
        ApplicationResult result = aliBaiLianApi.appChatCompletion(param);
        redisService.putObj(cacheKey, result.getOutput().getSessionId(), Duration.ofMinutes(70));
        // 解析结果
        String content = result.getOutput().getText();
        return this.handleAIResult(content);
    }

    /**
     * 解析 AI结果
     *
     * @param content
     * @return
     */
    private WxBotUserActionDetectResult handleAIResult(String content) {
        // 去除前后的 标记 ```json
        content = content.replaceAll("^```json\\s*", "")
                         .replaceAll("\\s*```$", "");
        // 解析结果
        return JSON.parseObject(content, WxBotUserActionDetectResult.class);
    }

    /**
     * 使用 火山方舟 api
     *
     * @param msg
     * @return
     */
    private WxBotUserActionDetectResult useVolcengine(String msg) {
        // 系统提示词
        String systemPrompt = this.buildSystemPrompt();
        ChatMessage systemMessage = ChatMessage.builder()
                                               .role(ChatMessageRole.SYSTEM)
                                               .content(systemPrompt)
                                               .build();
        // 用户消息
        ChatMessage userMessage = ChatMessage.builder()
                                             .role(ChatMessageRole.USER)
                                             .content(msg)
                                             .build();
        // 请求参数
        ArrayList<ChatMessage> chatMessages = Lists.newArrayList(systemMessage, userMessage);
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                                                             .model(VolcengineConst.MODEL_DOUBAO15_PRO)
                                                             .messages(chatMessages)
                                                             .build();

        // 请求
        List<ChatCompletionChoice> choices = volcengineApi.chatCompletion(request);
        String content = (String) choices.get(0).getMessage().getContent();
        return JSON.parseObject(content, WxBotUserActionDetectResult.class);
    }

    /**
     * 使用 硅基流动 api
     *
     * @param msg
     * @return
     */
    private WxBotUserActionDetectResult useSiliconFlow(String msg) {
        // 系统提示词
        String systemPrompt = this.buildSystemPrompt();
        AIMessage systemMsg = new AIMessage(AIConst.ROLE_SYSTEM, systemPrompt, null);

        // 用户
        AIMessage userMsg = new AIMessage(AIConst.ROLE_USER, msg, null);
        List<AIMessage> messages = List.of(systemMsg, userMsg);

        // 请求
        AIChatRequest.ResponseFormat responseFormat = new AIChatRequest.ResponseFormat(AIConst.RESPONSE_FORMAT_TEXT);
        AIChatRequest request = new AIChatRequest(SiliconFlowConst.MODEL, messages, 0.7, responseFormat);
        AIResponse aiResponse = siliconFlowApi.chatCompletion(request).block();
        String content = aiResponse.getChoices().get(0).getMessage().getContent();
        return JSON.parseObject(content, WxBotUserActionDetectResult.class);
    }

    /**
     * build 系统提示词
     *
     * @return
     */
    private String buildSystemPrompt() {
        // 当前时间
        String now = SmartLocalDateUtil.now();
        // 获取当前系统消息配置
        String systemPrompt = configService.getConfigValue(ConfigKeyEnum.AI_DETECT_USER_ACTION_SYSTEM_PROMPT);
        return StrFormatter.format(systemPrompt, now);
    }

    /**
     * build 固定消息
     *
     * @param actionEnum
     * @return
     */
    private String buildFixedContent(WxBotMsgPacketDTO.MsgDTO msgDTO, WxBotUserActionEnum actionEnum) {
        // 查询id
        if (WxBotUserActionEnum.FIXED_COMMAND_QUERY_ID == actionEnum) {
            String userId = StrFormatter.format("【您的id】{}", msgDTO.getFromUserName());
            String roomId = msgDTO.getFromRoomFlag() ? StrFormatter.format("\n【群聊id】{}", msgDTO.getRoomId()) : StringConst.EMPTY_STR;
            return StrFormatter.format(WxEmojiEnum.SHAN_DIAN.getValue() + " 报~属下已探明您要的信息：\n{}{}", userId, roomId);
        }
        return "一时语塞，无语凝噎" + WxEmojiEnum.FA_DOU.getValue();
    }
}
