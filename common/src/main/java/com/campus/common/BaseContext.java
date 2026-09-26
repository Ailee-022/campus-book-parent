package com.campus.common;

public class BaseContext {
    // ThreadLocal 保证不同线程（不同用户请求）之间的数据互不干扰
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }
}