package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import com.example.demo.utils.ffmpeg.LivePushCamera;
import com.example.demo.utils.ffmpeg.LivePushFile;
import com.example.demo.utils.ffmpeg.VodEncode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@RestController
@RequestMapping(value = "/videoTask")
public class VideoTaskController {
    @Resource
    private VideoTaskService videoTaskService;
    @Resource
    private Video_ESService video_esService;
    @Resource
    private VideoTask_TesterService videoTask_testerService;
    @Resource
    private EncodingSchemeService encodingSchemeService;
    @Resource
    private  VideoService videoService;
    @Resource
    private VideoNetworkService videoNetworkService;
    @Resource TesterService testerService;
    private  final String vodPath = "http://10.112.79.206:8080/hls_vod/"; //点播拉流地址
    private  final String vodServerPath = "/usr/local/nginx/html/hls_vod/"; //本机nginx地址
    private  final String liveURL = "http://10.112.79.206:8080/hls/home1.m3u8"; //直播拉流地址
    private final String mediaServer = "http://10.112.79.206:8085" ;//流媒体服务器url
    private int liveIdMerge = -1;  //目前实际正在直播的流的idmerge  如果没有直播，则为-1
    private static int currentNetworkId = -1; //当前实际的网络配置id
    private static int taskNetworkId = -1; //当前应该执行的任务的网络配置id
    private static int taskLiveIdMerge = -1; //当前应该执行的直播任务idmerge
    private static int currentIdMerge = -1; //当前正在执行的idmerge
    public static Boolean existM3U8 = false; //当前是否有m3u8文件
    public static void setCurrentNetworkId(int id){
        currentNetworkId = id;
    }
    public static int getCurrentNetworkId(){
        return currentNetworkId;
    }
    public static void setTaskNetworkId(int id){
        taskNetworkId = id;
    }
    public static int getTaskNetworkId(){
        return taskNetworkId;
    }
    public static void setTaskLiveIdMerge(int id){
        taskLiveIdMerge = id;
    }
    public static int getTaskLiveIdMerge(){
        return taskLiveIdMerge;
    }
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 增加任务
     */
    @PostMapping("/insert")
    @Transactional
    public void addVideoTask(@RequestBody VideoTaskNetworkTester videoTaskNetworkTester){
        System.out.println("×××××××××××开始新建任务×××××××");
        List<Integer> idESs = videoTaskNetworkTester.getIdESs(); //编码方案的id数组
        List<Integer> idVideoNetworks = videoTaskNetworkTester.getIdVideoNetworks(); //网络方案的id数组
        List<String> usernames =videoTaskNetworkTester.getUsernames();//测试人员的用户名数组
        String streamType = videoTaskNetworkTester.getStreamType(); //任务类型：直播、点播

        int idmergeNum=videoTaskService.countIdMerge();
        System.out.println("目前idmerge的个数"+idmergeNum);
        int idmerge=0;
        if(idmergeNum!=0){ idmerge=videoTaskService.getMaxIdMerge();}
        System.out.println("目前idmerge的最大id是 "+idmerge);

        for(Integer idVideoNetwork:idVideoNetworks){
            System.out.println("网络编码方案的id "+ idVideoNetwork);
            //根据编码方案id查询到VideoES的id
            for(Integer idES:idESs){
                idmerge++;
                List<Integer> idVideoESs = video_esService.queryIDByIdes(idES);
                for(Integer idVideoES:idVideoESs){
                    int idvt = videoTaskService.insert(new VideoTask(idVideoES,idVideoNetwork,streamType,0,0,idmerge));
                    System.out.println("videotask新增成功");
                    for(String username:usernames){
                        videoTask_testerService.insert(new VideoTask_Tester(username,idvt,0,0));
                        //将用户的isUsed+1
                        testerService.updateIsUsedByUsername(username,1);
                    }
                    //将videoes的isUsed+1
                    video_esService.updateIsUsedByID(idVideoES,1);
                    //将编码方案的isUsed+1
                    encodingSchemeService.updateIsUsedByID(idES,1);
                    //将网络编码方案的isUsed + 1
                    videoNetworkService.updateIsUsedByID(idVideoNetwork,1);
                }

            }

        }
        System.out.println("×××××××××××任务创建完成××××**×××");
    }

