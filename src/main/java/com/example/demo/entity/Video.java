package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video implements Serializable {
    private static final long serialVersionUID= 4522943071576672085L;
    private int idvideo;
    private  String videoName; //视频名称 Beauty_3840x2160.yuv  后缀有可能是y4m
    private  String pix_fmt; //yuv格式 "yuv420p"
    private  String resolution; //分辨率 3840x2160
    private  String frameRate; //帧速率 120
    private  String duration; //时长 5s
    private  String filePath; //视频文件的存储路径
    private  String motionSpeed; //运动速度 高 中 低


}
