package com.example.demo.service;

import com.example.demo.MyResponse;
import com.example.demo.entity.Tester;
import com.example.demo.mapper.TesterMapper;
import com.example.demo.security.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.temporal.Temporal;


/**
 * 实现用户服务的接口
 *
 * @author xian
 */
@Service
public class SysUserService {
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private TokenUtils tokenUtils;

    @Resource
    private TesterMapper testerMapper;

    /**
     * 用户登录
     *
     * @param tester 用户登录信息
     * @return 用户登录成功返回的Token
     */
    public void login(final Tester tester,HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            // 验证用户名和密码是否对的
//            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//            String password = bCryptPasswordEncoder.encode(tester.getPassword());
//            tester.setPassword(password);
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(tester.getUsername(),
//                            tester.getPassword()));
            Tester tempTester =  testerMapper.queryTesterByUsername(tester.getUsername());
            if(tempTester==null){
                // 设置 HTTP 状态码为 401
                response.setStatus(500);
                response.getWriter().append("用户名不存在");
            }else if(!tester.getPassword().equals(tempTester.getPassword())){
                // 设置 HTTP 状态码为 401
                response.setStatus(500);
                response.getWriter().append("密码错误");
            }else{
                response.setStatus(200);
                response.getWriter().append(tokenUtils.createToken(tempTester));
            }
        } catch (BadCredentialsException | IOException e) {
            //return new MyResponse("ERROR", "用户名或者密码不正确");
        }
        // 生成Token与查询用户权限
//        Tester sysUserData = testerMapper.queryTesterByUsername(tester.getUsername());
//        System.out.println("success");
        //return new MyResponse("SUCCESS",tokenUtils.createToken(sysUserData));
    }

    /**
     * 用户注册
     *
     * @param tester 用户注册信息
     * @return 用户注册结果
     */
    public void save(Tester tester,HttpServletResponse response) throws DataAccessException {
        try {
            // 密码加密存储
            //BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            //String password = bCryptPasswordEncoder.encode(tester.getPassword());
            //tester.setPassword(password);
            Tester testertemp = testerMapper.queryTesterByUsername(tester.getUsername());
            if(testertemp!=null){
                System.out.println("已经存在该用户名");
                // 设置 Json 格式返回
                response.setContentType("application/json;charset=UTF-8");
                // 设置 HTTP 状态码为 401
                //response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setStatus(500);
                // PrintWriter 输出 Response 返回信息
                //PrintWriter writer = response.getWriter();
                //ObjectMapper mapper = new ObjectMapper();
                //MyResponse myResponse = new MyResponse("error", "已经存在该用户名或者用户昵称，或者用户权限出错");
                // 将对象输出为 JSON 格式。可以通过重写 MyResponse 的 toString() ，直接通过 myResponse.toString() 即可
                //writer.write(mapper.writeValueAsString(myResponse));
                response.getWriter().append("已经存在该用户名");

                //return new MyResponse("ERROR", "已经存在该用户名或者用户昵称，或者用户权限出错");
            }else{
                System.out.println("即将添加用户"+tester);
                int primaryid = testerMapper.addTester(tester);
                System.out.println("是否添加成功 "+primaryid);

                //return new MyResponse("SUCCESS", "用户注册成功");
            }

        } catch (DataAccessException | IOException e) {
            //return new MyResponse("ERROR", "已经存在该用户名或者用户昵称，或者用户权限出错");
        }

    }
}
