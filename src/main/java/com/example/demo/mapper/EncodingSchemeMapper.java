package com.example.demo.mapper;

import com.example.demo.entity.EncodingScheme;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EncodingSchemeMapper {
    List<EncodingScheme> queryEncodingSchemeList();
//    List<Integer> queryEncodingScheme(EncodingScheme encodingScheme);
    EncodingScheme queryEncodingSchemeById(int id);
    int addEncodingScheme(EncodingScheme encodingScheme);
    int updateEncodingScheme(EncodingScheme encodingScheme);
    void updateIsUsedByID(int id,int isUsedStatus);
    int deleteEncodingSchemeByID(int id);

}