    /**
     * 返回所有任务
     * @return idmerge idvideoES idnetwork streamType vodStatus taskStatus
     */
    @GetMapping("/getList")
    public List<Map<String,Object>> queryVideoList(){
        System.out.println("***********开始获取任务列表×××××××××××");
        List<Map<String,Object>> ret = new ArrayList<>();
        List<Integer> distinctIdMerge = videoTaskService.getDistinctIdMerge();
        //List<Integer> distinctIdVideoTasks = videoTask_testerService.getDistinctIdVideoTask();
        for(Integer idmerge:distinctIdMerge){
            Map<String,Object> map = new HashMap<String, Object>();
            //通过idmerge查询用户名数组
            List<Integer> idvts = videoTaskService.queryIdTaskByIdMerge(idmerge);
            int vodStatus0 = 0,vodStatus1=0,vodStatus2=0;
            int taskStatus2 = 0,taskStatus1 = 0,taskStatus0 = 0;
            for(Integer idvt:idvts){
                int vodStatu = videoTaskService.queryVodStatusByID(idvt);
                if(vodStatu==0) vodStatus0++;
                else if(vodStatu==1) vodStatus1++;
                else vodStatus2++;
                System.out.println("**************************");
                List<Integer> assessmentStatus = videoTask_testerService.queryAssessmentStatusByIdvt(idvt);
                System.out.println("idmerge为"+idmerge+" 任务id为 "+idvt+" assessmentstatus"+assessmentStatus+"该视频对应测试人员数目为 "+assessmentStatus.size());
                for(Integer as:assessmentStatus){
                    if(as==2) taskStatus2++;
                    else if(as==1) taskStatus1++;
                    else taskStatus0++;
                }
            }

            int idvt = idvts.get(0);
            List<String> usernames = videoTask_testerService.queryUsernameByIdvt(idvt);
            map.put("usernames",usernames);
            map.put("idMerge",idmerge);
            VideoTask videoTask = videoTaskService.queryTaskByID(idvt);
            int idVideoES = videoTask.getIdVideoES();
            int idES = video_esService.queryById(idVideoES).getIdEncodingScheme();
            map.put("idES",idES);
            map.put("idVideoNetwork",videoTask.getIdVideoNetwork());
            map.put("streamType",videoTask.getStreamType());
            if(vodStatus0==idvts.size()) map.put("vodStatus",0); //没开始转码
            else if(vodStatus2==idvts.size()) map.put("vodStatus",2); //转码完毕
            else map.put("vodStatus",1); //正在转码
            System.out.println("idmerge为"+idmerge+" 视频个数为"+idvts.size()+"评价完成进度"+taskStatus0+" "+taskStatus1+" "+taskStatus2);
            map.put("taskStatus",new int[]{taskStatus0,taskStatus1,taskStatus2});
            map.put("videoNumber",idvts.size());
            ret.add(map);
        }
        System.out.println("获取的任务列表"+ret);
        System.out.println("***********获取任务列表结束×××××××××××");
        return ret;
    }
    /**
     * 删除任务
     * 需要把网络配置的、人员的、编码方案的isUsed -1
     * 根据idmerge
     */
    @GetMapping("/deleteTask")
    @Transactional
    public void deleteTask(int idMerge){
        List<Integer> idvts = videoTaskService.queryIdTaskByIdMerge(idMerge);
        //System.out.println("需要删除的videotask任务 id"+idvts);
        for(Integer idvt:idvts){
            //查询到用户名
            List<String> usernames = videoTask_testerService.queryUsernameByIdvt(idvt);
            //删除videotasktest中的数据
            videoTask_testerService.deleteByIdVideoTask(idvt);

            for(String username:usernames){
                testerService.updateIsUsedByUsername(username,-1);
            }
            //删除videotask
            VideoTask videoTask = videoTaskService.queryTaskByID(idvt);
            videoTaskService.deleteByID(idvt);
            //videoes
            int idVideoES = videoTask.getIdVideoES();
            video_esService.updateIsUsedByID(idVideoES,-1);
            //查询编码方案id
            int idES = video_esService.queryById(idVideoES).getIdEncodingScheme();
            encodingSchemeService.updateIsUsedByID(idES,-1);
            //网络编码方案
            int idVideoNetwork = videoTask.getIdVideoNetwork();
            videoNetworkService.updateIsUsedByID(idVideoNetwork,-1);
        }

    }

