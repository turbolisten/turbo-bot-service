package cn.turbo.bot.base.module.wxbot.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信联系人信息
 *
 * @author huke
 * @date 2025/4/9 19:40
 */
@Data
public class WxBotContactDTO {

    /**
     * 微信id
     */
    @JsonProperty("UserName")
    private String userName;

    /**
     * 昵称
     */
    @JsonProperty("NickName")
    private String nickName;

    @JsonProperty("Remark")
    private String remark;
}
