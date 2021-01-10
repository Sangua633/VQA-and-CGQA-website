package com.example.demo.game.service;


import com.example.demo.game.entity.Game;
import com.example.demo.game.mapper.GameMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GameService {
    @Resource
    private GameMapper gameMapper;
    /**
     * 查询数据库中的所有视频信息
     * @return
     */
    public List<Game> queryGameList(){
        List<Game> gameList = gameMapper.queryGameList();
        for(Game game:gameList) {
            System.out.println("查询到的视频"+game);
        }
        return gameList;
    }
    public Game queryGameByID(int id){
        return gameMapper.queryGameById(id);

    }


    /**
     * 通过游戏名查询游戏
     * @param gamename 游戏名
     * @return 视频
     */
    public Game queryByGameName(String gamename){
        Game game = gameMapper.queryGameByGameName(gamename);
        return game;
    }

    public int  addGame(Game game){
        return gameMapper.addGame(game);
    }
    public int deleteGameByID(int idgame){
        return gameMapper.deleteGameByID(idgame);
    }
    public int deleteGameByGameName(String gamename){
        return gameMapper.deleteGameByGameName(gamename);
    }
}
