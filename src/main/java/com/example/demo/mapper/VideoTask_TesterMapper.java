package com.example.demo.mapper;

import com.example.demo.entity.Video;
import com.example.demo.entity.VideoTask_Tester;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface  VideoTask_TesterMapper {
    int add(VideoTask_Tester videoTask_tester);
    List<VideoTask_Tester> queryVideoTaskByUsername(String username);

    int updateAssessmentByID(int idVideoTask_Tester,int sharpness,int fluency,int color,int assessmentStatus);
    void updateAssessmentStatusByID(int idVideoTask_Tester,int assessmentStatus);
    void updateLiveStatusByID(int idVideoTask_Tester,int liveStatus);
    void updateLiveStatusByUsername(String username,int liveStatus);

    List<String> queryUsernameByIdvt(int id);
    List<Integer> queryIDByIdvt(int id);
    List<Integer> queryAssessmentStatusByIdvt(int id);
    VideoTask_Tester queryByID(int id);
    List<VideoTask_Tester> queryTask();
    List<Integer> queryIDByUsernameLive(String username,int liveStatus);
    List<Integer> getDistinctIdVideoTask();


    void deleteByIdVideoTask(int idVideoTask);
}
