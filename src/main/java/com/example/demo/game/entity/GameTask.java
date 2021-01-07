package com.example.demo.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameTask implements Serializable {

    private static final long serialVersionUID = 4027897498352141416L;
    private int idgameTask;
    private String username;
    private int idVideoNetwork;
    private int idgameEncodingScheme;
    private int status;
    //评价
    private int fluency;
    private int sharpness;
    private int color;
    private int delay;
    private int assessmentStatus;
    //真实参数
    private int realWidth;//宽度
    private int realHeight;//高度
    private int realFrameRate;// 真实帧率 相加求平均
    private int realBitRate; //Mbps 带宽速率 相加求平均
    private int realBandWidth; //Mbps 带宽速率 平均
    private int realTotalLossPacket; //累加
    private int realPacketRecive;  //累加

}
