package cn.turbo.bot.base.module.user.domain;

import cn.turbo.bot.base.module.news.NewsSourceEnum;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户 配置 json 对象
 *
 * @author huke
 * @date 2025/4/8 22:39
 */
@Data
public class UserConfigDTO {

    /**
     * 资讯推送配置
     */
    private ConfigNewsPush newsPush;

    /**
     * 资讯推送配置
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ConfigNewsPush {

        /**
         * 是否开启
         */
        private Boolean enabledFlag;

        /**
         * 推送时间
         */
        private List<ConfigNewsPushTime> pushTimeList;
    }

    /**
     * 资讯推送时间
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ConfigNewsPushTime {
        /**
         * 推送时间 时分格式: 10:15
         */
        private String time;

        /**
         * 新闻来源
         */
        private List<NewsSourceEnum> newsSourceList;
    }

    public static void main(String[] args) {
        ConfigNewsPushTime pushTime1 = new ConfigNewsPushTime("08:30", Lists.newArrayList(NewsSourceEnum.TOU_TIAO, NewsSourceEnum.ZHI_HU));
        ConfigNewsPushTime pushTime2 = new ConfigNewsPushTime("18:00", Lists.newArrayList(NewsSourceEnum.JUE_JIN, NewsSourceEnum.WEI_BO));

        ConfigNewsPush configNewsPush = new ConfigNewsPush(true, Lists.newArrayList(pushTime1, pushTime2));

        UserConfigDTO userConfig = new UserConfigDTO();
        userConfig.setNewsPush(configNewsPush);
        String json = JSON.toJSONString(userConfig);
        System.out.println(json);

        userConfig = JSON.parseObject(json, UserConfigDTO.class);
        System.out.println(userConfig);
    }
}
