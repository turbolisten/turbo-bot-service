package cn.turbo.bot.base.module.news.api;

import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.module.news.api.domain.NewsJueJinDTO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 贴吧 资讯api
 *
 * @author huke
 * @date 2025/4/6 20:37
 */
@Slf4j
@Service
public class NewsJueJinApi {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 查询 热门文章
     *
     * @return
     */
    public List<NewsJueJinDTO> queryArticleRank() {
        // 查询热门文章 count 不起作用
        String url = "https://api.juejin.cn/content_api/v1/content/article_rank?category_id=1&type=hot&count=10&from=0&spider=0";
        String res = restTemplate.getForObject(url, String.class);
        if (StrUtil.isBlank(res)) {
            log.info("query jue-jin article rank err");
            return Collections.emptyList();
        }
        // 转java对象
        JSONArray array = JSON.parseObject(res).getJSONArray("data");
        int size = array.size();
        List<NewsJueJinDTO> list = new ArrayList<>(size);
        String defailUrlPrefix = "https://juejin.cn/post/";
        for (int i = 0; i < size; i++) {
            JSONObject content = array.getJSONObject(i).getJSONObject("content");
            String id = content.getString("content_id");

            NewsJueJinDTO jueJinDTO = new NewsJueJinDTO();
            jueJinDTO.setTitle(content.getString("title"));
            // 处理拼接详情 url
            String detailUrl = defailUrlPrefix + id;
            jueJinDTO.setUrl(detailUrl);
            list.add(jueJinDTO);
        }
        return list;
    }

    public static void main(String[] args) {
        NewsJueJinApi api = new NewsJueJinApi();
        api.restTemplate = new RestTemplate();

        List<NewsJueJinDTO> hotList = api.queryArticleRank();
        System.out.println(hotList.size());
        System.out.println(hotList);
    }
}
