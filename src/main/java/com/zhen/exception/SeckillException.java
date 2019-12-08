package com.zhen.exception;

//所有秒杀相关异常（让其它所有异常继承这个类）
public class SeckillException extends RuntimeException{

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
