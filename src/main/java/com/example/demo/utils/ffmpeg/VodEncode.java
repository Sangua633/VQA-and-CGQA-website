package com.example.demo.utils.ffmpeg;

import com.example.demo.entity.EncodingScheme;
import com.example.demo.entity.Video;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class VodEncode implements Callable<String> {
    private static final String FFMPEG_PATH = "/storage/lss/ffmpegbuild/bin/ffmpeg";
    private static final String OUT_Root_Path = "/usr/local/nginx/html/hlsvod/";
    private EncodingScheme vodEncodeParm;
    private Video video;
    private static String outputName;
    private static Process process;
    public VodEncode(EncodingScheme vodEncodeParm,Video video){
        this.vodEncodeParm = vodEncodeParm;
        this.video = video;
    }

    public  String getOutputName() {
        return outputName;
    }

    /**
     * 将yuv文件编码为m3u8文件
     * ffmpeg  -pix_fmt yuv420p
     * -s 3840x2160
     * -framerate 120
     * -i /storage/wpt2/4Kvideo/4K8bit/120fps/Beauty_3840x2160.yuv
     * -c:v h264_nvenc
     * -b:v 64000k
     * -minrate 64000k
     * -maxrate 64000k
     * -bufsize 128000k
     * -acodec aac
     * -s 1920x1080
     * -f hls Beauty_3840x2160_64000k.m3u8
     */
    @Override
    public String call() throws Exception {
        try {
            List<String> commands = new ArrayList<String>();
            commands.add(FFMPEG_PATH);
            System.out.println(video.getVideoName());
            if(!"orange.mp4".equals(video.getVideoName())){
                commands.add("-pix_fmt");
                commands.add(video.getPix_fmt());
                commands.add("-s");
                commands.add(video.getResolution());
                commands.add("-framerate");
                commands.add(vodEncodeParm.getFrameRate());
            }else{
                commands.add("-r");
                commands.add(vodEncodeParm.getFrameRate());
            }
            commands.add("-i");
            commands.add(video.getFilePath()+video.getVideoName());
            String videocode;
            String encodingType= vodEncodeParm.getEncodingType();
            String encoding = vodEncodeParm.getEncoding();

            if("h264".equals(encoding) && "hard".equals(encodingType)){
                videocode = "h264_nvenc";
            }else if("h265".equals(encoding) && "hard".equals(encodingType)){
                videocode = "hevc_nvenc";
            }else if("h264".equals(encoding) && "soft".equals(encodingType)){
                videocode = "libx264";
            }else{
                videocode = "libx265";
            }
            commands.add("-c:v");
            commands.add(videocode);
            commands.add("-b:v");
            String bitRateMbps = vodEncodeParm.getBitRate();
            float bitRate = Float.parseFloat(bitRateMbps.substring(0,bitRateMbps.length()-4))*1024;
            commands.add(bitRate+"k");
            commands.add("-minrate");
            commands.add(bitRate+"k");
            commands.add("-maxrate");
            commands.add(bitRate+"k");
            commands.add("-bufsize");
            commands.add(bitRate*2+"k");
            commands.add("-acodec");
            commands.add("aac");
            commands.add("-s");
            commands.add(vodEncodeParm.getResolution());

            commands.add("-f");
            commands.add("hls");
            commands.add("-n");
            commands.add(OUT_Root_Path+outputName);
            System.out.println("当前命令"+commands);
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            process = builder.start();
            InputStream error = process.getErrorStream();
            InputStream is = process.getInputStream();
            byte[] b = new byte[1024];
            int readbytes = -1;
            try {
                while((readbytes = error.read(b)) != -1){
                    System.out.println("ffmpeg error logº"+new String(b,0,readbytes));
                }
                while((readbytes = is.read(b)) != -1){
                    System.out.println("ffmpeg log"+new String(b,0,readbytes));
                }
            }catch (IOException e2){

            }finally {
                error.close();
                is.close();
            }
            System.out.println("vod 转码结束 文件名为:"+outputName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputName;
    }
    public void setoutputName(){
        outputName = video.getVideoName().substring(0,video.getVideoName().length()-4)+
                "_"+vodEncodeParm.getFrameRate()+
                "_"+vodEncodeParm.getEncoding()+
                "_"+vodEncodeParm.getEncodingType()+
                "_"+vodEncodeParm.getResolution()+
                "_"+vodEncodeParm.getBitRate()+".m3u8";
    }
}
