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
import java.io.File;
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
    private  final String vodPath = "http://10.112.79.206:8080/hlsvod/"; //点播拉流地址
    private  final String vodServerPath = "/usr/local/nginx/html/hlsvod/"; //本机nginx地址
    private  final String liveURL = "http://10.112.79.206:8080/hls/home1.m3u8"; //直播拉流地址
    private int liveIdMerge = -1;  //目前正在直播的流的idmerge  如果没有直播，则为-1
    @Autowired
    private RestTemplate restTemplate;
    private final String mediaServer = "http://192.168.10.119:8085" ;//流媒体服务器url
    /**
     * 增加任务列表
     * @param videoTaskNetworkTester
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
                }
                //将编码方案的isUsed+1
                encodingSchemeService.updateIsUsedByID(idES,1);
            }
            //将网络编码方案的isUsed + 1
            videoNetworkService.updateIsUsedByID(idVideoNetwork,1);
        }
        System.out.println("×××××××××××任务创建完成××××**×××");
    }

    /**
     * 返回所有任务列表
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
            int vodStatus = 0;
            int taskStatus2 = 0;
            int taskStatus1 = 0;
            int taskStatus0 = 0;
            for(Integer idvt:idvts){
                int vodStatu = videoTaskService.queryVodStatusByID(idvt);
                if(vodStatu==2){
                    vodStatus++;
                }
                System.out.println("**************************");
                List<Integer> assessmentStatus = videoTask_testerService.queryAssessmentStatusByIdvt(idvt);
                System.out.println("idmerge为"+idmerge+" 任务id为 "+idvt+" assessmentstatus"+assessmentStatus+"该视频对应测试人员数目为 "+assessmentStatus.size());
                for(Integer as:assessmentStatus){
                    if(as==2){
                        taskStatus2++;
                    }else if(as==1){
                        taskStatus1++;
                    }else{
                        taskStatus0++;
                    }
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
            map.put("vodStatus",vodStatus); //转好的个数
            System.out.println("转好的视频个数"+vodStatus);
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
     * 通过用户名查询当前测试员的点播任务
     * @param username 测试员用户名
     * @return id:videotasktester vodurl:
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
            System.out.println("idvideotask "+idvt);
            VideoTask videoTask = videoTaskService.queryTaskByID(idvt);

            int vodStatus = videoTask.getVodStatus();
            if(vodStatus==2 && videoTask.getVodURL()!=null){
                System.out.println("点播url"+videoTask.getVodURL());
                map.put("id",videoTask_tester.getIdVideoTask_Tester());
                map.put("status",200);
                map.put("message",videoTask.getVodURL());
            }
            ret.add(map);
        }
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
        for(Integer idvt:idvts){
            //删除videotasktest中的数据
            videoTask_testerService.deleteByIdVideoTask(idvt);
            //查询到用户名
            List<String> usernames = videoTask_testerService.queryUsernameByIdvt(idvt);
            for(String username:usernames){
                testerService.updateIsUsedByUsername(username,-1);
            }
            //删除videotask
            videoTaskService.deleteByID(idvt);
            VideoTask videoTask = videoTaskService.queryTaskByID(idvt);
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
        //videoTaskService.updateTaskStatusById(idvt,2);
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
    public String adminPlay(int idMerge) throws ExecutionException, InterruptedException {
        //通过idmerge查询idvideotask
        List<Integer> idvts = videoTaskService.queryIdTaskByIdMerge(idMerge);
        for(Integer idvt:idvts){
            //将任务状态变为1
            videoTaskService.updateTaskStatusById(idvt,1);
            System.out.println("idvt是： "+idvt);
            VideoTask videoTask = videoTaskService.queryTaskByID(idvt);
            //使网络配置生效
            int networkID = videoTask.getIdVideoNetwork();
            videoNetworkService.setNetworkConfig(networkID);
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
                List<String> usernames = videoTask_testerService.queryUsernameByIdvt(idvt);
                for(String username:usernames){
                    videoTask_testerService.updateLiveStatusByUsername(username,2);
                }
                //将相关用户的直播状态变为1
                List<Integer> ids = videoTask_testerService.queryIDByIdvt(idvt);
                for(Integer id:ids){
                    videoTask_testerService.updateLiveStatusByID(id,1);
                    videoTask_testerService.updateAssessmentStatusByID(id,1);
                }
                //退流
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
            }
        }
        return "ok";
    }
    @GetMapping("setLiveIdMerge")
    public void setLiveIdMerge(int idMerge){
        System.out.println("当前直播流 "+idMerge);
        liveIdMerge = idMerge;
    }
    @GetMapping("/queryLiveIdMerge")
    public int queryLiveIdMerge(){
        System.out.println("当前有无直播流"+liveIdMerge);
        return liveIdMerge;
    }
    @GetMapping("/endCameraLive")
    public String endCameraLive(int idMerge){
        liveIdMerge = -1;
        //通过idmerge查询idvideotask
        List<Integer> idvts = videoTaskService.queryIdTaskByIdMerge(idMerge);
        for(Integer idvt:idvts) {
            //将相关用户的直播状态变为2
            List<Integer> ids = videoTask_testerService.queryIDByIdvt(idvt);
            for (Integer id : ids) {
                videoTask_testerService.updateLiveStatusByID(id, 2);
                System.out.println("修改评价状态");
                videoTask_testerService.updateAssessmentStatusByID(id, 2);
            }
        }
        System.out.println("××××××××正在结束直播×××××××");
        endCameraLiveMediaServer();
        return "ok";
    }


    /**
     * 点播转码，获取编码参数、视频源，将编码状态进行转换
     * 获取到源视频信息和视频配置方案后，将这些信息发送给流媒体服务器后端，令其开始转码
     * @param id idmerge 前端视频方案的编码id
     */
    @GetMapping("/vodEncode")
    public String vodEncode(int id) throws ExecutionException, InterruptedException {
        System.out.println(id);
        //通过idmerge查询idvideotask
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
            if(!"404".equals(isFileExist)){
                System.out.println("不用转码文件已经存在了");
                videoTaskService.updateVodStatusById(idvt,2);
                //将admin vodurl写入数据库
                videoTaskService.updateAdminVodURLById(idvt,vodPath+isFileExist);
            }else{
                System.out.println("还没开始转码");
            }
        }
        return "ok";
    }

    /**
     * 文件转码完成
     */
    @GetMapping("/vodEncodeEnd")
    public void vodEncodeEnd(int idvt,String outputName) {
        videoTaskService.updateVodStatusById(idvt,2);
        //将admin vodurl写入数据库
        videoTaskService.updateAdminVodURLById(idvt,vodPath+outputName);
    }
    /**
     * 发送idvt和编码信息和视频信息 令流媒体服务器点播转码
     * 404 或者 outputname
     */
    private String vodEncodeMediaServer(EncodingSchemeVideo encodingSchemeVideo){
        String url = mediaServer+"/vodEncode";
        ResponseEntity<String> entity = restTemplate.postForEntity(url, encodingSchemeVideo, String.class);
        System.out.println(entity.getBody());
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
}
