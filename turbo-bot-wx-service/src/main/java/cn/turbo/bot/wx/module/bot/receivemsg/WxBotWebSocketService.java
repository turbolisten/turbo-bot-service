package cn.turbo.bot.wx.module.bot.receivemsg;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.turbo.bot.base.config.GlobalExecutor;
import cn.turbo.bot.base.module.wxbot.WxBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

/**
 * wx bot websocket
 *
 * @author huke
 * @date 2025/2/7 21:00
 */
@Slf4j
@Service
public class WxBotWebSocketService implements CommandLineRunner, DisposableBean {

    private final AtomicReference<WebSocketSession> sessionRef = new AtomicReference<>();

    @Autowired
    private WxBotMsgHandleService wxBotMsgHandleService;

    @Autowired
    private WxBotService wxBotService;

    @Value("${wx-bot.server.ws}")
    private String wsUrl;

    private boolean isDestroy = false;

    @Override
    public void run(String... args) {
        this.init();
    }

    private void init() {
        // 初始化ws连接
        this.connect();
    }

    @Override
    public void destroy() {
        isDestroy = true;
    }

    /**
     * 连接 ws
     */
    private void connect() {
        ReactorNettyWebSocketClient webSocketClient = new ReactorNettyWebSocketClient();
        webSocketClient.execute(URI.create(wsUrl), session -> {
            sessionRef.set(session);

            // 接收消息
            Mono<Void> receive = session.receive()
                                        .map(WebSocketMessage::getPayloadAsText)
                                        .doOnNext(this::handleMsg)
                                        .then();
            return Mono.fromRunnable(() -> {
                           // 处理连接打开
                           log.info("WxBot WebSocket connected");
                       })
                       // 处理接收消息
                       .then(receive)
                       .doOnTerminate(() -> {
                           // 处理连接关闭事件
                           log.info("WxBot WebSocket connect closed");
                           sessionRef.set(null);
                           this.connectWithRetry();
                       }).onErrorResume(e -> {
                        // 处理异常
                        log.info("WxBot WebSocket err: ", e);
                        sessionRef.set(null);
                        this.connectWithRetry();
                        return Mono.empty();
                    });
        }).subscribe();
    }

    private void handleMsg(String text) {
        // 线程处理
        GlobalExecutor.getExecutor().execute(() -> {
            log.info("WxBot WebSocket receive msg: {}", text);
            try {
                wxBotMsgHandleService.handle(text);
            } catch (Exception e) {
                log.error("WxBot WebSocket Exception in virtual thread ", e);
                wxBotService.sendDevAlertMsg(ExceptionUtil.stacktraceToString(e, 1000));
            }
        });
    }

    /**
     * 重连
     */
    private void connectWithRetry() {
        if (isDestroy) {
            return;
        }
        log.info("WxBot WebSocket Retry connect");
        try {
            Thread.sleep(5000);
            this.connect();
        } catch (InterruptedException e) {
            log.error("WxBot WebSocket Retry connect interrupted: ", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(String message) {
        WebSocketSession session = sessionRef.get();
        if (null == session || !session.isOpen()) {
            log.error("WxBot WebSocket session not open");
            return;
        }
        WebSocketMessage webSocketMessage = session.textMessage(message);
        session.send(Mono.just(webSocketMessage)).subscribe();
        log.info("WxBot WebSocket send msg: {}", message);
    }
}
