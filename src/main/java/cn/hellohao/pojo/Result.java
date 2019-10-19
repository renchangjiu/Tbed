package cn.hellohao.pojo;

;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author su
 * @date 2019/9/22 14:00
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Result<T> {
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 状态码
     */
    private int code;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 携带的数据
     */
    private T data;

    public static <T> Result<T> success() {
        return new Result<T>(true, 1, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(true, 1, "success", data);
    }


    public static <T> Result<T> success(int status, String message) {
        return new Result<T>(true, status, message, null);
    }


    public static <T> Result<T> success(int status, String message, T data) {
        return new Result<T>(true, status, message, data);
    }


    public static <T> Result<T> error() {
        return new Result<T>(false, -1, "error", null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<T>(false, -1, message, null);
    }

    public static <T> Result<T> error(int status, String message) {
        return new Result<T>(false, -1, message, null);
    }


    public Result(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(T data) {
        this.success = true;
        this.code = 1;
        this.message = "";
        this.data = data;
    }

    public boolean isNotSuccess() {
        return !this.success;
    }

}
