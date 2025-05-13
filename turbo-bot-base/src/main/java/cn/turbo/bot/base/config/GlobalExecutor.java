package cn.turbo.bot.base.config;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 全局 线程池
 *
 * @author huke
 * @date 2025/4/08 22:07
 */
@Slf4j
public class GlobalExecutor {

    // 全局虚拟线程池
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public static ExecutorService getExecutor() {
        return executor;
    }

    /**
     * 关闭钩子
     */
    static {
        Thread hook = new Thread(() -> {
            try {
                executor.close();
            } catch (Exception e) {
                log.error("Error closing GlobalExecutor: {}", e.getMessage());
            }
        }, "GlobalExecutor-ShutdownHook");
        hook.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(hook);
    }
}
