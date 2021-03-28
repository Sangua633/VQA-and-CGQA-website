package com.example.demo.service;

import com.example.demo.entity.VideoTask_Tester;
import com.example.demo.mapper.VideoTask_TesterMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VideoTask_TesterService {
    @Resource
    private  VideoTask_TesterMapper videoTask_testerMapper;
    /**
     * 新建
     * @param videoTask_tester
     * @return
     */
    public void insert(VideoTask_Tester videoTask_tester){
        videoTask_testerMapper.add(videoTask_tester);
    }

    /**
     * 通过用户名查询任务
     * @param username 用户名
     * @return
     */
    public List<VideoTask_Tester> getVideoTaskByUsername(String username){
        return videoTask_testerMapper.queryVideoTaskByUsername(username);
    }
    public List<Integer> queryDistinctIdvtByAssessmentStatus(int assessmentStatus){
        return videoTask_testerMapper.queryDistinctIdvtByAssessmentStatus(assessmentStatus);
    }

    public List<VideoTask_Tester> queryTask(){
        return videoTask_testerMapper.queryTask();
    }
    public List<String> queryUsernameByIdvt(int idvt){
        return videoTask_testerMapper.queryUsernameByIdvt(idvt);
    }
    /**
     * 根据idvideotask tester 返回给任务的id
     * @param id
     * @return
     */
    public int queryIdVideoTaskByID(int id){
        int idvt = videoTask_testerMapper.queryByID(id).getIdVideoTask();
        return idvt;
    }
    public VideoTask_Tester queryByID(int id){
        return videoTask_testerMapper.queryByID(id);
    }


    /**
     * 更新用户评价
     * @param id 任务id
     * @param sharpness 清晰度
     * @param fluency 流畅度
     * @param color 颜色
     */
    public void updateAssessmentByID(int id,int sharpness,int fluency,int color){
        videoTask_testerMapper.updateAssessmentByID(id,sharpness,fluency,color,2);
    }
    public void updateAssessmentStatusByID(int id,int status){
        videoTask_testerMapper.updateAssessmentStatusByID(id,status);
    }
    public void updateLiveStatusByID(int id,int liveStatus){
        videoTask_testerMapper.updateLiveStatusByID(id,liveStatus);
    }
    public void updateLiveStatusByUsername(String username,int liveStatus){
        videoTask_testerMapper.updateLiveStatusByUsername(username,liveStatus);
    }
    public List<Integer> queryIDByIdvt(int id){
        return videoTask_testerMapper.queryIDByIdvt(id);
    }
    public List<Integer> queryAssessmentStatusByIdvt(int id){
        return videoTask_testerMapper.queryAssessmentStatusByIdvt(id);
    }
    public List<Integer> queryIDByUsernameLive(String username,int liveStatus){
        return videoTask_testerMapper.queryIDByUsernameLive(username,liveStatus);
    }
    public List<Integer> getDistinctIdVideoTask(){
        return videoTask_testerMapper.getDistinctIdVideoTask();
    }
    public void deleteByIdVideoTask(int idVideoTask){
        videoTask_testerMapper.deleteByIdVideoTask(idVideoTask);
    }

}
