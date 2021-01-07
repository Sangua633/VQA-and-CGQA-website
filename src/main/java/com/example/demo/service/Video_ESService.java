package com.example.demo.service;

import com.example.demo.entity.EncodingScheme;
import com.example.demo.entity.Video;
import com.example.demo.entity.Video_ES;
import com.example.demo.mapper.EncodingSchemeMapper;
import com.example.demo.mapper.Video_ESMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class Video_ESService {
    @Resource
    private Video_ESMapper video_esMapper;

    /** 向video_es表中添加数据
     * 将一组视频名和编码方案的id批量添加到表中
     * @param videos 视频名列表
     * @param ides 编码方案的id
     * @return 是否增加成功
     */
    public int addVideo_ES(List<String> videos, int ides){
        System.out.println("××××××正在添加video_es××××××");
        int status = 0;
        for(String videoname:videos){
            status = video_esMapper.addVideo_ES(new Video_ES(0,videoname,ides,0));
        }
        System.out.println("××××××添加video_es结束××××××");
        return status;
    }

    /**
     * 根据编码任务的id查询对应的视频名
     * @param ides 编码任务id
     * @return 视频名组成的数组
     */
    public List<String> queryVideoNameByIdes(int ides){
        List<String> videoNames= video_esMapper.queryVideoNameByIdes(ides);
        //System.out.println("通过编码方案id "+ides+" 查询到的视频名"+videoNames);
        return videoNames;
    }
    public Video_ES queryById(int id){
        return video_esMapper.queryById(id);
    }

    public List<Integer> queryIDByIdes(int ides){
        List<Integer> IDs= video_esMapper.queryIDByIdes(ides);
        //System.out.println("通过编码方案id "+ides+" 查询到的videoes id"+IDs);
        return IDs;
    }

    public int deleteVideoESByIdes(int ides){
        return video_esMapper.deleteVideoESByIdes(ides);
    }
    public void updateIsUsedByID(int id,int isUsedStatus){
        video_esMapper.updateIsUsedByID(id,isUsedStatus);
    }


}
