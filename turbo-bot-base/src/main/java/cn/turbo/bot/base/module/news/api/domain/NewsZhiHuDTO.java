package cn.turbo.bot.base.module.news.api.domain;

import lombok.Data;

/**
 * 知乎 热点
 *
 * @author huke
 * @date 2025/4/6 20:37
 */
@Data
public class NewsZhiHuDTO {

    private String id;

    private String title;

    private String url;
}
