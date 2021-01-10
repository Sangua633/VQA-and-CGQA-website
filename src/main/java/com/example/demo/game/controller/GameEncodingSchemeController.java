package com.example.demo.game.controller;

import com.example.demo.entity.EncodingScheme;
import com.example.demo.entity.VideoJointES;
import com.example.demo.entity.VideoJointES2;
import com.example.demo.game.entity.GameEncodingScheme;
import com.example.demo.game.service.GameEncodingSchemeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/gameEncoding")
public class GameEncodingSchemeController {
    @Resource
    private GameEncodingSchemeService gameEncodingSchemeService;
    /**
     * 返回所有的编码方案
     * @return
     */
    @GetMapping("/getList")
    public List<GameEncodingScheme> queryList(){
        //System.out.println("×××××××开始返回编码方案列表××××××××");
        List<GameEncodingScheme> encodingSchemeList = gameEncodingSchemeService.queryEncodingSchemeList();
        //System.out.println("×××××××返回编码方案列表结束××××××××");
        return encodingSchemeList;
    }

    /** 添加编码方案
     * @param
     */
    @PostMapping("/insert")
    public int addES(@RequestBody GameEncodingScheme gameEncodingScheme) {
        System.out.println("××××××正在添加编码方案××××××");
        gameEncodingScheme.setEncoding("h264");
        gameEncodingScheme.setIsUsed(0);
        System.out.println(gameEncodingScheme);
        gameEncodingSchemeService.addEncodingScheme(gameEncodingScheme);
        System.out.println("××××××添加编码方案结束××××××");
        return gameEncodingScheme.getIdgameEncodingScheme();
    }

    /** 更新编码方案
     *
     * @param  gameEncodingScheme 视编码方案
     */
    @PostMapping("/update")
    public void updateES(@RequestBody  GameEncodingScheme gameEncodingScheme) throws IOException {
        System.out.println("×××××正在更新编码方案××××××");
        gameEncodingScheme.setIsUsed(0);
        gameEncodingSchemeService.updateEncodingScheme(gameEncodingScheme);
        System.out.println("××××××更新编码方案结束××××××");

    }

    /**
     * 根据id删除编码方案
     * @param id 编码方案的id
     * @return
     */
    @GetMapping("/deleteByID")
    public void deleteByID(int id){
        System.out.println(id);
        //删除编码方案表中的数据
        gameEncodingSchemeService.deleteEncodingSchemeByID(id);
    }

    /**
     * 根据编码方案id查询该方案是否被游戏任务用过
     * @param id 编码方案id
     * @return 被用过的次数
     */
    @GetMapping("/queryIsUsed")
    public int queryIsUsed(int id){
        return gameEncodingSchemeService.queryEncodingSchemeById(id).getIsUsed();
    }

}
