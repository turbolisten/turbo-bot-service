
package cn.turbo.bot.base.module.wxbot.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.common.BusinessException;
import cn.turbo.bot.base.common.StringConst;
import cn.turbo.bot.base.config.WebClientConfig;
import cn.turbo.bot.base.module.wxbot.api.domain.WxBotContactDTO;
import cn.turbo.bot.base.module.wxbot.api.domain.WxBotSendTextMsgDTO;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotConst;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotMsgTypeEnum;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 新机器人 api业务
 *
 * @author huke
 * @date 2024/02/08 18:58
 */
@Slf4j
@Service
public class WxBotApi {

    @Value("${wx-bot.server.api}")
    private String serverApi;

    private static final RateLimiter API_LIMITER = RateLimiter.create(1);

    @Autowired
    private WebClient webClient;

    /**
     * 发送文本消息
     *
     * @return
     */
    @SneakyThrows
    public void sendTextMsg(WxBotSendTextMsgDTO msgDTO) {
        // 处理字数
        String content = msgDTO.getContent();
        // 每条消息最多 2000字
        String[] split = StrUtil.split(content, 2000);
        for (int i = 0; i < split.length; i++) {
            if (i > 0) {
                TimeUnit.MILLISECONDS.sleep(300);
            }
            String str = split[i];
            Map<String, Object> param = new HashMap<>(10);
            param.put("ToUserName", msgDTO.getToWxId());
            param.put("MsgType", WxBotMsgTypeEnum.TEXT.getValue());
            /**
             * 手动处理需要at的人
             */
            List<String> atUserIdList = Collections.emptyList();
            List<WxBotSendTextMsgDTO.AtUser> atUserList = msgDTO.getAtUserList();
            if (CollUtil.isNotEmpty(atUserList)) {
                atUserIdList = atUserList.stream().map(WxBotSendTextMsgDTO.AtUser::getAtUserId).collect(Collectors.toList());
                // 手动拼装 at+昵称
                List<String> nickNameList = atUserList.stream().map(WxBotSendTextMsgDTO.AtUser::getNickname).toList();
                StringBuilder sb = new StringBuilder();
                for (String s : nickNameList) {
                    sb.append("@").append(s).append(" ");
                }
                str = sb + str;
            }
            param.put("Content", str);
            param.put("AtUsers", CollUtil.join(atUserIdList, StringConst.SEPARATOR));

            // 发送请求
            this.sendFunc(param, msgDTO.getBotWxId(), WxBotConst.Cmd.SEND_TEXT_MSG, false);
        }
    }

