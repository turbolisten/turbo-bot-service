package cn.turbo.bot.base.config;

import cn.turbo.bot.base.common.BusinessException;
import cn.turbo.bot.base.common.ResponseDTO;
import cn.turbo.bot.base.config.env.SystemEnvDTO;
import cn.turbo.bot.base.module.wxbot.WxBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MissingRequestValueException;

import java.util.List;

/**
 * 全局异常处理
 *
 * @author huke
 * @date 2025/2/6 21:33
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private SystemEnvDTO systemEnv;

    @Autowired
    private WxBotService wxBotService;

    @ExceptionHandler(BusinessException.class)
    public ResponseDTO<String> handleBusinessError(BusinessException ex) {
        return ResponseDTO.userErrorParam(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseDTO<String> handleException(Exception ex) {
        // 参数校验错误
        if (ex instanceof WebExchangeBindException bindException) {
            List<String> errList = bindException.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            String msg = "param err " + errList;
            return ResponseDTO.userErrorParam(msg);
        }
        // 参数错误
        if (ex instanceof MissingRequestValueException valueException) {
            return ResponseDTO.userErrorParam(valueException.getMessage());
        }
        String message = ex.getMessage();
        log.error("system err {}", message);
        if (!systemEnv.getIsProd()) {
            return ResponseDTO.userErrorParam(message);
        }
        // 发送告警信息
        wxBotService.sendDevAlertMsg("发现幺蛾子\n" + message);
        return ResponseDTO.userErrorParam("system error");
    }

}
