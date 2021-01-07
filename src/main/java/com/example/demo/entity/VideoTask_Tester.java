package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoTask_Tester implements Serializable {
    private static final long serialVersionUID = -1279326137902834909L;
    private  int idVideoTask_Tester;
    private  String username; //测试人员的用户名
    private  int idVideoTask; //任务id
    private  int sharpness;
    private  int fluency;
    private  int color;
    private  int assessmentStatus;
    private int liveStatus;

    public VideoTask_Tester(String username, int idVideoTask,int liveStatus,int assessmentStatus) {
        this.username = username;
        this.idVideoTask = idVideoTask;
        this.liveStatus = liveStatus;
        this.assessmentStatus = assessmentStatus;
    }
}
