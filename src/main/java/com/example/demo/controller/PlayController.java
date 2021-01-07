package com.example.demo.controller;


import com.example.demo.entity.EncodingScheme;
import com.example.demo.entity.Video;
import com.example.demo.entity.VideoTask;
import com.example.demo.entity.Video_ES;
import com.example.demo.service.*;
import com.example.demo.utils.ffmpeg.LivePushCamera;
import com.example.demo.utils.ffmpeg.LivePushFile;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


@RestController
@RequestMapping(value = "/play")
public class PlayController {
    @Resource
    private VideoTaskService videoTaskService;
    @Resource
    VideoTask_TesterService videoTask_testerService;

    @Resource
    private Video_ESService video_esService;

    @Resource
    private EncodingSchemeService encodingSchemeService;
    @Resource
    private VideoService videoService;



    /**
     * 根据id返回给点播资源的url，需要首先判断点播文件是否被转码
     * @param id 任务videotasktester id
     * @param response 响应
     * @throws IOException
     */
    @GetMapping("/vod")
    public void vodPlay(int id,HttpServletResponse response) throws IOException {
        //验证是否转码完成
        response.setContentType("application/json;charset=UTF-8");
        int idvt = videoTask_testerService.queryIdVideoTaskByID(id);
        System.out.println("idvt"+idvt);
        VideoTask videoTask = videoTaskService.queryTaskByID(idvt);
        int vodStatus = videoTask.getVodStatus();
        if(vodStatus==0){
            response.setStatus(404);
            response.getWriter().append("该任务还未转码，请联系管理员");
        }else if(vodStatus==1){
            response.setStatus(404);
            response.getWriter().append("该任务正在转码，请联系管理员");
        }else{
            System.out.println("点播url"+videoTask.getVodURL());

            if(videoTask.getVodURL()==null){
                response.setStatus(404);
                response.getWriter().append("管理员还未开始此任务，请联系管理员！");
            }else{
                //videoTask_testerService.updateAssessmentStatusByID(id,1);
                response.setStatus(200);
                response.getWriter().append(videoTask.getVodURL());
            }

        }
    }

    /**
     * 根据用户名和当前的livestatus判断当前用户是否有直播任务
     * @param username 用户名
     * @param
     */
    @GetMapping("/live")
    public Map<String,Object> livePlay(String username)  {
        System.out.println("直播的用户名"+username);
        Map<String,Object> map = new HashMap<>();
        List<Integer> ids = videoTask_testerService.queryIDByUsernameLive(username,1);
        if(ids.size()==0){
            map.put("id",-1);
            map.put("status",404);
            map.put("message","当前没有直播任务");
        }else if(ids.size()>=1){
            //videoTask_testerService.updateAssessmentStatusByID(ids.get(0),1);
            map.put("id",ids.get(0));
            map.put("status",200);
            map.put("message","正在直播");
        }
        return map;
    }

}
