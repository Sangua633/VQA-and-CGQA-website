package com.example.demo.mapper;

import com.example.demo.entity.Tester;
import com.example.demo.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface VideoMapper {
    List<Video> queryVideoList();
    Video queryVideoById(int id);
    Video queryVideoByVideoname(String videoname);
    int addVideo(Video video );
    int updateVideo(Video video);
    int deleteVideoByID(int id);
    int deleteVideoByVideoname(String videoname);

    //int updateTesterMap(Map<String,Object> map);


}
