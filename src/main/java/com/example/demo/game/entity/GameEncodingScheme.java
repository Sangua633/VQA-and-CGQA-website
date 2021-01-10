package com.example.demo.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameEncodingScheme implements Serializable {


    private static final long serialVersionUID = 677466561532046597L;
    private int idgameEncodingScheme=0;
    private  int width; //1280
    private  int height;//720
    private  int frameRate; //帧速率 20
    private  String encoding="h264"; //编码方式 h264
    private  int bitRate; //码率  Mbps
    private int isUsed=0;
    private  int idGame;
}
