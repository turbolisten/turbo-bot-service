package cn.turbo.bot.base.module.news;

import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.module.news.api.*;
import cn.turbo.bot.base.module.wxbot.basic.constant.WxEmojiEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 资讯 业务
 *
 * @author huke
 * @date 2025/4/8 22:39
 */
@Service
public class NewsService {

    /**
     * 默认查询资讯 10条
     */
    private static final int DEFAULT_LIMIT = 10;

    @Autowired
    private NewsTouTiaoApi newsTouTiaoApi;

    @Autowired
    private NewsWeiboApi newsWeiboApi;

    @Autowired
    private NewsZhiHuApi newsZhiHuApi;

    @Autowired
    private NewsTieBaApi newsTieBaApi;

    @Autowired
    private NewsJueJinApi newsJueJinApi;

    /**
     * build 组装热门资讯 微信消息
     *
     * @param newsSource
     * @return
     */
    public String buildHotNewsWxMsg(NewsSourceEnum newsSource) {
        String title;
        boolean displayUrl = true;
        switch (newsSource) {
            case WEI_BO -> {
                title = "微博-实时热搜";
                displayUrl = false;
            }
            case ZHI_HU -> {
                title = "知乎-实时热榜";
            }
            case TIE_BA -> {
                title = "贴吧-实时热议";
                displayUrl = false;
            }
            case JUE_JIN -> {
                title = "掘金-实时热门";
            }
            default -> {
                // 默认头条
                title = "头条-实时热门";
            }
        }
        // 查询热门消息
        List<NewsBase> newsList = this.queryHotNews(newsSource);

        // build 消息
        title = StrUtil.format("{}【{}】{}\n\n", WxEmojiEnum.HUO_MIAO.getValue(), title, WxEmojiEnum.HUO_MIAO.getValue());
        String newsFormat = "{}#{}\n";
        String newsFormatWithUrl = "{}【{}】\n   {}{}\n";

        StringBuilder sb = new StringBuilder(title);
        for (int i = 0; i < newsList.size(); i++) {
            int index = i + 1;
            NewsBase newsDTO = newsList.get(i);
            String newsTitle = displayUrl
                               ? StrUtil.format(newsFormatWithUrl, WxEmojiEnum.getByNum(index).getValue(), newsDTO.getTitle(), WxEmojiEnum.HUO_JIAN.getValue(), newsDTO.getUrl())
                               : StrUtil.format(newsFormat, WxEmojiEnum.getByNum(index).getValue(), newsDTO.getTitle());
            sb.append(newsTitle);
        }
        // 去掉最后一行
        // sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    /**
     * 查询热门消息
     *
     * @param newsSource
     * @return
     */
    public List<NewsBase> queryHotNews(NewsSourceEnum newsSource) {
        List<NewsBase> newsList;
        switch (newsSource) {
            case WEI_BO -> {
                newsList = newsWeiboApi.queryHot()
                                       .stream()
                                       .limit(DEFAULT_LIMIT)
                                       .map(e -> new NewsBase(e.getNote(), e.getUrl()))
                                       .collect(Collectors.toList());
            }
            case ZHI_HU -> {
                newsList = newsZhiHuApi.queryHot()
                                       .stream()
                                       .limit(DEFAULT_LIMIT)
                                       .map(e -> new NewsBase(e.getTitle(), e.getUrl()))
                                       .collect(Collectors.toList());
            }
            case TIE_BA -> {
                newsList = newsTieBaApi.queryHot()
                                       .stream()
                                       .limit(DEFAULT_LIMIT)
                                       .map(e -> new NewsBase(e.getTopicName(), e.getTopicUrl()))
                                       .collect(Collectors.toList());
            }
            case JUE_JIN -> {
                newsList = newsJueJinApi.queryArticleRank()
                                        .stream()
                                        .limit(DEFAULT_LIMIT)
                                        .map(e -> new NewsBase(e.getTitle(), e.getUrl()))
                                        .collect(Collectors.toList());
            }
            default -> {
                // 默认头条
                newsList = newsTouTiaoApi.query()
                                         .stream()
                                         .limit(DEFAULT_LIMIT)
                                         .map(e -> new NewsBase(e.getTitle(), e.getUrl()))
                                         .collect(Collectors.toList());
            }
        }
        return newsList;
    }
}
