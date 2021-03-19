package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoTask implements Serializable {
    private static final long serialVersionUID= 4522943071576672086L;
    private int idVideoTask; //任务id
    private int idVideoES; //视频源和编码方案的id
    private int idVideoNetwork; //网络编码方案的id
    private  String vodURL; //被编码后的点播文件的url
    private String coverPath; //封面路径
    private  String streamType; //流的类型：点播、直播、摄像头直播
    private  String liveURL; //直播的url
    private  int vodStatus; //点播文件的状态：未开始转码、正在转码、转码完成
    private  int taskStatus; //任务的状态：未开始、正在进行、已结束
    private int idmerge; //合并同一编码方案的视频组，id
    private  String adminVodURL;


    public VideoTask(int idVideoES, int idVideoNetwork, String streamType, int vodStatus, int taskStatus, int idmerge) {
        this.idVideoES = idVideoES;
        this.idVideoNetwork = idVideoNetwork;
        this.streamType = streamType;
        this.vodStatus = vodStatus;
        this.taskStatus = taskStatus;
        this.idmerge = idmerge;
    }
}
