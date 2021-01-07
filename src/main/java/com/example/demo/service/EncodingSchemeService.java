package com.example.demo.service;

import com.example.demo.entity.EncodingScheme;
import com.example.demo.entity.Tester;
import com.example.demo.mapper.EncodingSchemeMapper;
import com.example.demo.mapper.TesterMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EncodingSchemeService {
    @Resource
    private EncodingSchemeMapper encodingSchemeMapper;

    /**
     * 查询数据库中的所有编码方案
     * @return
     */
    public List<EncodingScheme> queryEncodingSchemeList(){
        List<EncodingScheme> encodingSchemeList = encodingSchemeMapper.queryEncodingSchemeList();
//        for(EncodingScheme encodingScheme:encodingSchemeList) {
//            System.out.println("查询到的编码方案"+encodingScheme);
//        }
        return encodingSchemeList;
    }

    public EncodingScheme queryEncodingSchemeById(int id){
        EncodingScheme encodingScheme = encodingSchemeMapper.queryEncodingSchemeById(id);
        System.out.println("通过id查询到的用户"+encodingScheme);
        return encodingScheme;
    }

    /**
     * 添加编码方案
     * @param encodingScheme
     * @return
     */
    public int  addEncodingScheme(EncodingScheme encodingScheme){
        encodingSchemeMapper.addEncodingScheme(encodingScheme);
        return encodingScheme.getIdEncodingScheme();
    }

    /**
     * 删除编码方案根据id
     * @param id
     * @return
     */
    public int deleteEncodingSchemeByID(int id){
        return encodingSchemeMapper.deleteEncodingSchemeByID(id);
    }

    /**
     * 更新编码方案
     * @param encodingScheme
     * @return
     */
    public int updateEncodingScheme(EncodingScheme encodingScheme) {
        return encodingSchemeMapper.updateEncodingScheme(encodingScheme);
    }

    public void updateIsUsedByID(int id,int isUsedStatus){
        encodingSchemeMapper.updateIsUsedByID(id,isUsedStatus);
    }

}
