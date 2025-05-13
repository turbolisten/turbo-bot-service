package cn.turbo.bot.base.module.news;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资讯 基础
 *
 * @author huke
 * @date 2025/4/6 20:37
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewsBase {

    private String title;

    private String url;

}
