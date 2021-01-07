package com.example.demo.game.mapper;

import com.example.demo.game.entity.GameEncodingScheme;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface GameEncodingSchemeMapper {
    List<GameEncodingScheme> queryEncodingSchemeList();
    GameEncodingScheme queryEncodingSchemeById(int id);
    int addEncodingScheme(GameEncodingScheme encodingScheme);
    int updateEncodingScheme(GameEncodingScheme encodingScheme);
    void updateIsUsedByID(int id,int isUsedStatus);
    int deleteEncodingSchemeByID(int id);
}
