package com.example.demo.utils.ffmpeg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 向流媒体服务器上的后端发送有关点播文件的命令
 * 例如：
 *     转码
 *     检查文件是否存在
 */
public class SendVod {
    @Autowired
    private RestTemplate restTemplate;
    private String ffmpegURL = "http://10.112.79.206:8084" ;//后端url

    /**
     * 简单的测试一下能不能通
     * 涉及有参的两种get请求方式
     * 可以通
     */
    public void test(){
        //第一种 getForEntity中直接写参数
        // 参数在链接地址后 用数字标识先后位置
        String testInterface = "/test";
        String url = ffmpegURL+testInterface+"?username={1}";
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class,"bobo");
        System.out.println("响应内容1====>"+entity.getBody());

        //第一种 用Map封装参数
        // 参数在链接地址后 用参数名标识位置
        String url1 = ffmpegURL+testInterface+"?userName={userName}";
        Map<String,Object> map = new HashMap<>();
        map.put("userName","波波烤鸭");
        ResponseEntity<String> entity1 = restTemplate.getForEntity(url1, String.class,map);
        System.out.println("响应内容2====>"+entity1.getBody());
    }

    /**
     * 点播文件转码，需要传递的参数有
     * @return
     */
//    public String vodEncode()

}
