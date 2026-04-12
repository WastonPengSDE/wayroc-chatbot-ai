package com.wayroc.wayrocchatbot.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayroc.wayrocchatbot.common.BaseResponse;
import com.wayroc.wayrocchatbot.common.ErrorCode;
import com.wayroc.wayrocchatbot.common.ResultUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT 登录鉴权过滤器：从 Authorization 头解析 token，校验通过则将 userId 放入 request 属性。
 */
@Component
public class LoginFilter extends OncePerRequestFilter {

    public static final String LOGIN_USER_ID = "loginUserId";

    private static final List<String> EXCLUDE_PATHS = List.of(
            "/user/login",
            "/user/register",
            "/error",
            "/v3/api-docs",
            "/swagger-ui"
    );

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (shouldExclude(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        Long userId = jwtUtils.getUserIdFromToken(authHeader);

        if (userId == null) {
            writeNotLoginResponse(response);
            return;
        }

        request.setAttribute(LOGIN_USER_ID, userId);
        // 放行请求
        filterChain.doFilter(request, response);
    }

    private boolean shouldExclude(String requestUri) {
        if (requestUri == null) return true;
        return EXCLUDE_PATHS.stream().anyMatch(requestUri::startsWith);
    }

    private void writeNotLoginResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        BaseResponse<?> body = ResultUtils.error(ErrorCode.NOT_LOGIN);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
