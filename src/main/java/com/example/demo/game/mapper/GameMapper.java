package com.example.demo.game.mapper;

import com.example.demo.entity.Video;
import com.example.demo.game.entity.Game;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GameMapper {
    List<Game> queryGameList();
    Game queryGameById(int id);
    Game queryGameByGameName(String gameName);
    int addGame(Game game );
    int updateGame(Game game);
    int deleteGameByID(int id);
    int deleteGameByGameName(String gameName);
}
