package com.example.demo.game.controller;

import com.example.demo.game.entity.GameESNetworkTester;
import com.example.demo.game.entity.GameEncodingScheme;
import com.example.demo.game.entity.GameTask;
import com.example.demo.game.service.GameEncodingSchemeService;
import com.example.demo.game.service.GameTaskService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/game")
public class GameTaskController {
    @Resource
    private GameTaskService gameTaskService;
    @Resource
    private GameEncodingSchemeService gameEncodingSchemeService;
    private int playingGameTaskID = -1; //当前正在执行的游戏任务id
    /**
     * 返回云游戏任务列表
     * @return
     */
    @GetMapping("/getList")
    public List<GameTask> queryGameTaskList(){
        System.out.println("*****返回云游戏任务列表******");
        List<GameTask> gameTasks = gameTaskService.queryGameTaskList();
        System.out.println(gameTasks);
        System.out.println("*****返回云游戏任务列表结束******");
        return gameTasks;

    }

    /**
     * 新增游戏任务
     * 游戏方案、网络、测试人员是数组
     * 注意要把游戏方案置isused增1
     * @param  gameESNetworkTester 游戏方案、网络、测试人员是数组
     * @return ok
     */
    @PostMapping("/insert")
    public String addGameTask(@RequestBody GameESNetworkTester gameESNetworkTester){
        System.out.println("*****新增的游戏任务*****");
        List<String> usernames = gameESNetworkTester.getUsernames();
        List<Integer> idVideoNetworks = gameESNetworkTester.getIdVideoNetworks();
        List<Integer> idgameEncodingSchemes = gameESNetworkTester.getIdgameEncodingSchemes();
        for(String username:usernames){
            for(int idVideoNetwork: idVideoNetworks){
                for(int idgameEncodingScheme:idgameEncodingSchemes){
                    GameTask gameTask = new GameTask(username,idVideoNetwork,idgameEncodingScheme);
                    gameTaskService.addGameTask(gameTask);
                    gameEncodingSchemeService.updateIsUsedByID(idgameEncodingScheme,1);
                    System.out.println(gameTask);
                }
            }
        }
        return "ok";
    }

    /**
     * 更新游戏方案
     * @param gameTask 游戏任务
     * @return
     */
    @PostMapping("/update")
    public String updateGameTask(@RequestBody GameTask gameTask){
        System.out.println("******更新游戏任务****");
        //只会更新游戏的用户名，网络id 编码方案id
        gameTaskService.updateGameTask(gameTask);
        return "ok";
    }

    /**
     * 删除游戏任务，注意将游戏方案的isused -1
     * @param id
     * @return
     */
    @GetMapping("/delete")
    public String deleteGameTask(int id){
        System.out.println("******删除游戏任务****");
        GameTask gameTask = gameTaskService.queryGameTaskById(id);
        gameTaskService.deleteGameTaskByID(id);
        int idGameEncodingScheme = gameTask.getIdgameEncodingScheme();
        gameEncodingSchemeService.updateIsUsedByID(idGameEncodingScheme,-1);
        return "ok";
    }


    /**
     * 将当前游戏任务的status置为1
     *
     * @param id 游戏任务的id
     * @return ok
     */
    @GetMapping("/adminPlay")
    public String gameTaskAdminPlay(int id){
        //将当前游戏任务的status置为1
        gameTaskService.updateStatusById(id,1);

        playingGameTaskID = id;
        return "ok";
    }

    /**
     * 判断当前用户是否有云游戏测试任务
     * 如果有，把任务assessment的status置为1 表示正在进行任务评价
     * @param username 测试人员的姓名
     * @return 含有状态码、message的map
     */
    @GetMapping("/haveGameTask")
    public Map<String,Object>  haveGameTask(String username){
        System.out.println("*****正在查询测试人员有无游戏测试任务*****");
        Map<String,Object> map = new HashMap<>();
        GameTask gameTask = gameTaskService.queryGameTaskByStatus(1);
        if(username.equals(gameTask.getUsername())){
            System.out.println(username+"有测试任务");
            gameTaskService.updateAssessmentStatusById(gameTask.getIdgameTask(),1);
            map.put("status",200);
            map.put("message","当前有游戏任务");
        }else{
            System.out.println(username+"没有测试任务");
            map.put("status",404);
            map.put("message","当前没有测试任务");

        }
        return map;
    }

    /**
     * 更新游戏任务的评价
     * 将游戏评价状态置为2
     * @param fluency 流畅度
     * @param sharpness 清晰度
     * @param color 色彩
     * @param delay 延迟
     * @return ok
     */
    @GetMapping("/assessment")
    public String updateAssessment(int fluency,int sharpness,int color,int delay){
        if(playingGameTaskID!=-1){
            gameTaskService.updateAssessmentById(playingGameTaskID,fluency,sharpness,color,delay);
            gameTaskService.updateAssessmentStatusById(playingGameTaskID,2);
            gameTaskService.updateStatusById(playingGameTaskID,2);
            playingGameTaskID=-1;
            return "ok";
        }else{
            return "no game is playing now";
        }

    }

    /**
     * 更新游戏的真实数据，
     * @param gameTask
     * @return "ok"
     */
    @PostMapping("/updateRealData")
    public String updateRealData(@RequestBody GameTask gameTask){
        System.out.println("*****更新真实数据****");
        System.out.println(playingGameTaskID);
        gameTask.setIdgameTask(playingGameTaskID);
        System.out.println(gameTask);
        gameTaskService.updateGameTaskByRealData(gameTask);
        return "ok";

    }
    /**
     * 给云游戏服务器返回正在执行的游戏任务的编码方案
     * @return 编码方案
     */
    @GetMapping("/setPara")
    public GameEncodingScheme query(){
        System.out.println("×××××××给游戏服务器返回编码参数××××××××");
        if(playingGameTaskID!=-1){
            GameTask gameTask = gameTaskService.queryGameTaskById(playingGameTaskID);
            int idgameEncodingScheme = gameTask.getIdgameEncodingScheme();
            GameEncodingScheme gameEncodingScheme = gameEncodingSchemeService.queryEncodingSchemeById(idgameEncodingScheme);
            return gameEncodingScheme;
        }
        return null;
    }
}
