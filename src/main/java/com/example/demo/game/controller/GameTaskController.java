package com.example.demo.game.controller;

import com.example.demo.entity.VideoNetwork;
import com.example.demo.entity.VideoTask;
import com.example.demo.game.entity.Game;
import com.example.demo.game.entity.GameESNetworkTester;
import com.example.demo.game.entity.GameEncodingScheme;
import com.example.demo.game.entity.GameTask;
import com.example.demo.game.service.GameEncodingSchemeService;
import com.example.demo.game.service.GameService;
import com.example.demo.game.service.GameTaskService;
import com.example.demo.service.TesterService;
import com.example.demo.service.VideoNetworkService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    private GameService gameService;
    @Resource
    private VideoNetworkService videoNetworkService;
    @Resource
    private TesterService testerService;
    @Resource
    private GameEncodingSchemeService gameEncodingSchemeService;
    private int playingGameTaskID = -1; //当前正在执行的游戏任务id
    private int gameTaskId = -1; //should play
    private static int currentNetworkId = -1; //当前实际的网络配置id
    private static int taskNetworkId = -1; //当前应该执行的任务的网络配置id
    public static void setCurrentNetworkId(int id){
        currentNetworkId = id;
    }
    @Autowired
    private RestTemplate restTemplate;
    private final String gameServerUrl = "http://192.168.3.11:8085";
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
    @GetMapping("/getCurrentTask")
    public int queryCurrentTask(){
        GameTask gameTask = gameTaskService.queryGameTaskByStatus(1);
        if(gameTask==null ){
            return -1;
        }else{
            playingGameTaskID = gameTask.getIdgameTask();
            return gameTask.getIdgameTask();
        }
    }

    @GetMapping("/resetNetwork")
    public String restNetwork(){
        videoNetworkService.setNetworkConfig(-1);
        currentNetworkId = -1;
        return "ok";
    }

    /**
     * 获取当前实际的网络设置
     * @return 当前的网络状态,如果为设置，就返回一个全是0的网络对象
     */
    @GetMapping("/getCurrentNetwork")
    public VideoNetwork queryCurrentNetwork(){
        if(currentNetworkId==-1) return new VideoNetwork(0,0+"",0+"",0+"",0+"",0);
        System.out.println(currentNetworkId);
        return videoNetworkService.query(currentNetworkId);
    }

    /**
     * 重新将网络设置成当前任务的网络配置
     * @return 成功设置返回true，失败返回false
     */
    @GetMapping("/setCurrentNetwork")
    public Boolean setCurrentNetwork(){
        int temp = currentNetworkId;
        System.out.println("********************查看当前有没有执行的任务**************");
        //通过assessment 状态查找任务
        GameTask gameTask = gameTaskService.queryGameTaskByStatus(1);
        if(gameTask==null ){
            System.out.println("当前没有正在执行的任务");
            playingGameTaskID = -1;
            taskNetworkId = -1;
            System.out.println("***************当前没有执行的任务********************");
        }else{
            taskNetworkId = gameTask.getIdVideoNetwork();
            //使网络配置生效
            try {
                videoNetworkService.setNetworkConfig(taskNetworkId);
                currentNetworkId = taskNetworkId;
            } catch (RuntimeException e) {
                System.out.println("到WANem的连接异常");
                currentNetworkId = temp;
                //e.printStackTrace();
                return false;
            }
        }

        return true;
    }



    /**
     * 游戏任务
     * 停止游戏，
     * 停止网络，
     * 停止更新评价状态为2，
     * 更新正在执行的网络状态、目前网络状态、目前游戏任务id
     * @param id
     * @return
     */
    @GetMapping("/adminStop")
    public Boolean adminStop(int id) {
        GameTask gameTask = gameTaskService.queryGameTaskById(id);
        GameEncodingScheme gameEncodingScheme = gameEncodingSchemeService.queryEncodingSchemeById(gameTask.getIdgameEncodingScheme());
        Game game = gameService.queryGameByID(gameEncodingScheme.getIdGame());
        try{
            endGame(game.getGameName(),gameEncodingScheme.getWidth()+"");
        }catch (Exception e){
            System.out.println("无法结束远程游戏");
            return  false;
        }
        playingGameTaskID = -1;
        //使网络配置生效
        int temp = currentNetworkId;
        try {
            videoNetworkService.setNetworkConfig(-1);
            taskNetworkId = -1;
            currentNetworkId= -1;
        } catch (RuntimeException e) {
            System.out.println("到WANem的连接异常");
            currentNetworkId = temp;
            //e.printStackTrace();
            return false;
        }
        // update assessment statu
        gameTaskService.updateStatusById(id,2);
        gameTaskService.updateAssessmentStatusById(id,2);
        return true;
    }

    /**
     * 启动游戏任务
     * 启动网络 更新正在执行的网络状态i 目前网络状态
     * 更新评价状态
     * @param id 游戏任务的id
     * @return ok
     */
    @GetMapping("/adminPlay")
    @Transactional
    public Map<String,Object> gameTaskAdminPlay(int id)  {
        Map<String,Object> map = new HashMap<>();

            //将当前游戏任务的status置为1
            gameTaskService.updateStatusById(id,1);
            GameEncodingScheme gameEncodingScheme = gameEncodingSchemeService.queryEncodingSchemeById(gameTaskService.queryGameTaskById(id).getIdgameEncodingScheme()) ;
            int gameId = gameEncodingScheme.getIdGame();
            Game game = gameService.queryGameByID(gameId);
            try{
                startUpGame(game.getGamePath(),game.getGameName(),gameEncodingScheme.getWidth()+"");
                playingGameTaskID = id;
            }catch (Exception e){
                System.out.println("无法正常启动游戏");
                map.put("status",404);
                map.put("message","无法正常启动游戏");
            }
            int networkID= gameTaskService.queryGameTaskById(id).getIdVideoNetwork();
            int temp = currentNetworkId;
            //使网络配置生效
            try {
                videoNetworkService.setNetworkConfig(networkID);
                currentNetworkId = networkID;
                taskNetworkId = networkID;
            } catch (RuntimeException e) {
                System.out.println("到WANem的连接异常");
                currentNetworkId = temp;
                taskNetworkId = -1;
                e.printStackTrace();
                map.put("status", 408);
                map.put("message", "网络模拟器服务连接超时");
                //return map;
            }
            map.put("status",200);
            map.put("message","该任务开始执行了！");

        return map;
    }
    @GetMapping("/stopGame")
    public Boolean StopGame()  {
        //通过assessment 状态查找任务
        GameTask gameTask = gameTaskService.queryGameTaskByStatus(1);
        if(gameTask!=null){
            GameEncodingScheme gameEncodingScheme = gameEncodingSchemeService.queryEncodingSchemeById(gameTask.getIdgameEncodingScheme());
            Game game = gameService.queryGameByID(gameEncodingScheme.getIdGame());
            try{
                endGame(game.getGameName(),gameEncodingScheme.getWidth()+"");
            }catch (Exception e){
                return false;
            }
        }
        return true;
    }
    @GetMapping("/startGameAgain")
    public Boolean startGameAgain()  {
        //通过assessment 状态查找任务
        GameTask gameTask = gameTaskService.queryGameTaskByStatus(1);
        if(gameTask!=null){
            GameEncodingScheme gameEncodingScheme = gameEncodingSchemeService.queryEncodingSchemeById(gameTask.getIdgameEncodingScheme());
            Game game = gameService.queryGameByID(gameEncodingScheme.getIdGame());

            try{
                startUpGame(game.getGamePath(),game.getGameName(),gameEncodingScheme.getWidth()+"");
            }catch (Exception e){
                return false;
            }
        }
        return true;
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
        if(playingGameTaskID!=-1 &&  gameTask!=null){
            gameTaskService.updateAssessmentById(gameTask.getIdgameTask(),screenFluency,screenSharpness,screenColor,gameDelay,gameLag);
            gameTaskService.updateAssessmentStatusById(gameTask.getIdgameTask(),2);
            gameTaskService.updateStatusById(gameTask.getIdgameTask(),2);
            playingGameTaskID=-1;

            GameEncodingScheme gameEncodingScheme = gameEncodingSchemeService.queryEncodingSchemeById(gameTask.getIdgameEncodingScheme());
            Game game = gameService.queryGameByID(gameEncodingScheme.getIdGame());
            try{
                endGame(game.getGameName(),gameEncodingScheme.getWidth()+"");
            }catch (Exception e){
            }
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
        GameTask currentGameTask = gameTaskService.queryGameTaskByStatus(1);
        if(currentGameTask==null){
            return "false";
        }
        playingGameTaskID = currentGameTask.getIdgameTask();
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

    /**
     * control cloud game start
     * @return
     * @throws IOException
     */
    private String startUpGame(String gamePath,String gameName,String gameResolution)  throws IOException{
        String url = gameServerUrl+"/startUpGame"+"?gamePath={1}&gameName={2}&gameResolution={3}";
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class,gamePath,gameName,gameResolution);
        return entity.getBody();
    }

    /**
     * control cloud game stop
     * @param gameName
     * @return
     * @throws IOException
     */
    private String endGame(String gameName,String gameResolution) throws IOException {
        String url = gameServerUrl+"/endGame"+"?gameName={1}&gameResolution={2}";
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class,gameName,gameResolution);
        return entity.getBody();
    }
}
