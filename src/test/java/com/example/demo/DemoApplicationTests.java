package com.example.demo;


import com.example.demo.entity.EncodingScheme;
import com.example.demo.entity.EncodingSchemeVideo;
import com.example.demo.entity.Tester;
import com.example.demo.entity.Video;
import com.sun.javafx.collections.MappingChange;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    DataSource dataSource;
    @Autowired
    private RestTemplate restTemplate;



    private String ffmpegURL = "http://192.168.10.119:8085" ;//后端url
    @Test
    public void test3(){
        //第一种 postForEntity 可以获取响应状态，头部信息等
        String url = ffmpegURL+"/testPost";
        EncodingSchemeVideo encodingSchemeVideo = new EncodingSchemeVideo();
        encodingSchemeVideo.setEs(new EncodingScheme());
        ResponseEntity<String> entity = restTemplate.postForEntity(url, encodingSchemeVideo, String.class);
        System.out.println(entity.getBody());

    }
//    @Test
//    public void testRest(){
//
//        String url = "http://10.112.65.6/WANem/index-advanced.php";
//        HttpHeaders headers =  new HttpHeaders();
//        headers.add("Content-Type","application/x-www-form-urlencoded");
//        //headers.add("Cookie","PHPSESSID=vdrq2j72upddqvrbtt17nrrds6");
//        headers.add("Referer",url);
//        headers.add("Cache-Control","no-cache");
//        headers.add("User-Agent","Mozilla/4.0");
//        //headers.add("Accept-Encoding","gzip,deflate");
//        headers.add("Accept","*/*");
//
//        MultiValueMap<String,String> params1 = new LinkedMultiValueMap<>();
//        MultiValueMap<String,String> params2 = new LinkedMultiValueMap<>();
//
//        params1.add("selInt","eth0");
//        params1.add("btnAdvanced","Start");
//
//        params2.add("txtLimit1",1000+""); //包的队列大小，当超过设定值时，超过的包将被丢弃
//        params2.add("selSym1","Yes");
//        params2.add("txtBandwidthAuto1","Other"); //从快速列表选择带宽
//        params2.add("txtBandwidth1",0+""); //自定义带宽
//        params2.add("txtDelay1",50+""); //延时，单向的延时
//        params2.add("txtLoss1",0+""); //丢包率
//        params2.add("txtDup1",0+""); //重包率 以一定概率生成某个包的多份拷贝，并按随机时间到达目标段
//        params2.add("txtReorder1",0+""); //包重排序率，按概率将包的顺序打乱
//        params2.add("txtCorrupt1",0+""); //错包率 按概率产生噪音，即格式错误的包
//        params2.add("txtDelayJitter1",0+""); //抖动
//        params2.add("txtLossCorrelation1",0+""); //丢包相关性，用来设置这个包的延时时间和上一个包的时间的相关度
//        params2.add("txtDupCorrelation1",0+""); //重包相关性
//        params2.add("txtReorderCorrelation1",0+""); //重排序相关性
//        params2.add("txtDelayCorrelation1",0+""); //延时相关性
//        params2.add("txtGap1",0+""); //用来确定包重新排序的个数，不设更接近真实环境
//        params2.add("selDelayDistribution1","-N/A-");
//        params2.add("selidtyp1","none");
//        params2.add("txtidtmr1","");
//        params2.add("txtidsctmr1","");
//        params2.add("selrndtyp1","none");
//        params2.add("txtrndmttflo1","");
//        params2.add("txtrndmttfhi1","");
//        params2.add("txtrndmttrlo1","");
//        params2.add("txtrndmttrhi1","");
//        params2.add("selrcdtyp1","none");
//        params2.add("txtrcdmttflo1","");
//        params2.add("txtrcdmttfhi1","");
//        params2.add("txtrcdmttrlo1","");
//        params2.add("txtrcdmttrhi1","");
//        params2.add("txtSrc1","any");
//        params2.add("txtSrcSub1","");
//        params2.add("txtDest1","any");
//        params2.add("txtDestSub1","");
//        params2.add("txtPort1","any");
//        params2.add("btnApply","Apply settings");
//        HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<>(params1,headers);
//        ResponseEntity<String> response = restTemplate.postForEntity(url,requestEntity,String.class);
//        String myCookie = response.getHeaders().get("Set-Cookie").get(0);
//        System.out.println(myCookie);
//        myCookie = myCookie.split(";")[0];
//        System.out.println(myCookie);
//        String body1 = response.getBody();
//        System.out.println(body1);
//        System.out.println("*******************************************");
//
//        headers.add("Cookie",myCookie);
//        HttpEntity<MultiValueMap<String,String>> requestEntity2 = new HttpEntity<>(params2,headers);
//        ResponseEntity<String> response2 = restTemplate.postForEntity(url,requestEntity2,String.class);
//        //response.getHeaders();
//        String body2 = response2.getBody();
//        //String json = restTemplate.getForObject(url,String.class);
//        System.out.println(body2);
//    }

    @Test
    void contextLoads() throws SQLException {
        System.out.println(dataSource.getClass());
        System.out.println(dataSource.getConnection());
    }

}
