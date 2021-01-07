package com.example.demo.controller;

import com.example.demo.entity.Tester;
import com.example.demo.entity.Video;
import com.example.demo.mapper.VideoMapper;
import com.example.demo.service.TesterService;
import com.example.demo.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class VideoController {
    @Resource
    private VideoService videoService;

    @GetMapping("/getVideoList")
    public List<Video> queryVideoList(){
        List<Video> videoList = videoService.queryVideoList();
        return videoList;
    }

    @GetMapping("/insertVideo")
    public int  addVideo(@RequestBody final Video video){
        return videoService.addVideo(video);
    }

    /**
     * 根据id删除视频
     * 经测试，可用
     * 无需token
     * @param id 用户信息
     * @return 是否删除成功，1成功，0失败
     */
    @GetMapping("/deleteVideoByID")
    public int deleteByID(int id){
        return videoService.deleteVideoByID(id);
    }
    @GetMapping("/deleteVideoByUsername")
    public int deleteByID(String videoname){
        return videoService.deleteVideoByVideoname(videoname);
    }

    /**
     * 更新用户信息，需提供username
     * 经测试，可用
     * 无需token
     * @param tester 用户信息
     * @return 是否更新成功，1成功，0失败
     */
//    @PostMapping("updateTester")
//    public int updateTester(@RequestBody final Tester tester){
//        return testerService.updateTester(tester);
//    }




}

