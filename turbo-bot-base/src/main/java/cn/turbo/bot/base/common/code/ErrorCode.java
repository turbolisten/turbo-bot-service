package cn.turbo.bot.base.common.code;

/**
 * 错误码<br>
 * 一共分为三种： 1）系统错误、2）用户级别错误、3）未预期到的错误
 *
 * @date 2021-09-02 20:21:10
 */
public interface ErrorCode {

    /**
     * 错误码
     *
     * @return
     */
    int getCode();

    /**
     * 错误消息
     *
     * @return
     */
    String getMsg();

    /**
     * code 范围
     *
     * @return
     */
    int[] range();
}
