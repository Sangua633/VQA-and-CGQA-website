package com.example.demo.service;

import com.example.demo.entity.Tester;
import com.example.demo.mapper.TesterMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * 实现用户曾删改查的接口
 *
 * @author xian
 */
@Service
public class TesterService {
    @Resource
    private TesterMapper testerMapper;

    /**
     * 查询数据库中的所有用户信息
     * @return
     */
    public List<Tester> queryTesterList(){
        List<Tester> testerList = testerMapper.queryTesterList();
        for(Tester tester:testerList) {
            System.out.println("查询到的用户"+tester);
        }
        return testerList;
    }
    public List<Tester> queryEditorList(){
        List<Tester> testerList = testerMapper.queryEditorList();
//        for(Tester tester:testerList) {
//            System.out.println("查询到的普通用户"+tester);
//        }
        return testerList;
    }

    public Tester queryTesterById(int id){
        Tester tester = testerMapper.queryTesterById(id);
        System.out.println("通过id查询到的用户"+tester);
        return tester;
    }
    public Tester queryTesterByUsername(String username){
        Tester tester = testerMapper.queryTesterByUsername(username);
        System.out.println("通过用户名 "+username+" 查询到的用户为"+tester);
        return tester;
    }

    public int  addTester(Tester tester){
        return testerMapper.addTester(tester);
    }
    public int deleteTesterByID(int idtester){
        return testerMapper.deleteTesterByID(idtester);
    }
    public int deleteTesterByUsername(String username){
        return testerMapper.deleteTesterByUsername(username);
    }
    public int updateTester(Tester tester) {
        return testerMapper.updateTester(tester);
    }
    public void updateIsUsedByID(String username,int isUsedStatus){
        testerMapper.updateIsUsedByUsername(username,isUsedStatus);
    }
}
