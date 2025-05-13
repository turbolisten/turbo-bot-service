package cn.turbo.bot.base.common;


import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.common.code.ErrorCode;
import cn.turbo.bot.base.common.code.ErrorCodeUser;
import lombok.Data;

/**
 * 返回类
 *
 * @author huke
 */
@Data
public class ResponseDTO<T> {

    public static final int OK_CODE = 0;

    public static final String OK_MSG = "success";

    private Integer code;

    private String msg;

    private Boolean ok;

    private T data;

    public ResponseDTO(Integer code, boolean ok, String msg, T data) {
        this.code = code;
        this.ok = ok;
        this.msg = msg;
        this.data = data;
    }

    public ResponseDTO(ErrorCode errorCode, boolean ok, String msg, T data) {
        this.code = errorCode.getCode();
        this.ok = ok;
        if (StrUtil.isNotBlank(msg)) {
            this.msg = msg;
        } else {
            this.msg = errorCode.getMsg();
        }
        this.data = data;
    }

    public static <T> ResponseDTO<T> ok() {
        return new ResponseDTO<>(OK_CODE, true, OK_MSG, null);
    }

    public static <T> ResponseDTO<T> ok(T data) {
        return new ResponseDTO<>(OK_CODE, true, OK_MSG, data);
    }

    public static <T> ResponseDTO<T> okMsg(String msg) {
        return new ResponseDTO<>(OK_CODE, true, msg, null);
    }

    // -------------------------------------------- 最常用的 用户参数 错误码 --------------------------------------------

    public static <T> ResponseDTO<T> userErrorParam() {
        return new ResponseDTO<>(ErrorCodeUser.PARAM_ERROR, false, null, null);
    }


    public static <T> ResponseDTO<T> userErrorParam(String msg) {
        return new ResponseDTO<>(ErrorCodeUser.PARAM_ERROR, false, msg, null);
    }

    // -------------------------------------------- 错误码 --------------------------------------------

    public static <T> ResponseDTO<T> error(ErrorCode errorCode) {
        return new ResponseDTO<>(errorCode, false, null, null);
    }

    public static <T> ResponseDTO<T> error(ErrorCode errorCode, boolean ok) {
        return new ResponseDTO<>(errorCode, ok, null, null);
    }

    public static ResponseDTO<?> error(ResponseDTO<?> responseDTO) {
        return new ResponseDTO<>(responseDTO.getCode(), responseDTO.getOk(), responseDTO.getMsg(), responseDTO.getData());
    }

    public static <T> ResponseDTO<T> error(ErrorCode errorCode, String msg) {
        return new ResponseDTO<>(errorCode, false, msg, null);
    }

    public static <T> ResponseDTO<T> errorData(ErrorCode errorCode, T data) {
        return new ResponseDTO<>(errorCode, false, null, data);
    }

}
