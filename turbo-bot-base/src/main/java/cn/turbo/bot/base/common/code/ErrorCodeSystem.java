package cn.turbo.bot.base.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统错误状态码（此类返回码应该高度重视）
 *
 * @Author
 * @Date 2021/10/24 20:09
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeSystem implements ErrorCode {

    /**
     * 10001
     */
    SYSTEM_ERROR(10001, "系统似乎出现了点小问题"),

    SYSTEM_BUSY(10002, "系统繁忙,请稍候重试~"),

    ;

    private final int code;

    private final String msg;

    @Override
    public int[] range() {
        return new int[]{10001, 20000};
    }
}

