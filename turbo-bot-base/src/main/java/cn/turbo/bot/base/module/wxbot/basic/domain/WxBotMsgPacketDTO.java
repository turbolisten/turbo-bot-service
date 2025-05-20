package cn.turbo.bot.base.module.wxbot.basic.domain;

import cn.turbo.bot.base.module.wxbot.api.domain.WxBotSendTextMsgDTO;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxBotMsgTypeEnum;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微信消息包
 *
 * @author huke
 * @date 2025/2/7 22:11
 */
@NoArgsConstructor
@Data
public class WxBotMsgPacketDTO {

    @JsonProperty("CurrentPacket")
    private CurrentPacketDTO currentPacket;

    @JsonProperty("CurrentWxid")
    private String currentWxId;

    @Data
    public static class CurrentPacketDTO {

        @JsonProperty("WebConnId")
        private String webConnId;

        @JsonProperty("Data")
        private JSONObject data;
    }

    @Data
    public static class MsgDTO {

        @JsonProperty("MsgId")
        private Integer msgId;

        @JsonProperty("FromUserName")
        private String fromUserName;

        @JsonProperty("ToUserName")
        private String toUserName;

        /**
         * 消息类型
         *
         * @see WxBotMsgTypeEnum
         */
        @JsonProperty("MsgType")
        private Integer msgType;

        @JsonProperty("Content")
        private String content;

        @JsonProperty("Status")
        private Integer status;

        @JsonProperty("ImgStatus")
        private Integer imgStatus;

        @JsonProperty("ImgBuf")
        private Object imgBuf;

        @JsonProperty("CreateTime")
        private Integer createTime;

        @JsonProperty("MsgSource")
        private String msgSource;

        @JsonProperty("PushContent")
        private String pushContent;

        @JsonProperty("NewMsgId")
        private Long newMsgId;

        @JsonProperty("NewMsgIdExt")
        private String newMsgIdExt;

        @JsonProperty("ActionUserName")
        private String actionUserName;

        @JsonProperty("ActionNickName")
        private String actionNickName;

        /**
         * 自定义字段 是否来自群聊
         */
        private Boolean fromRoomFlag;

        /**
         * 自定义字段 群聊 id
         */
        private String roomId;

        /**
         * 自定义字段 原始的消息来源 id - 个人/群聊id
         */
        private String fromUserOrRoomWxId;

        /**
         * 自定义字段 群聊回复需要at的人
         */
        private List<WxBotSendTextMsgDTO.AtUser> atUserList;

        /**
         * 自定义字段 群聊是否at bot
         */
        private Boolean isAtBot;
    }

    @NoArgsConstructor
    @Data
    public static class ContactDTO {

        @JsonProperty("MsgType")
        private Integer msgType;
        @JsonProperty("UserName")
        private String userName;
        @JsonProperty("NickName")
        private String nickName;
        @JsonProperty("Signature")
        private String signature;
        @JsonProperty("SmallHeadImgUrl")
        private String smallHeadImgUrl;
        @JsonProperty("BigHeadImgUrl")
        private String bigHeadImgUrl;
        @JsonProperty("Province")
        private String province;
        @JsonProperty("City")
        private String city;
        @JsonProperty("Remark")
        private String remark;
        @JsonProperty("Alias")
        private String alias;
        @JsonProperty("Sex")
        private Integer sex;
        @JsonProperty("ContactType")
        private Integer contactType;
        @JsonProperty("VerifyFlag")
        private Integer verifyFlag;
        @JsonProperty("LabelLists")
        private String labelLists;
        @JsonProperty("ChatRoomOwner")
        private String chatRoomOwner;
        @JsonProperty("EncryptUsername")
        private String encryptUsername;
        @JsonProperty("ExtInfo")
        private String extInfo;
        @JsonProperty("ExtInfoExt")
        private String extInfoExt;
        @JsonProperty("ChatRoomMember")
        private Object chatRoomMember;
        @JsonProperty("Ticket")
        private String ticket;
        @JsonProperty("ChatroomVersion")
        private Integer chatroomVersion;
    }
}


