package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video_ES implements Serializable {
    private static final long serialVersionUID = 1026940718042509810L;
    private int idvideo_es;
    private String videoName; //视频名称
    private  int idEncodingScheme; //编码方案的id
    private int isUsed=0;
}
