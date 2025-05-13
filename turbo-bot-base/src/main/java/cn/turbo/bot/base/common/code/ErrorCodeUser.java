package cn.turbo.bot.base.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户级别的错误码（用户引起的错误返回码，可以不用关注）
 *
 * @Author
 * @Date 2021/09/21 22:12:27
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeUser implements ErrorCode {
    /**
     * 30001 参数错误
     */
    PARAM_ERROR(30001, "参数错误"),

    DATA_NOT_EXIST(30002, "数据找不到了~"),

    ALREADY_EXIST(30003, "数据已存在了呀~"),

    REPEAT_SUBMIT(30004, "亲~太快了，请稍后再试吧~"),

    NO_PERMISSION(30005, "抱歉~您没有操作权限"),

    DEVELOPING(30006, "系統正在紧急开发中，敬请期待~"),

    LOGIN_STATE_INVALID(30007, "您还未登录或登录失效，请重新登录！"),

    USER_STATUS_ERROR(30008, "用户状态异常,请联系客服"),

    FORM_REPEAT_SUBMIT(30009, "请勿重复提交"),

    LOGIN_FROM_OTHER(30010, "您的账号已在其他地方登录"),
    ;

    private final int code;

    private final String msg;

    @Override
    public int[] range() {
        return new int[]{30001, 40000};
    }
}
