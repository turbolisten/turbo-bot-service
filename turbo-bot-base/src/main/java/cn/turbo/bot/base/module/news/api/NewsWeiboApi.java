package cn.turbo.bot.base.module.news.api;

import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.module.news.api.domain.NewsWeiboHotDTO;
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
 * 微博 资讯api
 *
 * @author huke
 * @date 2025/4/6 20:37
 */
@Slf4j
@Service
public class NewsWeiboApi {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 查询 热搜
     *
     * @return
     */
    public List<NewsWeiboHotDTO> queryHot() {
        String url = "https://weibo.com/ajax/side/hotSearch";
        String res = restTemplate.getForObject(url, String.class);
        if (StrUtil.isBlank(res)) {
            log.info("query weibo hot err");
            return Collections.emptyList();
        }
        // 转java对象
        JSONArray array = JSON.parseObject(res)
                              .getJSONObject("data")
                              .getJSONArray("realtime");
        int size = array.size();
        List<NewsWeiboHotDTO> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            String labelName = jsonObject.getString("label_name");
            String note = jsonObject.getString("note");
            String word = jsonObject.getString("word");
            String wordScheme = jsonObject.getString("word_scheme");
            String keyword = StrUtil.blankToDefault(word, wordScheme);

            NewsWeiboHotDTO dto = new NewsWeiboHotDTO();
            dto.setLabelName(labelName);
            dto.setNote(note);
            // 处理拼接详情 url
            String detailUrl = "https://s.weibo.com/weibo?q=" + URLEncodeUtil.encode(keyword);
            dto.setUrl(detailUrl);
            list.add(dto);
        }
        return list;
    }

    public static void main(String[] args) {
        NewsWeiboApi api = new NewsWeiboApi();
        api.restTemplate = new RestTemplate();

        List<NewsWeiboHotDTO> hotList = api.queryHot();
        System.out.println(hotList.size());
        System.out.println(hotList);
    }
}
