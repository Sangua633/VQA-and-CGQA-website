package com.example.demo.game.service;

import com.example.demo.game.entity.GameTask;
import com.example.demo.game.mapper.GameTaskMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GameTaskService {
    @Resource
    private GameTaskMapper gameTaskMapper;
    public List<GameTask> queryGameTaskList(){
        return gameTaskMapper.queryGameTaskList();
    }
    public GameTask queryGameTaskById(int id){
        return gameTaskMapper.queryGameTaskById(id);
    }
    public GameTask queryGameTaskByStatus(int status){
        return gameTaskMapper.queryGameTaskByStatus(status);
    }

    public int addGameTask(GameTask gameTask){
        return gameTaskMapper.addGameTask(gameTask);
    }

    /**
     * 更新游戏任务，只会更新
     * @param gameTask
     * @return
     */
    public int updateGameTask(GameTask gameTask){
        return gameTaskMapper.updateGameTask(gameTask);
    }
    public int updateGameTaskByRealData(GameTask gameTask){
        return gameTaskMapper.updateGameTaskByRealData(gameTask);
    }
    public void updateStatusById(int id,int status){
        gameTaskMapper.updateStatusById(id,status);
    }
    public void updateAssessmentStatusById(int id,int assessmentStatus){
        gameTaskMapper.updateAssessmentStatusById(id,assessmentStatus);
    }
    public void updateAssessmentById(int id,int screenFluency,int screenSharpness,int screenColor,int gameDelay,int gameLag){
        gameTaskMapper.updateAssessmentById(id,screenFluency,screenSharpness,screenColor,gameDelay,gameLag);
    }
    public void updateTimestampById(int id,String timestamp){
        System.out.println(timestamp);
        gameTaskMapper.updateTimestampById(id,timestamp+",");
    }

    public int deleteGameTaskByID(int id){
        return  gameTaskMapper.deleteGameTaskByID(id);
    }


    public int queryStatusByID(int id){
        return gameTaskMapper.queryStatusByID(id);
    }
    public int queryAssessmentStatusByID(int id){
        return gameTaskMapper.queryAssessmentStatusByID(id);
    }


}
