package com.campus.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获所有未知异常，统一返回 500 和友好提示
    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception ex) {
        ex.printStackTrace(); // 在控制台打印真实堆栈，方便我们开发排查
        return Result.error("系统开小差了，请稍后再试");
    }
}