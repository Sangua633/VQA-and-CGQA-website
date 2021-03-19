package com.example.demo.controller;

import com.example.demo.entity.EncodingScheme;
import com.example.demo.entity.Tester;
import com.example.demo.entity.VideoJointES;
import com.example.demo.entity.VideoJointES2;
import com.example.demo.service.EncodingSchemeService;
import com.example.demo.service.TesterService;
import com.example.demo.service.Video_ESService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/encoding")
public class EncodingSchemeController {
    @Resource
    private EncodingSchemeService encodingSchemeService;
    @Resource
    private Video_ESService video_esService;

    /**
     * 返回所有的编码方案
     * @return
     */
    @GetMapping("/getList")
    public List<EncodingScheme> queryList(){
        //System.out.println("×××××××开始返回编码方案列表××××××××");
        List<EncodingScheme> encodingSchemeList = encodingSchemeService.queryEncodingSchemeList();
        //System.out.println("×××××××返回编码方案列表结束××××××××");
        return encodingSchemeList;
    }
    /**
     * 返回所有的编码方案和对应的视频列表
     * @return
     */
    @GetMapping("/getVideoESList")
    public List<VideoJointES2> queryVideoESList(){
        //System.out.println("×××××××开始返回编码方案和对应的视频列表××××××××");
        List<VideoJointES2> videoJointES2s = new ArrayList<>();
        //得到编码方案
        List<EncodingScheme> encodingSchemeList = encodingSchemeService.queryEncodingSchemeList();
        for(EncodingScheme es:encodingSchemeList){
            VideoJointES2 videoJointES2 = new VideoJointES2();
            //得到ides
            int ides = es.getIdEncodingScheme();
            List<String> videonames = video_esService.queryVideoNameByIdes(ides);
            videoJointES2.setVideoNames(videonames);
            videoJointES2.setBitRate(es.getBitRate());
            videoJointES2.setResolution(es.getResolution());
            videoJointES2.setEncoding(es.getEncoding());
            videoJointES2.setFrameRate(es.getFrameRate());
            videoJointES2.setEncodingType(es.getEncodingType());
            videoJointES2.setIsUsed(es.getIsUsed());
            videoJointES2.setIdEncodingScheme(ides);
            videoJointES2s.add(videoJointES2);
        }
        //System.out.println("×××××××返回编码方案和对应的视频列表结束××××××××");
        return videoJointES2s;
    }



    /** 添加编码方案
     * 将视频和方案的对应关系添加到videoes
     * @param videoJointES 视频名数组和编码方案
     */
    @PostMapping("/insert")
    public int addES(@RequestBody final VideoJointES videoJointES) throws IOException {
        System.out.println("××××××正在添加编码方案××××××");
        //添加编码方案,并获得id
        int idES = encodingSchemeService.addEncodingScheme(videoJointES.getEncodingScheme());
        //添加视频和编码方案关系
        List<String> videoNames = videoJointES.getVideoNames();
        video_esService.addVideo_ES(videoNames,idES);
        System.out.println("××××××添加编码方案结束××××××");
        return idES;
    }

    /** 更新编码方案
     *
     * @param  es 视编码方案
     */
    @PostMapping("/update")
    public void updateES(@RequestBody final EncodingScheme es) throws IOException {
        System.out.println("×××××正在更新编码方案××××××");
        encodingSchemeService.updateEncodingScheme(es);

        System.out.println("××××××更新编码方案结束××××××");

    }

    /**
     * 根据id删除编码方案
     * @param id 编码方案的id
     * @return
     */
    @GetMapping("/deleteByID")
    public void deleteByID(int id){
        System.out.println(id);
        //删除编码方案表中的数据
        encodingSchemeService.deleteEncodingSchemeByID(id);
        //删除video-es表中的数据
        video_esService.deleteVideoESByIdes(id);
    }

//    /**
//     * 更新用户信息，需提供username
//     * 经测试，可用
//     * 无需token
//     * @param tester 用户信息
//     * @return 是否更新成功，1成功，0失败
//     */
//    @PostMapping("updateTester")
//    public int updateTester(@RequestBody final Tester tester){
//        return testerService.updateTester(tester);
//    }


}
