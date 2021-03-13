package com.example.demo.game.mapper;


import com.example.demo.game.entity.GameTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GameTaskMapper {
    List<GameTask> queryGameTaskList();
    GameTask queryGameTaskById(int id);
    GameTask queryGameTaskByStatus(int status);

    int addGameTask(GameTask gameTask);

    int updateGameTask(GameTask gameTask);
    int updateGameTaskByRealData(GameTask gameTask);
    void updateStatusById(int id,int status);
    void updateAssessmentStatusById(int id,int assessmentStatus);
    void updateAssessmentById(int id,int screenFluency,int screenSharpness,int screenColor,int gameDelay,int gameLag);
    void updateTimestampById(int id, String timestamp);

    int deleteGameTaskByID(int id);


    int queryStatusByID(int id);
    int queryAssessmentStatusByID(int id);


}
