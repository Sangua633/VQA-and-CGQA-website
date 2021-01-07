package com.example.demo.controller;

import com.example.demo.entity.Tester;
import com.example.demo.mapper.TesterMapper;
import com.example.demo.security.GetToken;
import com.example.demo.service.SysUserService;
import com.example.demo.service.TesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
@RestController
public class TesterController {

    @Resource
    private TesterService testerService;
    @Resource
    GetToken getToken;

    @GetMapping("/getTesterList")
    public List<Tester> queryTesterList(){
        System.out.println("×××××××开始返回列表××××××××");
        List<Tester> testerList = testerService.queryTesterList();

        //System.out.println();
        System.out.println("×××××××返回列表结束××××××××");
        return testerList;
    }
    @GetMapping("/getEditorList")
    public List<Tester> queryEditorList(){
        //System.out.println("×××××××开始返回普通人的列表××××××××");
        List<Tester> testerList = testerService.queryEditorList();
        //System.out.println();
        //System.out.println("×××××××返回普通人的列表结束××××××××");
        return testerList;
    }

    @GetMapping("/getTesterById")
    public Tester queryTesterById(int id){
        return testerService.queryTesterById(id);
    }

    @GetMapping("/getTesterByUsername")
    public Tester queryTesterByUsername(String username){
        //System.out.println(username);
        return testerService.queryTesterByUsername(username);
    }

    /***
     * 查询这个用户名是否被注册
     * @param username 用户名
     * @return 已经被注册，就返回“True”，如果没有注册，返回“False”
     */
    @GetMapping("/canUsedUsername")
    public String isTesterByUsername(String username){
        Tester tester = testerService.queryTesterByUsername(username);
        if(tester!=null){
            return "True";
        }else{
            return "False";
        }
    }
    /**
     * 添加用户
     * @param tester 用户信息
     * @return 是否删除成功，1成功，0失败
     */
    @PostMapping("/insertTester")
    public int  addTester(@RequestBody final Tester tester){
        return testerService.addTester(tester);
    }
    /**
     * 根据id删除用户
     * 经测试，可用
     * 无需token
     * @param id 用户信息
     * @return 是否删除成功，1成功，0失败
     */
    @GetMapping("/deleteByID")
    public int deleteByID(int id){
        return testerService.deleteTesterByID(id);
    }
    @GetMapping("/deleteByUsername")
    public int deleteByID(String username){
        return testerService.deleteTesterByUsername(username);
    }

    /**
     * 更新用户信息，需提供username
     * 经测试，可用
     * 无需token
     * @param tester 用户信息
     * @return 是否更新成功，1成功，0失败
     */
    @PostMapping("updateTester")
    public int updateTester(@RequestBody final Tester tester){
        return testerService.updateTester(tester);
    }


}
