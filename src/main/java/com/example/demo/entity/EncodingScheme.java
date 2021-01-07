package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncodingScheme implements Serializable {

    private static final long serialVersionUID = 5335365682101989428L;
    private int idEncodingScheme;
    private  String resolution; //想要设置的分辨率 1920x1080
    private  String frameRate; //帧速率 120
    private  String encoding; //编码方式 h264/h265
    private  String encodingType; //硬编码还是软编码 hard / soft
    private  String bitRate; //码率  64000k 记得带单位k
    //private  String videoName; //源视频名称 Beauty_3840x2160.yuv
    private int isUsed=0;

}
