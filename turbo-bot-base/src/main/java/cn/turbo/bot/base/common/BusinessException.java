package cn.turbo.bot.base.common;

/**
 * 自定义 业务异常
 *
 * @author huke
 * @date 2025/2/6 21:31
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
