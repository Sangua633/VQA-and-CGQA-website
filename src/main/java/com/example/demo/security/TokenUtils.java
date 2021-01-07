package com.example.demo.security;

import com.example.demo.entity.Tester;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;


import java.io.Serializable;
import java.util.Date;


/**
 * 生成 验证用户Token
 *
 * @author xian
 */
@Component
public class TokenUtils implements Serializable {
    private static final long serialVersionUID = -3L;
    /**
     * Token 有效时长
     */
    private static final Long EXPIRATION = 604800L;  //7天

    /**
     * 生成 Token 字符串  setAudience 接收者 setExpiration 过期时间 role 用户角色
     *
     * @param tester  用户信息
     * @return 生成的Token字符串 or null
     */
    public String createToken(Tester tester) {
        try {
            // Token 的过期时间
            Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION * 1000);
            // 生成 Token
            String token = Jwts.builder()
                    // 设置 Token 签发者 可选
                    .setIssuer("SpringBoot")
                    // 根据用户名设置 Token 的接受者
                    .setAudience(tester.getUsername())
                    // 设置过期时间
                    .setExpiration(expirationDate)
                    // 设置 Token 生成时间 可选
                    .setIssuedAt(new Date())
                    // 通过 claim 方法设置一个 key = role，value = userRole 的值
                    .claim("role", tester.getPermission())
                    // 设置加密密钥和加密算法，注意要用私钥加密且保证私钥不泄露
                    .signWith(RsaUtils.getPrivateKey(), SignatureAlgorithm.RS256)
                    .compact();
            return String.format("Bearer %s", token);
            //return token;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证 Token ，并获取到用户名和用户权限信息
     *
     * @param token Token 字符串
     * @return sysUser 用户信息
     */
    public Tester validationToken(String token) {
        try {
            // 解密 Token，获取 Claims 主体
            Claims claims = Jwts.parserBuilder()
                    // 设置公钥解密，以为私钥是保密的，因此 Token 只能是自己生成的，如此来验证 Token
                    .setSigningKey(RsaUtils.getPublicKey())
                    .build().parseClaimsJws(token).getBody();
            assert claims != null;
            // 验证 Token 有没有过期 过期时间
            Date expiration = claims.getExpiration();
            // 判断是否过期 过期时间要在当前日期之后
            if (!expiration.after(new Date())) {
                System.out.println("token过期");
                return null;
            }
            Tester tester = new Tester();
            tester.setUsername(claims.getAudience());
            tester.setPermission(claims.get("role").toString());
            return tester ;
        } catch (Exception e) {
            System.out.println("验证token时发生异常");
            e.printStackTrace();
            return null;
        }
    }
}
