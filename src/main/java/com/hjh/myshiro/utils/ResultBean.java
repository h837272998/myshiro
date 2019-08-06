package com.hjh.myshiro.utils;

import java.io.Serializable;

/**
 * @Description:
 * @Author: HJH
 * @Date: 2019-05-03 15:03
 */

public class ResultBean<T> implements Serializable {
    private static final int SUCCESS = 0; // 成功
    private static final int CHECK_FAIL = 1; // 失败
    private static final int UNKNOWN_EXCEPTION  = -99; // 抛出异常

    private int code = SUCCESS; // 返回状态

    private String msg = "success";
    private T data;
    public ResultBean() {
        super();
    }

    public ResultBean(T data) {
        super();
        this.data = data;
    }

    public ResultBean(String msg) {
        this.code = CHECK_FAIL;
        this.msg = msg;
    }

    public ResultBean(Throwable e) {
        super();
        this.msg = e.toString();
        this.code = UNKNOWN_EXCEPTION;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}