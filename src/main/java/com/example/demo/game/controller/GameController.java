package com.example.demo.game.controller;

import com.example.demo.game.entity.Game;
import com.example.demo.game.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class GameController {
    @Resource
    private GameService gameService;
    @GetMapping("/getGameList")
    public List<Game> queryGameList(){
        List<Game> gameList = gameService.queryGameList();
        return gameList;
    }
    @GetMapping("/getGameByID")
    public Game queryGame(int id){
        return gameService.queryGameByID(id);
    }

    @GetMapping("/insertGame")
    public int  addGame(@RequestBody final Game game){
        return gameService.addGame(game);
    }

    /**
     * 根据id删除视频
     * 经测试，可用
     * 无需token
     * @param id 用户信息
     * @return 是否删除成功，1成功，0失败
     */
    @GetMapping("/deleteGameByID")
    public int deleteByID(int id){
        return gameService.deleteGameByID(id);
    }
    @GetMapping("/deleteGameByUsername")
    public int deleteByID(String gameName){
        return gameService.deleteGameByGamename(gameName);
    }


}
