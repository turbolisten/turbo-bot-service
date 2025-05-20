package cn.turbo.bot.app.module.sample;

import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 示例业务
 *
 * @author Turbolisten
 * @date 2025/5/19 07:27
 */
@Service
public class SampleService {

    /**
     * 示例方法
     * 定时推送新闻
     */
    @SneakyThrows
    @Scheduled(cron = "15 10 9 * * ?")
    public void pushNews() {

    }
}
