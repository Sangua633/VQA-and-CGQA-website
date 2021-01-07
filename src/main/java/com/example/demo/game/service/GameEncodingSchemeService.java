package com.example.demo.game.service;

import com.example.demo.entity.EncodingScheme;
import com.example.demo.game.entity.GameEncodingScheme;
import com.example.demo.game.mapper.GameEncodingSchemeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GameEncodingSchemeService {
    @Resource
    private GameEncodingSchemeMapper gameEncodingSchemeMapper;
    /**
     * 查询数据库中的所有编码方案
     * @return
     */
    public List<GameEncodingScheme> queryEncodingSchemeList(){
        List<GameEncodingScheme> encodingSchemeList = gameEncodingSchemeMapper.queryEncodingSchemeList();
//        for(EncodingScheme encodingScheme:encodingSchemeList) {
//            System.out.println("查询到的编码方案"+encodingScheme);
//        }
        return encodingSchemeList;
    }

    public GameEncodingScheme queryEncodingSchemeById(int id){
        GameEncodingScheme encodingScheme = gameEncodingSchemeMapper.queryEncodingSchemeById(id);
        System.out.println("通过id查询到的用户"+encodingScheme);
        return encodingScheme;
    }

    /**
     * 添加编码方案
     * @param encodingScheme
     * @return
     */
    public int  addEncodingScheme(GameEncodingScheme encodingScheme){
        gameEncodingSchemeMapper.addEncodingScheme(encodingScheme);
        return encodingScheme.getIdgameEncodingScheme();
    }

    /**
     * 删除编码方案根据id
     * @param id
     * @return
     */
    public int deleteEncodingSchemeByID(int id){
        return gameEncodingSchemeMapper.deleteEncodingSchemeByID(id);
    }

    /**
     * 更新编码方案
     * @param encodingScheme
     * @return
     */
    public int updateEncodingScheme(GameEncodingScheme encodingScheme) {
        return gameEncodingSchemeMapper.updateEncodingScheme(encodingScheme);
    }

    public void updateIsUsedByID(int id,int isUsedStatus){
        gameEncodingSchemeMapper.updateIsUsedByID(id,isUsedStatus);
    }
}
