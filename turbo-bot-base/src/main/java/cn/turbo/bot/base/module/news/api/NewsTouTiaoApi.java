package cn.turbo.bot.base.module.news.api;

import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.module.news.api.domain.NewsTouTiaoDTO;
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
 * 今日头条 资讯api
 *
 * @author huke
 * @date 2025/4/6 20:37
 */
@Slf4j
@Service
public class NewsTouTiaoApi {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 查询头条
     *
     * @return
     */
    public List<NewsTouTiaoDTO> query() {
        String url = "https://www.toutiao.com/hot-event/hot-board/?origin=toutiao_pc";
        String res = restTemplate.getForObject(url, String.class);
        if (StrUtil.isBlank(res)) {
            log.info("query tou-tiao news err");
            return Collections.emptyList();
        }
        // 转java对象
        JSONArray array = JSON.parseObject(res).getJSONArray("data");
        int size = array.size();
        List<NewsTouTiaoDTO> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            String id = jsonObject.getString("ClusterIdStr");
            String title = jsonObject.getString("Title");

            NewsTouTiaoDTO dto = new NewsTouTiaoDTO();
            dto.setTitle(title);
            // 处理拼接详情 url
            String detailUrl = "https://www.toutiao.com/trending/" + id + "/";
            dto.setUrl(detailUrl);
            list.add(dto);
        }
        return list;
    }

    public static void main(String[] args) {
        NewsTouTiaoApi api = new NewsTouTiaoApi();
        api.restTemplate = new RestTemplate();

        List<NewsTouTiaoDTO> hotList = api.query();
        System.out.println(hotList.size());
        System.out.println(hotList);
    }
}
