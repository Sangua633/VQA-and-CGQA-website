package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoTaskNetworkTester implements Serializable {

    private static final long serialVersionUID = 937392581137478837L;

    private List<Integer> idESs; //编码方案的id数组
    private List<Integer> idVideoNetworks; //网络方案的id数组
    private List<String> usernames; //测试人员的用户名数组
    private String streamType; //任务类型：直播、点播

}
