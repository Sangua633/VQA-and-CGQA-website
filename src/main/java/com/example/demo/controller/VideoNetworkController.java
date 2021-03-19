package com.example.demo.controller;


import com.example.demo.entity.Tester;
import com.example.demo.entity.VideoNetwork;
import com.example.demo.entity.VideoTask;
import com.example.demo.service.VideoNetworkService;
import com.example.demo.service.VideoTaskService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/videoNetwork")
public class VideoNetworkController {
    @Resource
    private VideoNetworkService videoNetworkService;

    /**
     * 返回网络配置列表
     * @return
     */
    @GetMapping("/getList")
    public List<VideoNetwork> queryList(){
        List<VideoNetwork> networkList = videoNetworkService.queryNetworkList();
        return networkList;
    }

    @PostMapping("/insert")
    public String  add(@RequestBody final VideoNetwork videoNetwork){
        videoNetworkService.addNetwork(videoNetwork);
        return "ok";
    }
    @GetMapping("/delete")
    public String delete(int id){
        videoNetworkService.deleteNetworkByID(id);
        return "ok";
    }
    @PostMapping("/update")
    public String update(@RequestBody final VideoNetwork videoNetwork){
        videoNetworkService.updateNetwork(videoNetwork);
        return "ok";
    }
    @GetMapping("/query")
    public VideoNetwork query(int id){
        return  videoNetworkService.query(id);
    }

    @GetMapping("/reset")
    public void reset(){
        videoNetworkService.setNetworkConfig(-1);
        VideoTaskController.setCurrentNetworkId(-1);
    }


}
