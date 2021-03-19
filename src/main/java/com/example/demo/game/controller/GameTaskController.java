package com.example.demo.game.controller;

import com.example.demo.game.entity.GameESNetworkTester;
import com.example.demo.game.entity.GameEncodingScheme;
import com.example.demo.game.entity.GameTask;
import com.example.demo.game.service.GameEncodingSchemeService;
import com.example.demo.game.service.GameTaskService;
import com.example.demo.service.TesterService;
import com.example.demo.service.VideoNetworkService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/game")
public class GameTaskController {
    @Resource
    private GameTaskService gameTaskService;
    @Resource
    private VideoNetworkService videoNetworkService;
    @Resource
    private TesterService testerService;
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
    @Transactional
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
                    videoNetworkService.updateIsUsedByID(idVideoNetwork,1);
                    testerService.updateIsUsedByUsername(username,1);
                    System.out.println(gameTask);
                }
            }
        }
        return "ok";
    }

    /**
     * 更新游戏任务
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
    @Transactional
    public String deleteGameTask(int id){
        System.out.println("******删除游戏任务****");
        GameTask gameTask = gameTaskService.queryGameTaskById(id);
        gameTaskService.deleteGameTaskByID(id);
        int idGameEncodingScheme = gameTask.getIdgameEncodingScheme();
        gameEncodingSchemeService.updateIsUsedByID(idGameEncodingScheme,-1);
        videoNetworkService.updateIsUsedByID(gameTask.getIdVideoNetwork(),-1);
        testerService.updateIsUsedByUsername(gameTask.getUsername(),-1);
        return "ok";
    }


    @GetMapping("/adminStop")
    public Boolean adminStop(int id){

        return false;
    }

    /**
     * 将当前游戏任务的status置为1
     *
     * @param id 游戏任务的id
     * @return ok
     */
    @GetMapping("/adminPlay")
    @Transactional
    public Map<String,Object> gameTaskAdminPlay(int id){
        Map<String,Object> map = new HashMap<>();
        if(playingGameTaskID!=-1){
            map.put("status",404);
            map.put("message","目前已经有正在执行任务！");
        }else{
            //将当前游戏任务的status置为1
            gameTaskService.updateStatusById(id,1);
            playingGameTaskID = id;
            map.put("status",200);
            map.put("message","该任务开始执行了！");
        }

        return map;
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
        if(gameTask==null ){
            System.out.println("当前没有正在执行的任务");
            map.put("status",404);
            map.put("message","当前没有正在执行的任务");
        }else{
            if(username.equals(gameTask.getUsername())){
                System.out.println(username+"有测试任务");
                gameTaskService.updateAssessmentStatusById(gameTask.getIdgameTask(),1);
                map.put("status",200);
                map.put("message","当前有游戏任务");
            }else{
                System.out.println(username+"没有测试任务");
                map.put("status",404);
                map.put("message","该用户当前没有测试任务");

            }
        }
        return map;
    }

    /**
     * 更新游戏任务的评价
     * 将游戏评价状态置为2
     * @return ok
     */
    @GetMapping("/assessment")
    @Transactional
    public String updateAssessment(int screenFluency,int screenSharpness,int screenColor,int gameDelay,int gameLag){
        GameTask gameTask = gameTaskService.queryGameTaskByStatus(1);
        if(playingGameTaskID!=-1 ||  gameTask!=null){
            gameTaskService.updateAssessmentById(gameTask.getIdgameTask(),screenFluency,screenSharpness,screenColor,gameDelay,gameLag);
            gameTaskService.updateAssessmentStatusById(gameTask.getIdgameTask(),2);
            gameTaskService.updateStatusById(gameTask.getIdgameTask(),2);
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
     * 追加时间戳
     * @param timestamp 时间
     * @return ok
     */
    @GetMapping("/updateTimestamp")
    public String updateTime(String timestamp){
        System.out.println(playingGameTaskID);
        gameTaskService.updateTimestampById(playingGameTaskID,timestamp);
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

    @GetMapping("/startUpGame")
    public String startUpGame(String gameName)  throws IOException{

            String  command = "cmd.exe /c start D:/work/program/5gvideowebcode/gametest/gametest/"+gameName+".exe";
            Runtime.getRuntime().exec(command);


        return "ok";
    }
    @GetMapping("/endGame")
    public String endGame(String gameName) throws IOException {
        String commmand = "tskill "+ gameName;
        Runtime.getRuntime().exec(commmand);
        return "ok";
    }
}
