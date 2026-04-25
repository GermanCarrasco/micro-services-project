package com.bank.platform.account_service.config;

import com.bank.platform.security.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserContextInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

//        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");
        String username = request.getHeader("X-User-Username");

//        if (userId == null) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false;
//        }

        UserContext.set(role, username);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        UserContext.clear(); // 🔥 evitar memory leaks
    }
}