    /**
     * 发送 语音消息
     *
     * @param botWxId
     * @param toUserName
     * @param voiceUrl
     */
    public void sendAudioMsg(String botWxId, String toUserName, String VoicePath, String voiceUrl) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("ToUserName", toUserName);
        // 可选字段 语音本地路径 VoicePath, VoiceBase64、VoiceUrl 不能同时存在
        if (StrUtil.isNotBlank(VoicePath)) {
            param.put("VoicePath", VoicePath);
        } else {
            param.put("VoiceUrl", voiceUrl);
        }
        // 语音时长 单位秒 必须
        param.put("VoiceTime", 3);
        JsonNode jsonNode = this.sendFunc(param, botWxId, WxBotConst.Cmd.SEND_AUDIO, true);
        System.out.println(jsonNode);
    }

    /*    *//**
     * 发送APP消息
     *
     * @return
     *//*
    public void sendAppMsg(NewBotSendAppMsgDTO msgDTO) {
        Map<String, Object> param = new HashMap<>(10);
        param.put("ToUserName", msgDTO.getToWxId());
        param.put("MsgType", NewBotMsgTypeEnum.APP.getValue());
        param.put("Content", msgDTO.getXml());

        // 发送请求
        this.sendFunc(param, msgDTO.getBotWxId(), WxBotConst.Cmd.SendAppMsg);
    }

    *//**
     * 发送图片消息
     * 优先级 网络图片 > base64 > 本地图片
     *
     * @return
     *//*
    public void sendImgMsg(NewBotSendImgMsgDTO msgDTO) {
        Map<String, Object> param = new HashMap<>(6);
        param.put("ToUserName", msgDTO.getToWxId());
        if (StringUtils.isNotBlank(msgDTO.getImgUrl())) {
            param.put("ImageUrl", msgDTO.getImgUrl());
        } else if (StringUtils.isNotBlank(msgDTO.getImgBase64())) {
            param.put("ImageBase64", msgDTO.getImgBase64());
        } else if (StringUtils.isNotBlank(msgDTO.getImgPath())) {
            param.put("ImagePath", msgDTO.getImgPath());
        } else {
            throw new SmartBusinessException("img not null");
        }
        // 发送请求
        this.sendFunc(param, msgDTO.getBotWxId(), WxBotConst.Cmd.SendImage);
    }*/


    /**
     * 推送登录
     *
     * @param botWxId
     */
    public void pushLogin(String botWxId) {
        String url = StrFormatter.format(serverApi + WxBotConst.Api.LOGIN_PUSH, botWxId);
        Mono<ApiResDTO> mono = webClient.get()
                                        .uri(url)
                                        .retrieve()
                                        .onStatus(
                                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                                response -> response.bodyToMono(String.class)
                                                                    .flatMap(error -> Mono.error(new BusinessException("WxBot API Error: " + error)))
                                                 )
                                        .bodyToMono(ApiResDTO.class)
                                        .doOnNext(res -> log.info("WxBot API push login: {}", res))
                                        .doOnError(e -> log.error("WxBot API push login Failed", e))
                                        .onErrorResume(e -> {
                                            log.error("WxBot API push login err: ", e);
                                            return Mono.error(e);
                                        });
        mono.subscribe(e -> this.handleRes(e, "push login"));
    }

    /**
     * 获取群聊信息 包含群成员
     * 数据有点大
     *
     * @param botWxId
     * @param roomId
     */
    public WxBotContactDTO getChatRoomInfo(String botWxId, String roomId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("ChatroomID", roomId);

        // 发送请求
        JsonNode jsonNode = this.sendFunc(param, botWxId, WxBotConst.Cmd.ContactInfo, true);
        if (null == jsonNode || !jsonNode.isArray() || jsonNode.size() == 0) {
            return null;
        }
        return JSON.parseObject(jsonNode.get(0).toString(), WxBotContactDTO.class);
    }

    /**
     * 获取好友资料
     *
     * @param botWxId
     * @param wxId
     */
    public WxBotContactDTO getContactInfo(String botWxId, String wxId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("Wxid", new String[]{wxId});

        // 发送请求
        JsonNode jsonNode = this.sendFunc(param, botWxId, WxBotConst.Cmd.ContactInfo, true);
        if (null == jsonNode || !jsonNode.isArray() || jsonNode.size() == 0) {
            return null;
        }
        return JSON.parseObject(jsonNode.get(0).toString(), WxBotContactDTO.class);
    }

    /**
     * 公用发送消息
     *
     * @param param
     * @param botWxId
     * @param cmd
     */
    private JsonNode sendFunc(Map<String, Object> param, String botWxId, Integer cmd, boolean isNeedRes) {
        API_LIMITER.acquire();

        // 组装参数
        Map<String, Object> sendParam = Maps.newHashMap();
        sendParam.put("CgiCmd", cmd);
        sendParam.put("CgiRequest", param);
        // api
        String url = StrFormatter.format(serverApi + WxBotConst.Api.COMMON_API, botWxId);
        // 发送请求
        Mono<ApiResDTO> mono = webClient.post()
                                        .uri(url)
                                        .bodyValue(sendParam)
                                        .retrieve()
                                        .onStatus(
                                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                                response -> response.bodyToMono(String.class)
                                                                    .flatMap(error -> Mono.error(new BusinessException("WxBot API Error: " + error)))
                                                 )
                                        .bodyToMono(ApiResDTO.class)
                                        //.doOnNext(res -> log.info("WxBot API Response: {}", res))
                                        .doOnError(e -> log.error("WxBot API Call Failed", e))
                                        .onErrorResume(e -> {
                                            log.error("WxBot API err: ", e);
                                            return Mono.error(e);
                                        });
        // 需要返回结果
        if (isNeedRes) {
            return this.handleRes(mono.block(), cmd);
        }
        mono.subscribe(e -> this.handleRes(e, cmd));
        return null;
    }

    /**
     * 处理结果
     *
     * @param res
     * @param obj
     */
    private JsonNode handleRes(ApiResDTO res, Object... obj) {
        if (null == res) {
            return null;
        }
        ApiResDTO.CgiBaseResponse cgiBaseResponse = res.getCgiBaseResponse();
        if (cgiBaseResponse == null) {
            log.error("WxBot API send {} error: res is null", obj);
            return null;
        }
        if (!Objects.equals(ApiResDTO.SUCCESS_CODE, cgiBaseResponse.getRet())) {
            log.error("WxBot API send {} error:{} msg->{}", obj, cgiBaseResponse.getRet(), cgiBaseResponse.getErrMsg());
            return null;
        }
        return res.getResponseData();
    }

    /**
     * api 返回数据
     */
    @Data
    public static class ApiResDTO {
        /**
         * 成功code码
         */
        public static final int SUCCESS_CODE = 0;

        @JsonProperty("CgiBaseResponse")
        private CgiBaseResponse cgiBaseResponse;

        // 自动适配对象、数组、原始值
        @JsonProperty("ResponseData")
        private JsonNode responseData;

        @Data
        public static class CgiBaseResponse {

            @JsonProperty("Ret")
            private Integer ret;

            @JsonProperty("ErrMsg")
            private String errMsg;
        }
    }

    @SneakyThrows

    public static void main(String[] args) {
        WebClientConfig webClientConfig = new WebClientConfig();

        WxBotApi wxBotApi = new WxBotApi();
        wxBotApi.webClient = webClientConfig.webClient();
        wxBotApi.serverApi = "http://127.0.0.1:10110";
        String botWxId = "baaicangzai";
        String wxId = "sunnybye";

        wxBotApi.sendAudioMsg(botWxId, wxId, "/home/test2.silk",null);

        //WxBotContactDTO contactInfo = wxBotApi.getContactInfo(botWxId, wxId);
        //System.out.println(contactInfo);
    }
}
