package cn.turbo.bot.base.listener;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 启动监听器
 *
 * @author huke
 * @date 2025-02-15
 */
@Slf4j
@Component
public class ApplicationListener implements org.springframework.context.ApplicationListener<WebServerInitializedEvent> {

    public static String SYSTEM_PRINT_INFO = null;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        WebServer server = webServerInitializedEvent.getWebServer();
        WebServerApplicationContext context = webServerInitializedEvent.getApplicationContext();
        Environment env = context.getEnvironment();
        //获取服务信息
        String ip = NetUtil.getLocalhost().getHostAddress();
        Integer port = server.getPort();
        String contextPath = env.getProperty("server.servlet.context-path");
        if (contextPath == null) {
            contextPath = "";
        }
        String profile = env.getProperty("spring.profiles.active");
        String appName = env.getProperty("spring.application.name");
        //拼接服务地址
        String title = String.format("-------------- Project Start ☺ %s ☑%s ----------------------------", appName, profile);
        String localhostUrl = URLUtil.normalize(String.format("http://localhost:%d%s", port, contextPath), false, true);
        String externalUrl = URLUtil.normalize(String.format("http://%s:%d%s", ip, port, contextPath), false, true);
        String swaggerUrl = URLUtil.normalize(String.format("http://localhost:%d%s/swagger-ui.html", port, contextPath), false, true);
        SYSTEM_PRINT_INFO = String.format("\n%s\n" +
                        "\tLocal:\t\t%s" +
                        "\n\tExternal:\t%s" +
                        "\n\tSwagger:\t%s" +
                        "\n-------------------------------------------------------------------------------------\n",
                title, localhostUrl, externalUrl, swaggerUrl);
    }
}