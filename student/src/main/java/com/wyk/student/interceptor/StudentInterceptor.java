package com.wyk.student.interceptor;


import com.wyk.exception.CustomizeException;
import com.wyk.student.domain.enums.StudentContext;
import com.wyk.util.JwtUtil;
import com.wyk.util.ThreadUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class StudentInterceptor implements HandlerInterceptor {
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_SUB = "Bearer ";
    private static final String CONTEXT_KEY = "username";
    private final JwtUtil jwtUtil;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Claims claims = Optional.ofNullable(jwtUtil.parseToken(token))
                .orElseThrow(() -> {
                    log.warn("令牌无效,令牌为: {}",token);
                    return new CustomizeException("令牌无效", HttpStatus.UNAUTHORIZED.value());
                });
        String username = Optional.ofNullable(claims.get(CONTEXT_KEY))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElseThrow(() -> {
                    log.warn("令牌中无指定信息");
                    return new CustomizeException("令牌中无指定信息", HttpStatus.UNAUTHORIZED.value());
                });
        long id = Long.parseLong(claims.getSubject());
        ThreadUtil.set(StudentContext.USER_ID,id);
        ThreadUtil.set(StudentContext.USERNAME,username);
        return true;
    }

    private String getToken(HttpServletRequest request) {
        return Optional.ofNullable(request)
                .map(req -> req.getHeader(TOKEN_HEADER))
                .filter(token -> !token.trim().isEmpty())
                .filter(token -> token.startsWith(TOKEN_SUB))
                .map(token -> token.substring(TOKEN_SUB.length()))
                .orElseThrow(() -> new CustomizeException("未授权", HttpStatus.UNAUTHORIZED.value()));
    }
    private int getId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("令牌中用户ID格式错误,subject: {}", value);
            throw new CustomizeException("令牌格式错误",HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadUtil.clear();
    }
}
