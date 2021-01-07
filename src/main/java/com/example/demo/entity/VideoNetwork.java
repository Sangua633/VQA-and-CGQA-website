package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoNetwork implements Serializable {
    private static final long serialVersionUID = -4931452437541385162L;
    private int  idvideoNetwork;
    private String bandwidth;
    private String packetloss;
    private int lossCorrelation;
    private  String delay;
    private  String delayJitter;
    public  int isUsed=0;
}
