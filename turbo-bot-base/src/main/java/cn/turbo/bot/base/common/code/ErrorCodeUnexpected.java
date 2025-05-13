package cn.turbo.bot.base.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 未预期的错误码（即发生了不可能发生的事情，此类返回码应该高度重视）
 *
 * @Author
 * @Date 2021/09/27 22:10:46
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeUnexpected implements ErrorCode {
    /**
     * 20001
     */
    BUSINESS_HANDING(20001, "呃~ 业务繁忙，请稍后重试"),

    ;

    private final int code;

    private final String msg;

    @Override
    public int[] range() {
        return new int[]{20001, 30000};
    }
}
