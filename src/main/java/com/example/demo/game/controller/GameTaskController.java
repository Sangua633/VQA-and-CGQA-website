package com.example.demo.game.controller;

import com.example.demo.game.entity.GameEncodingScheme;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/game")
public class GameTaskController {
    @GetMapping("/adminPlay")
    public String gameTaskAdminPlay(int id){
        //将游戏任务id设置为1
        return "ok";

    }
    @GetMapping("/play")
    public String play(String username){
        System.out.println("当前用户为："+username);
        return "ok";
    }

    /**
     * 返回请求编码方案
     * @return
     */
    @GetMapping("/setPara")
    public GameEncodingScheme query(){
        System.out.println("×××××××给游戏服务器返回编码参数××××××××");
        GameEncodingScheme gameEncodingScheme = new GameEncodingScheme(1,720,1280,20,"h264",20,1);
        return gameEncodingScheme;
    }
}
