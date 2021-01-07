package com.example.demo.mapper;

import com.example.demo.entity.Video;
import com.example.demo.entity.VideoTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface VideoTaskMapper {
    List<VideoTask> queryVideoTaskList();
    VideoTask queryVideoTaskListByID(int id);
    int addVideoTask(VideoTask videoTask);
    void updateVodStatusById(int idVideoTask,int vodStatus);
    void updateVodURLById(int idVideoTask,String vodURL);
    void updateLiveURLById(int idVideoTask,String liveURL);
    void updateAdminVodURLById(int idVideoTask,String adminVodURL);
    void updateTaskStatusById(int idVideoTask,int taskStatus);
    int countIdMerge();
    int getMaxIdMerge();
    List<Integer> getDistinctIdMerge();
    List<Integer> queryIdTaskByIdMerge(int idmerge);
    int queryVodStatusByID(int idVideoTask);
    String queryStreamTypeByID(int idVideoTask);

    int queryTaskStatusByID(int id);
    void deleteByID(int id);

}