    /**
     * 获取当前实际的网络设置
     * @return 当前的网络状态,如果为设置，就返回一个全是0的网络对象
     */
    @GetMapping("/getCurrentNetwork")
    public VideoNetwork queryCurrentNetwork(){
        if(currentNetworkId==-1) return new VideoNetwork(0,0+"",0+"",0+"",0+"",0);
        return videoNetworkService.query(currentNetworkId);
    }

    /**
     * 重新将网络设置成当前任务的网络配置
     * @return 成功设置返回true，失败返回false
     */
    @GetMapping("/setCurrentNetwork")
    public Boolean setCurrentNetwork(){
        int temp = currentNetworkId;
        //使网络配置生效
        try {
            setCurrentNetworkId(taskNetworkId);
            videoNetworkService.setNetworkConfig(taskNetworkId);
        } catch (RuntimeException e) {
            System.out.println("到WANem的连接异常");
            setCurrentNetworkId(temp);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 通过用户名查询当前测试员的点播任务
     * @param username 测试员用户名
     * @return id:videotasktester vodurl、封面地址
     */
    @GetMapping("/getVodTaskByUsername")
    public List<Map<String,Object>> queryVodTaskList(String username){
        //根据用户名查询videoTask_tester
        List<VideoTask_Tester> videoTask_testers = videoTask_testerService.getVideoTaskByUsername(username);
        List<Map<String,Object> > ret = new ArrayList<Map<String,Object>>();
        for(VideoTask_Tester videoTask_tester:videoTask_testers){
            if(videoTask_tester.getAssessmentStatus()!=1) continue; //评价状态为1才是需要执行的任务，状态为2的表示评价结束
            Map<String,Object> map = new HashMap<>();
            int idvt = videoTask_tester.getIdVideoTask();
            VideoTask videoTask = videoTaskService.queryTaskByID(idvt);
            int vodStatus = videoTask.getVodStatus();
            if(vodStatus==2 && videoTask.getVodURL()!=null){
                System.out.println("点播url"+videoTask.getVodURL());
                map.put("id",videoTask_tester.getIdVideoTask_Tester());
                map.put("status",200);
                map.put("vodUrl",videoTask.getVodURL());
                map.put("coverUrl",videoTask.getCoverPath());
                ret.add(map);
            }
        }
        return ret;
    }


    /**
     * 更新用户评价
     * @param map 包含任务id、评价。。。
     */
    @RequestMapping("/assessment")
    public void updateAssessmentByID(@RequestBody Map<String,Integer> map){
        int id = map.get("id");
        System.out.println("assessmentid "+id);
        videoTask_testerService.updateAssessmentByID(id,map.get("sharpness"),map.get("fluency"),map.get("color"));
        int idvt = videoTask_testerService.queryIdVideoTaskByID(id);
        System.out.println("idvt"+idvt);
        String streamType = videoTaskService.queryStreamTypeByID(idvt);
        if("live".equals(streamType)){
            //将用户的直播状态变为2
            videoTask_testerService.updateLiveStatusByID(id,2);
        }
        //将任务评价状态变为2
        videoTask_testerService.updateAssessmentStatusByID(id,2);
        currentIdMerge=-1;
        //videoTaskService.updateTaskStatusById(idvt,2);
    }

    /**
     * 结束本条任务
     * 网络配置重置，
     * 评价状态置为2
     * 点播url清空
     * 结束直播
     * 直播status置为2
     * 直播url清空
     * @return ok
     */
    @GetMapping("/adminStop")
    @Transactional
    public String adminStop(int idMerge){
        //使网络配置重置
        try {
            videoNetworkService.setNetworkConfig(-1);
        } catch (RuntimeException e) {
            System.out.println("到WANem的连接异常");
            //e.printStackTrace();
            //return map;
        }
        //通过idmerge查询idvideotask
        List<Integer> idvts = videoTaskService.queryIdTaskByIdMerge(idMerge);
        setCurrentNetworkId(-1);
        setTaskNetworkId(-1);
        currentIdMerge = -1;
        endCameraLiveMediaServer();
        existM3U8 = false;
        for(Integer idvt:idvts){
            System.out.println("idvt是： "+idvt);
            VideoTask videoTask = videoTaskService.queryTaskByID(idvt);
            //点播
            if ("vod".equals(videoTask.getStreamType())) {
                //得到id
                List<Integer> ids = videoTask_testerService.queryIDByIdvt(idvt);
                for(Integer id:ids){
                    videoTask_testerService.updateAssessmentStatusByID(id,2);
                }
                videoTaskService.updateVodURLById(idvt,null);
            }else { //直播
                liveIdMerge = -1;
                videoTaskService.updateLiveURLById(idvt,null);
                //将相关用户的直播状态变为2
                List<Integer> ids = videoTask_testerService.queryIDByIdvt(idvt);
                for(Integer id:ids){
                    videoTask_testerService.updateLiveStatusByID(id,2);
                    videoTask_testerService.updateAssessmentStatusByID(id,2);
                }
            }
        }
        return "ok";
    }



    /**
     * 管理员播放
     * 将点播url和直播url分配给相关用户
     * 对于直播，需要进行推流
     * 设置网络生效
     * @param idMerge idmerge
     */
    @GetMapping("/adminPlay")
    @Transactional
    public Map<String,Object> adminPlay(int idMerge) throws ExecutionException, InterruptedException {
        Map<String,Object> map = new HashMap<>();
        //通过idmerge查询idvideotask
        List<Integer> idvts = videoTaskService.queryIdTaskByIdMerge(idMerge);
        if(idvts!=null) {
            VideoTask videoTaskTemp = videoTaskService.queryTaskByID(idvts.get(0));
            //使网络配置生效
            try {
                int networkID = videoTaskTemp.getIdVideoNetwork();
                setCurrentNetworkId(networkID);
                setTaskNetworkId(networkID);
                videoNetworkService.setNetworkConfig(networkID);
            } catch (RuntimeException e) {
                System.out.println("到WANem的连接异常");
                setCurrentNetworkId(-1);
                setTaskNetworkId(-1);
                e.printStackTrace();
                map.put("status", 408);
                map.put("message", "网络模拟器服务连接超时");
                //return map;
            }
        }
        currentIdMerge = idMerge;
        Runnable tempTask = new Runnable() {
            @Override
            public void run() {
                for(Integer idvt:idvts){
                    //将任务状态变为1
                    //videoTaskService.updateTaskStatusById(idvt,1);
                    System.out.println("idvt是： "+idvt);
                    VideoTask videoTask = videoTaskService.queryTaskByID(idvt);
                    //点播
                    if ("vod".equals(videoTask.getStreamType())) {
                        //得到id
                        List<Integer> ids = videoTask_testerService.queryIDByIdvt(idvt);
                        for(Integer id:ids){
                            videoTask_testerService.updateAssessmentStatusByID(id,1);
                        }
                        videoTaskService.updateVodURLById(idvt,videoTask.getAdminVodURL());
                    }else { //直播

                        videoTaskService.updateLiveURLById(idvt,liveURL);
                        //将相关用户的以前的直播状态变为2
//                        List<String> usernames = videoTask_testerService.queryUsernameByIdvt(idvt);
//                        for(String username:usernames){
//                            videoTask_testerService.updateLiveStatusByUsername(username,2);
//                        }
                        //将相关用户的直播状态变为1
                        List<Integer> ids = videoTask_testerService.queryIDByIdvt(idvt);
                        for(Integer id:ids){
                            videoTask_testerService.updateLiveStatusByID(id,1);
                            videoTask_testerService.updateAssessmentStatusByID(id,1);
                        }
                        //推流
                        //得到编码信息和视频源信息
                        int idVideoES = videoTask.getIdVideoES();
                        Video_ES video_es = video_esService.queryById(idVideoES);
                        int ides =  video_es.getIdEncodingScheme();
                        EncodingScheme encodingScheme = encodingSchemeService.queryEncodingSchemeById(ides);
                        String videoName = video_es.getVideoName();
                        //摄像头直播
                        if("camera".equals(videoName)){
                            EncodingSchemeVideo encodingSchemeVideo=new EncodingSchemeVideo(idMerge,encodingScheme,new Video(0,"camera","/","/","/","/","/","/"));
                            pushStreamMediaServer(encodingSchemeVideo);
                        }else{
                            Video video = videoService.queryByVideoName(videoName);
                            EncodingSchemeVideo encodingSchemeVideo=new EncodingSchemeVideo(idMerge,encodingScheme,video);
                            pushStreamMediaServer(encodingSchemeVideo);
                        }
                        setTaskLiveIdMerge(idMerge);
                        liveIdMerge = idMerge;
                    }
                }

            }
        };
        Thread mythread = new Thread(tempTask);
        mythread.start();
        map.put("status",200);
        map.put("message","ok");
        return map;
    }

//    /**
//     * 设置当前直播流的id 由流媒体服务器发送而来
//     * @param idMerge idmerge
//     */
//    @GetMapping("/setLiveIdMerge")
//    public void setLiveIdMerge(int idMerge){
//        System.out.println("当前直播流 "+idMerge);
//        liveIdMerge = idMerge;
//    }

    @GetMapping("/queryForM3U8")
    public Boolean queryForM3U8(){
        return existM3U8;
    }

    /**
     * 流媒体服务器通知web m3u8已经产生了
     */
    @GetMapping("/setM3U8")
    public String setM3U8(String exist){
        System.out.println(exist);
        if("false".equals(exist)) existM3U8=false;
        else existM3U8=true;
        return "ok";
    }

    /**
     * 查询当前任务的idmerge 和当前是否是直播
     * @return [idmerge,0/1] 第二位0表示点播或没有任务，1表示直播
     */
    @GetMapping("/queryCurrentIdMerge")
    public int[] queryCurrentIdMerge(){
        System.out.println("********************查看当前有没有执行的任务**************");
        //通过assessment 状态查找任务
        int[] ret = new int[2];
        List<Integer> distinctIdvt = videoTask_testerService.queryDistinctIdvtByAssessmentStatus(1);
        System.out.println(distinctIdvt.size());
        if(distinctIdvt.size()==0){
            ret[0]=-1;
            ret[1]=0;
            currentIdMerge = -1;
            System.out.println("***************当前没有执行的任务********************");
            return ret;
        }else{
            VideoTask videoTask = videoTaskService.queryTaskByID(distinctIdvt.get(0));
            currentIdMerge = videoTask.getIdmerge();
            //点播
            if ("vod".equals(videoTask.getStreamType())) ret[1]=0;
            else ret[1] = 1;
            System.out.println(ret);
        }
        return ret;
    }

    /**
     * 重新推流。如果当前有正在推的流，就返回false
     * 否则重新推流，并返回true
     * @return 是否重新推流
     */
    @GetMapping("/pushStreamAgain")
    public Boolean pushStreamAgain(){
        if(isLiveNow().equals("true")) return false;
        else{
            //推流
            //得到编码信息和视频源信息
            List<Integer> idvts = videoTaskService.queryIdTaskByIdMerge(currentIdMerge);
            VideoTask videoTask = videoTaskService.queryTaskByID(idvts.get(0));
            int idVideoES = videoTask.getIdVideoES();
            Video_ES video_es = video_esService.queryById(idVideoES);
            int ides =  video_es.getIdEncodingScheme();
            EncodingScheme encodingScheme = encodingSchemeService.queryEncodingSchemeById(ides);
            String videoName = video_es.getVideoName();
            //摄像头直播
            if("camera".equals(videoName)){
                EncodingSchemeVideo encodingSchemeVideo=new EncodingSchemeVideo(currentIdMerge,encodingScheme,new Video(0,"camera","/","/","/","/","/","/"));
                pushStreamMediaServer(encodingSchemeVideo);
            }else{
                Video video = videoService.queryByVideoName(videoName);
                EncodingSchemeVideo encodingSchemeVideo=new EncodingSchemeVideo(currentIdMerge,encodingScheme,video);
                pushStreamMediaServer(encodingSchemeVideo);
            }
        }

        return true;
    }

    /**
     * 结束直播流
     * @param
     * @return
     */
    @GetMapping("/endLive")
    @Transactional
    public String endLiveStream(){
        //通过idmerge查询idvideotask
        liveIdMerge = -1;
        System.out.println("××××××××正在结束直播×××××××");
        endCameraLiveMediaServer();
        existM3U8 = false;
        return "ok";
    }


    /**
     * 点播转码，获取编码参数、视频源，将编码状态进行转换
     * 获取到源视频信息和视频配置方案后，将这些信息发送给流媒体服务器后端，令其开始转码
     * @param id idmerge 前端视频方案的编码id
     */
    @GetMapping("/vodEncode")
    @Transactional
    public String vodEncode(int id) throws ExecutionException, InterruptedException {
        System.out.println(id);
        //通过idmerge查询idvideotask
        Runnable tempTask = new Runnable() {
            @Override
            public void run() {
                List<Integer> idvts = videoTaskService.queryIdTaskByIdMerge(id);
                for(Integer idvt:idvts){
                    //将点播状态置为1,表示正在转码
                    videoTaskService.updateVodStatusById(idvt,1);
                    //得到编码信息和视频源信息
                    VideoTask videoTask = videoTaskService.queryTaskByID(idvt);
                    int idVideoES = videoTask.getIdVideoES();
                    Video_ES video_es = video_esService.queryById(idVideoES);
                    int ides =  video_es.getIdEncodingScheme();
                    EncodingScheme encodingScheme = encodingSchemeService.queryEncodingSchemeById(ides);
                    String videoName = video_es.getVideoName();
                    Video video = videoService.queryByVideoName(videoName);
                    //将视频信息和编码方案传递给流媒体服务器后端
                    EncodingSchemeVideo encodingSchemeVideo = new EncodingSchemeVideo(idvt,encodingScheme,video);
                    String isFileExist = vodEncodeMediaServer(encodingSchemeVideo);
                    System.out.println("result"+isFileExist);
                    String[] result = isFileExist.split(",");
                    for(String str:result){
                        System.out.println(str);
                    }
                    if(result[0].equals(isFileExist)){
                        System.out.println("不用转码文件已经存在了");
                        videoTaskService.updateVodStatusById(idvt,2);
                        //将admin vodurl写入数据库
                        videoTaskService.updateAdminVodURLById(idvt,vodPath+result[1]);
                        //将coverpath写入数据库
                        videoTaskService.updateCoverPathById(idvt,result[2]);
                    }else{
                        System.out.println("开始转码");
                        System.out.println(encodingSchemeVideo);
                        videoTaskService.updateVodStatusById(idvt,2);
                        //将admin vodurl写入数据库
                        videoTaskService.updateAdminVodURLById(idvt,vodPath+result[1]);
                        //将coverpath写入数据库
                        videoTaskService.updateCoverPathById(idvt,result[2]);
                    }
                }

            }
        };
        Thread mythread = new Thread(tempTask);
        mythread.start();
        return "ok";
    }

    /**
     * 文件转码完成
     * 弃用
     */
    @GetMapping("/vodEncodeEnd")
    public String vodEncodeEnd(int idvt,String outputName) {
        //将admin vodurl写入数据库
        videoTaskService.updateVodStatusById(idvt,2);
        videoTaskService.updateAdminVodURLById(idvt,vodPath+outputName);
        System.out.println("写入数据库");
        return "ok";
    }
    /**
     * 发送idvt和编码信息和视频信息 令流媒体服务器点播转码
     * 404 或者 outputname
     */
    private String vodEncodeMediaServer(EncodingSchemeVideo encodingSchemeVideo){
        String url = mediaServer+"/vodEncode";
        ResponseEntity<String> entity = restTemplate.postForEntity(url, encodingSchemeVideo, String.class);
        return entity.getBody();
    }
    /**
     * 发送idmerge和编码信息和视频源信息 令流媒体服务器开始直播
     * 404 或者 outputname
     */
    private String pushStreamMediaServer(EncodingSchemeVideo encodingSchemeVideo){
        String url = mediaServer+"/pushStream";
        ResponseEntity<String> entity = restTemplate.postForEntity(url, encodingSchemeVideo, String.class);
        System.out.println(entity.getBody());
        return entity.getBody();
    }
    private void endCameraLiveMediaServer(){
        String url = mediaServer+"/endCameraLive";
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
    }

    /**
     * 查询流媒体服务器是否在推流
     * @return 如果在推流返回true，否则返回false
     */
    private String isLiveNow(){
        String url = mediaServer+"/isLiveNow";
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
        return entity.getBody();
    }
}
