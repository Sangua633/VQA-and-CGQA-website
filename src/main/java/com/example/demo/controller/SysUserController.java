package com.example.demo.controller;

import com.example.demo.MyResponse;
import com.example.demo.PermissionResponse;
import com.example.demo.entity.Tester;
import com.example.demo.security.GetToken;
import com.example.demo.service.SysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 实现用户功能接口API
 *
 * @author xian
 */
@RestController

public class SysUserController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    GetToken getToken;



    /**
     * 用户登录接口
     *
     * @param tester 用户登录的用户名和密码
     * @return 用户Token和角色
     * @throws AuthenticationException 身份验证错误抛出异常
     */
    @PostMapping(value = "/login")
    public void login(@RequestBody final Tester tester,HttpServletResponse response) throws AuthenticationException {
        System.out.println("××××××××正在登录×××××××");
        System.out.println("现在是热部署了。。");
        sysUserService.login(tester,response);
    }

    /**
     * 用户注册接口
     *
     * @param tester 用户注册信息
     * @return 用户注册结果
     */
    @PostMapping(value = "/register")
    public void register(@RequestBody final Tester tester, HttpServletResponse response) {
        System.out.println("正在注册用户 "+tester);
        sysUserService.save(tester,response);
    }

    /**
     * 获取用户权限和用户名
     *
     * @param request http请求
     * @return 用户注册结果
     */
    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
    public PermissionResponse getInfo(HttpServletRequest request) throws ServletException, IOException {
        System.out.println("×××××××开始getinfo××××××××");
        String token = getToken.getToken(request);
        System.out.println("token: " + token);
        Tester tester = getToken.getUserFromToken(token);
        System.out.println("tester信息  " + tester);
        List<String> roles = new ArrayList<>();
        roles.add(tester.getPermission());
        System.out.println("×××××××getinfo结束××××××××");
        return new PermissionResponse(token, roles,tester.getUsername());
    }

    /**
     * 这是登录用户才可以看到的内容
     */
    @PostMapping(value = "/message")
    public String message() {
        return "这个消息只有登录用户才可以看到";
    }

    /**
     * 退出
     */
    @PostMapping(value = "/out")
    public String logout() {
        System.out.println("×××××××正在退出登录××××××××");
        return "正在退出登录";
    }


    /**
     * 这是管理员用户才可以看到
     */
    @PostMapping(value = "/admintest")
    @PreAuthorize("hasRole('admin')")
    public String admin() {
        return "这个消息只有管理员用户才可以看到";
    }
}

