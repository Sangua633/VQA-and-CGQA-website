package com.example.demo.mapper;

import com.example.demo.entity.EncodingScheme;
import com.example.demo.entity.Video_ES;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface Video_ESMapper {
    //List<Video_ES> queryEncodingSchemeList();
    //EncodingScheme queryEncodingSchemeById(int id);
    int addVideo_ES(Video_ES video_es);
    List<String> queryVideoNameByIdes(int ides); //根据编码方案id查询视频
    List<Integer> queryIDByIdes(int ides); //根据编码方案id查询id
    Video_ES queryById(int id);//根据videoesid查询
    //int updateEncodingScheme(EncodingScheme encodingScheme);
    void updateIsUsedByID(int id,int isUsedStatus);
    int deleteVideoESByIdes(int ides);

}
