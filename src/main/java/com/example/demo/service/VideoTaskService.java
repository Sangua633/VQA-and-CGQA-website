package com.example.demo.service;

import com.example.demo.entity.Tester;
import com.example.demo.entity.Video;
import com.example.demo.entity.VideoTask;
import com.example.demo.mapper.TesterMapper;
import com.example.demo.mapper.VideoMapper;
import com.example.demo.mapper.VideoTaskMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VideoTaskService {
    @Resource
    private VideoTaskMapper videoTaskMapper;
    @Resource
    private TesterMapper testerMapper;
    /**
     * 查询当前测试任务
     * @return
     */
    public VideoTask queryTaskByID(int id){
        VideoTask videoTask = videoTaskMapper.queryVideoTaskListByID(id);
        return videoTask;
    }
    public String queryStreamTypeByID(int id){
        return videoTaskMapper.queryStreamTypeByID(id);
    }
    /**
     * 更新点播文件的状态
     */
    public void updateVodStatusById(int id,int status){
        videoTaskMapper.updateVodStatusById(id,status);
    }
    public void updateVodURLById(int id,String vodURL){videoTaskMapper.updateVodURLById(id,vodURL); }
    public void updateLiveURLById(int id,String liveURL){videoTaskMapper.updateLiveURLById(id,liveURL); }
    public void updateAdminVodURLById(int id,String adminVodURL){videoTaskMapper.updateAdminVodURLById(id,adminVodURL); }
    public void updateCoverPathById(int id,String coverPath){
        videoTaskMapper.updateCoverPathById(id,coverPath);
    }
    public void updateTaskStatusById(int id,int status){
        videoTaskMapper.updateTaskStatusById(id,status);
    }
    /**
     * 新建一个任务
     * @param videoTask
     * @return 返回该任务的id
     */
    public int insert(VideoTask videoTask){
        videoTaskMapper.addVideoTask(videoTask);
        return videoTask.getIdVideoTask();
    }

    /**
     * 查询数据库中不同的idmerge的个数
     * @return
     */
    public int countIdMerge(){
        return videoTaskMapper.countIdMerge();
    }
    public int getMaxIdMerge(){
        return  videoTaskMapper.getMaxIdMerge();
    }
    /**
     * 查询数据库中不同的idmerge
     */
    public List<Integer> getDistinctIdMerge(){
        return  videoTaskMapper.getDistinctIdMerge();
    }
    public List<Integer> queryIdTaskByIdMerge(int idmerge){
        return videoTaskMapper.queryIdTaskByIdMerge(idmerge);
    }
    public int queryVodStatusByID(int id){
        return videoTaskMapper.queryVodStatusByID(id);
    }
    public  int queryTaskStatusByID(int id){
        return videoTaskMapper.queryTaskStatusByID(id);
    }
    public void deleteByID(int id){
        videoTaskMapper.deleteByID(id);
    }

//    /**
//     * 查询当前测试员的所有任务
//     * @return
//     */
//    public List<VideoTask> queryTaskByUsername(String username){
//        //得到tester的id
//        int idTester = testerMapper.queryTesterByUsername(username).getIdtester();
//        List<VideoTask> videoTasks = videoTaskMapper.queryTaskByIdtester(idTester);
//        for(VideoTask task:videoTasks) {
//            System.out.println("id为 "+idTester+" 用户名为 "+username+" 的测试员查询到的任务"+task);
//        }
//        return videoTasks;
//    }
//    /**
//     * 查询当前测试员的所有点播任务
//     * @return
//     */
//    public List<VideoTask> queryVodTaskByusername(String username){
//        //得到tester的id
//        int idTester = testerMapper.queryTesterByUsername(username).getIdtester();
//        List<VideoTask> videoTasks = videoTaskMapper.queryVodTaskByIdtester(idTester);
//        for(VideoTask task:videoTasks) {
//            System.out.println("id为 "+idTester+" 用户名为 "+username+" 的测试员查询到的点播任务"+task);
//        }
//        return videoTasks;
//    }
//





}
