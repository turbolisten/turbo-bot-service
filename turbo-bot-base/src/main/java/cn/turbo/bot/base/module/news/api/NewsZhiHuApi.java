package cn.turbo.bot.base.module.news.api;

import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.module.news.api.domain.NewsZhiHuDTO;
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
 * 知乎 资讯api
 *
 * @author huke
 * @date 2025/4/6 20:37
 */
@Slf4j
@Service
public class NewsZhiHuApi {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 查询 热点
     *
     * @return
     */
    public List<NewsZhiHuDTO> queryHot() {
        String url = "https://www.zhihu.com/api/v3/feed/topstory/hot-list-web?limit=10&desktop=true";
        String res = restTemplate.getForObject(url, String.class);
        if (StrUtil.isBlank(res)) {
            log.info("query zhi-hu hot err");
            return Collections.emptyList();
        }
        // 转java对象
        JSONArray array = JSON.parseObject(res).getJSONArray("data");
        int size = array.size();
        List<NewsZhiHuDTO> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            JSONObject target = jsonObject.getJSONObject("target");
            String title = target.getJSONObject("title_area").getString("text");
            String link = target.getJSONObject("link").getString("url");

            NewsZhiHuDTO zhiHuDTO = new NewsZhiHuDTO();
            zhiHuDTO.setTitle(title);
            zhiHuDTO.setUrl(link);
            list.add(zhiHuDTO);
        }
        return list;
    }

    public static void main(String[] args) {
        NewsZhiHuApi api = new NewsZhiHuApi();
        api.restTemplate = new RestTemplate();

        List<NewsZhiHuDTO> hotList = api.queryHot();
        System.out.println(hotList.size());
        System.out.println(hotList);
    }
}
