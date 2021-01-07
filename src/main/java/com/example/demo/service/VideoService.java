package com.example.demo.service;

import com.example.demo.entity.Tester;
import com.example.demo.entity.Video;
import com.example.demo.mapper.TesterMapper;
import com.example.demo.mapper.VideoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VideoService {
    @Resource
    private VideoMapper videoMapper;
    /**
     * 查询数据库中的所有视频信息
     * @return
     */
    public List<Video> queryVideoList(){
        List<Video> videoList = videoMapper.queryVideoList();
        for(Video video:videoList) {
            System.out.println("查询到的视频"+video);
        }
        return videoList;
    }


//    public Tester queryTesterById(int id){
//        Tester tester = testerMapper.queryTesterById(id);
//        System.out.println("通过id查询到的用户"+tester);
//        return tester;
//    }

    /**
     * 通过视频名查询视频
     * @param videoname 视频名
     * @return 视频
     */
    public Video queryByVideoName(String videoname){
        Video video = videoMapper.queryVideoByVideoname(videoname);
        return video;
    }

    public int  addVideo(Video video){
        return videoMapper.addVideo(video);
    }
    public int deleteVideoByID(int idvideo){
        return videoMapper.deleteVideoByID(idvideo);
    }
    public int deleteVideoByVideoname(String videoname){
        return videoMapper.deleteVideoByVideoname(videoname);
    }
//    public int updateTester(Tester tester) {
//        return testerMapper.updateTester(tester);
//    }
}
