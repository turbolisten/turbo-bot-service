package cn.turbo.bot.base.module.news.api;

import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.module.news.api.domain.NewsTieBaDTO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
public class NewsTieBaApi {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 查询 贴吧热议
     *
     * @return
     */
    public List<NewsTieBaDTO> queryHot() {
        String url = "https://tieba.baidu.com/hottopic/browse/topicList";
        String res = restTemplate.getForObject(url, String.class);
        if (StrUtil.isBlank(res)) {
            log.info("query tie-ba hot err");
            return Collections.emptyList();
        }
        JSONArray array = JSON.parseObject(res)
                              .getJSONObject("data")
                              .getJSONObject("bang_topic")
                              .getJSONArray("topic_list");
        return array.toJavaList(NewsTieBaDTO.class);
    }

    public static void main(String[] args) {
        NewsTieBaApi api = new NewsTieBaApi();
        api.restTemplate = new RestTemplate();

        List<NewsTieBaDTO> hotList = api.queryHot();
        System.out.println(hotList.size());
        System.out.println(hotList);
    }
}
