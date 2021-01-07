package com.example.demo.mapper;

import com.example.demo.entity.Tester;
import com.example.demo.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TesterMapper {
    List<Tester> queryTesterList();
    List<Tester> queryEditorList();
    Tester queryTesterById(int id);
    Tester queryTesterByUsername(String username);
    int addTester(Tester tester);
    int updateTester(Tester tester);
    void updateIsUsedByUsername(String username,int isUsedStatus);
    //int updateTesterMap(Map<String,Object> map);
    int deleteTesterByID(int id);
    int deleteTesterByUsername(String username);
}
