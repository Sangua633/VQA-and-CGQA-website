package com.example.demo.security;

import com.example.demo.entity.Tester;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class GetToken {
    @Resource
    TokenUtils tokenUtils;
    public String getToken(HttpServletRequest request){
        String token = null;
        // 存储Token的Headers Key与Value
        final String authorizationKey = "Authorization";
        String authorizationValue;
        try {
            authorizationValue = request.getHeader(authorizationKey);
        } catch (Exception e) {
            authorizationValue = null;
        }
        // Token 开头部分
        String bearer = "Bearer ";
        if (authorizationValue != null && authorizationValue.startsWith(bearer)) {
            // token
            token = authorizationValue.substring(bearer.length());
        }
        return token;
    }

    public Tester getUserFromToken(String token)
            throws ServletException, IOException {
        Tester tester = null;
//        // 存储Token的Headers Key与Value
//        final String authorizationKey = "Authorization";
//        String authorizationValue;
//        try {
//            authorizationValue = request.getHeader(authorizationKey);
//        } catch (Exception e) {
//            authorizationValue = null;
//        }
//        // Token 开头部分
//        String bearer = "Bearer ";
//        if (authorizationValue != null && authorizationValue.startsWith(bearer)) {
//            // token
//            String token = authorizationValue.substring(bearer.length());

        tester = tokenUtils.validationToken(token);
//        }
        return tester;
    }
}
