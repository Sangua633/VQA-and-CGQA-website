package com.example.demo.service;


import com.example.demo.entity.VideoNetwork;
import com.example.demo.mapper.VideoNetworkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
@Service
public class VideoNetworkService {
    @Autowired
    private RestTemplate restTemplate;
    @Resource
    private VideoNetworkMapper videoNetworkMapper;
    private  final String WANemURL = "http://10.112.65.6/WANem/index-advanced.php";
    /**
     * 返回所有的网络配置
     * @return
     */
    public List<VideoNetwork> queryNetworkList(){
        List<VideoNetwork> networkList= videoNetworkMapper.queryNetworkList();
//        for(VideoNetwork network:networkList) {
//            System.out.println("查询到的网络编码方案"+network);
//        }
        return networkList;
    }
    public VideoNetwork query(int id){
        return  videoNetworkMapper.queryNetwork(id);
    }

    /**
     * 根据id查询网络的参数
     * 如果id为负数，则表示重置，所有参数为0
     * @param id 网络id
     * @return
     */
    public VideoNetwork getNetworkConfig(int id){
        if(id<0){
            return new VideoNetwork(0,0+"",0+"",0,0+"",0+"",0);
        }else{
            return videoNetworkMapper.queryNetwork(id);
        }
    }

    /**
     * 新增网络配置方案
     * @param videoNetwork
     */
    public void addNetwork(VideoNetwork videoNetwork){
        videoNetworkMapper.addNetwork(videoNetwork);
    }
    public void deleteNetworkByID(int id){
        videoNetworkMapper.deleteNetworkByID(id);
    }
    public void updateNetwork(VideoNetwork videoNetwork){
        videoNetworkMapper.updateNetwork(videoNetwork);
    }
    public void updateIsUsedByID(int id,int isUsedStatus){
        videoNetworkMapper.updateIsUsedByID(id,isUsedStatus);
    }
    /**
     * 根据网路id得到网络配置，然后按照配置控制Wanem使其生效
     * 如果id为负数，则表示重置，所有参数为0
     * @param id 网络id
     */
    public void setNetworkConfig(int id){
        VideoNetwork videoNetwork = getNetworkConfig(id);
        System.out.println(videoNetwork);
        //第一次请求，获得session id
        HttpHeaders headers =  new HttpHeaders();
        headers.add("Content-Type","application/x-www-form-urlencoded");
        headers.add("Referer",WANemURL);
        headers.add("Cache-Control","no-cache");
        headers.add("User-Agent","Mozilla/4.0");
        headers.add("Accept","*/*");
        MultiValueMap<String,String> params1 = new LinkedMultiValueMap<>();
        params1.add("selInt","eth0");
        params1.add("btnAdvanced","Start");
        HttpEntity<MultiValueMap<String,String>> requestEntity1 = new HttpEntity<>(params1,headers);
        ResponseEntity<String> response1 = restTemplate.postForEntity(WANemURL,requestEntity1,String.class);
        String myCookie = response1.getHeaders().get("Set-Cookie").get(0);
        myCookie = myCookie.split(";")[0];
        //第二次请求，header中加入id
        headers.add("Cookie",myCookie);
        MultiValueMap<String,String> params2 = new LinkedMultiValueMap<>();
        params2.add("txtLimit1",2000+""); //包的队列大小，当超过设定值时，超过的包将被丢弃
        params2.add("selSym1","Yes");
        params2.add("txtBandwidthAuto1","Other"); //从快速列表选择带宽
        params2.add("txtBandwidth1",Integer.parseInt(videoNetwork.getBandwidth())*1024+""); //自定义带宽
        params2.add("txtDelay1", videoNetwork.getDelay()); //延时，单向的延时
        params2.add("txtLoss1", videoNetwork.getPacketloss()); //丢包率
        params2.add("txtDup1",0+""); //重包率 以一定概率生成某个包的多份拷贝，并按随机时间到达目标段
        params2.add("txtReorder1",0+""); //包重排序率，按概率将包的顺序打乱
        params2.add("txtCorrupt1",0+""); //错包率 按概率产生噪音，即格式错误的包
        params2.add("txtDelayJitter1", videoNetwork.getDelayJitter()); //抖动
        params2.add("txtLossCorrelation1",videoNetwork.getLossCorrelation()+""); //丢包相关性，用来设置这个包的延时时间和上一个包的时间的相关度
        params2.add("txtDupCorrelation1",0+""); //重包相关性
        params2.add("txtReorderCorrelation1",0+""); //重排序相关性
        params2.add("txtDelayCorrelation1",0+""); //延时相关性
        params2.add("txtGap1",0+""); //用来确定包重新排序的个数，不设更接近真实环境
        params2.add("selDelayDistribution1","-N/A-");
        params2.add("selidtyp1","none");
        params2.add("txtidtmr1","");
        params2.add("txtidsctmr1","");
        params2.add("selrndtyp1","none");
        params2.add("txtrndmttflo1","");
        params2.add("txtrndmttfhi1","");
        params2.add("txtrndmttrlo1","");
        params2.add("txtrndmttrhi1","");
        params2.add("selrcdtyp1","none");
        params2.add("txtrcdmttflo1","");
        params2.add("txtrcdmttfhi1","");
        params2.add("txtrcdmttrlo1","");
        params2.add("txtrcdmttrhi1","");
        params2.add("txtSrc1","any");
        params2.add("txtSrcSub1","");
        params2.add("txtDest1","any");
        params2.add("txtDestSub1","");
        params2.add("txtPort1","any");
        params2.add("btnApply","Apply settings");
        HttpEntity<MultiValueMap<String,String>> requestEntity2 = new HttpEntity<>(params2,headers);
        ResponseEntity<String> response2 = restTemplate.postForEntity(WANemURL,requestEntity2,String.class);
        //String body2 = response2.getBody();
        //System.out.println(body2);
    }

}
