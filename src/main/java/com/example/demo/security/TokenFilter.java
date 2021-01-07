package com.example.demo.security;

import com.example.demo.entity.Tester;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;



import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Jwt 拦截器，通过Token鉴权
 *
 * @author xian
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Service
public class TokenFilter extends OncePerRequestFilter {

    @Resource
    TokenUtils tokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("******×××××××××××*正在拦截××××××××××××××××");
        if("OPTIONS".equals(request.getMethod())){
            System.out.println("这是一个空的option请求,URL为:"+request.getRequestURL());
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.addHeader("Access-Control-Max-Age", "3600");
        }else{
            // 存储Token的Headers Key与Value
            final String authorizationKey = "Authorization";
            String authorizationValue;
            try {
                System.out.println("请求方法："+request.getMethod()+"请求URL： "+request.getRequestURL()+ "请求资源："+request.getQueryString());
                authorizationValue = request.getHeader(authorizationKey);
            } catch (Exception e) {
                authorizationValue = null;
            }
            // Token 开头部分
            System.out.println("获得了token！ header：  "+authorizationValue);
            String bearer = "Bearer ";
            if (authorizationValue != null && authorizationValue.startsWith(bearer)) {
                // token
                String token = authorizationValue.substring(bearer.length());

                Tester tester = tokenUtils.validationToken(token);
                if (tester != null) {
                    // Spring Security 角色名称默认使用 "ROLE_" 开头
                    // authorities.add 可以增加多个用户角色，对于一个用户有多种角色的系统来说，
                    // 可以通过增加用户角色表、用户--角色映射表，存储多个用户角色信息
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + tester.getPermission()));
                    // 传入用户名、用户密码、用户角色。 这里的密码随便写的，用不上
                    UserDetails userDetails = new User(tester.getUsername(), "password", authorities);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(userDetails.getUsername());
                    // 授权
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);

        }
        System.out.println("*****××××××××**拦截结束××××××××××××××××××");
    }
}
