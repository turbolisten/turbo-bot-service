package cn.turbo.bot.base.module.config.domain;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

/**
 * bot check 配置
 *
 * @author huke
 * @date 2025/2/13 23:20
 */
@Data
public class BotChatConfigDTO {

    /**
     * 免费 单聊条数
     */
    private Integer freeChatNum;

    /**
     * 免费 群聊条数
     */
    private Integer freeRoomChatNum;

    /**
     * 申请试用文案
     */
    private String applyTrialText;


    public static void main(String[] args) {
        BotChatConfigDTO configDTO = new BotChatConfigDTO();
        configDTO.setFreeChatNum(50);
        configDTO.setFreeRoomChatNum(50);
        configDTO.setApplyTrialText("xxxx");
        System.out.println(JSON.toJSONString(configDTO));
    }
}
