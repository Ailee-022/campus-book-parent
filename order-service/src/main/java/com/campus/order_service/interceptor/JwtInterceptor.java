package com.campus.order_service.interceptor;

import com.campus.common.BaseContext;
import com.campus.common.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取请求头中的 Token
        String token = request.getHeader("Authorization");

        // 2. 如果没有 Token，直接拦截，返回 401
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        // 3. 如果带了 "Bearer " 前缀，把它去掉
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 4. 解析 Token，拿到 userId 存入 ThreadLocal
        try {
            Claims claims = JwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            BaseContext.setCurrentId(userId);
            return true; // 放行
        } catch (Exception e) {
            response.setStatus(401); // Token 过期或伪造
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后，清理 ThreadLocal，防止内存泄漏
        BaseContext.removeCurrentId();
    }
}