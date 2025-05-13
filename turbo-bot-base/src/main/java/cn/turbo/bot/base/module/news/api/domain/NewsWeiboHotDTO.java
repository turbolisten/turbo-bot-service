package cn.turbo.bot.base.module.news.api.domain;

import lombok.Data;

/**
 * 微博热搜
 *
 * @author huke
 * @date 2025/4/6 20:37
 */
@Data
public class NewsWeiboHotDTO {

    /**
     * 标签名称 爆、新 等
     */
    private String labelName;

    /**
     * 标签内容
     */
    private String note;

    private String url;
}
