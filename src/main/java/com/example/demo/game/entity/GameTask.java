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
    private int screenFluency;
    private int screenSharpness;
    private int screenColor;
    private int gameDelay;
    private int gameLag;
    private int assessmentStatus=0;
    //真实参数
    private String realWidth;//宽度
    private String realHeight;//高度
    private String realFrameRate;// 真实帧率 当前时刻的帧率 相加求平均
    private String realBitRate_Mbps; //Mbps 码率 当前时刻的码率 相加求平均
    private String realTotalLossPacket; //截至到此可的丢包数量
    private String realPacketRecive; //截至到此可的丢包数量
    private String realBytesRecive;  //截至到此可的接收的包数量
    private String timestamp="";
    public GameTask(String username,int idVideoNetwork, int idgameEncodingScheme){
        this.username = username;
        this.idgameEncodingScheme= idgameEncodingScheme;
        this.idVideoNetwork = idVideoNetwork;
    }

}
