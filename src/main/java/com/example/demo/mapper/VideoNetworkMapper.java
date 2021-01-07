package com.example.demo.mapper;

import com.example.demo.entity.Tester;
import com.example.demo.entity.VideoNetwork;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface VideoNetworkMapper {

    List<VideoNetwork> queryNetworkList();
    VideoNetwork queryNetwork(int id);
    int addNetwork(VideoNetwork videoNetwork);
    int updateNetwork(VideoNetwork videoNetwork);
    void updateIsUsedByID(int id,int isUsedStatus);
    int deleteNetworkByID(int id);
}
