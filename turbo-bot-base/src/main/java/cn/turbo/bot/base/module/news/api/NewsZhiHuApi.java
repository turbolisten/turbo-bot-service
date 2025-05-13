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
        // 这个 limit 不起作用
        String url = "https://www.zhihu.com/api/v3/feed/topstory/hot-lists/total?limit=10&desktop=true";
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
            String id = target.getString("id");
            String title = target.getString("title");

            NewsZhiHuDTO zhiHuDTO = new NewsZhiHuDTO();
            zhiHuDTO.setId(id);
            zhiHuDTO.setTitle(title);
            // 处理拼接详情 url
            String detailUrl = "https://www.zhihu.com/question/" + id;
            zhiHuDTO.setUrl(detailUrl);
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
