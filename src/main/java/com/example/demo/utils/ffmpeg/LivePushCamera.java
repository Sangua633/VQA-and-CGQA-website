package com.example.demo.utils.ffmpeg;

import com.example.demo.entity.EncodingScheme;
import com.example.demo.entity.Video;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LivePushCamera implements Runnable {
    private static final String FFMPEG_PATH = "/storage/lss/ffmpegbuild/bin/ffmpeg";
    private static final String PUSH_URL = "rtmp://10.112.79.206:1935/hls/home1";
    private static final String INPUT_NAME = "/dev/video0";
    private EncodingScheme vodEncodeParm;
    private static Process process;
    public LivePushCamera(EncodingScheme vodEncodeParm){
        this.vodEncodeParm = vodEncodeParm;
    }
    /**
     *ffmpeg
     * -f video4linux2
     * -s 3840x2160
     * -framerate 30
     * -i /dev/video0
     * -c:v h264_nvenc
     * -b:v 32000k
     * -minrate 32000k
     * -maxrate 32000k
     * -bufsize 64000k
     * -acodec aac
     * -f flv
     * rtmp://10.112.79.206:1935/live/home1
     *
     */
    @Override
    public void run()  {
        try {
            List<String> commands = new ArrayList<String>();
            commands.add(FFMPEG_PATH);
            commands.add("-f"); //
            commands.add("video4linux2"); //摄像头直播
            commands.add("-s"); //分辨率
            commands.add(vodEncodeParm.getResolution());
            commands.add("-framerate");  //帧速率
            commands.add(vodEncodeParm.getFrameRate());
            commands.add("-i");
            commands.add(INPUT_NAME);
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
            commands.add("-f");
            commands.add("flv");
            commands.add(PUSH_URL);
            System.out.println("当前命令"+commands);

            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            process = builder.start();
            InputStream error = process.getErrorStream();
            InputStream is = process.getInputStream();

            byte[] b = new byte[1024];
            int readbytes = -1;
            try {
                while((readbytes = error.read(b)) != -1 ){
                    if(Thread.currentThread().isInterrupted()){
                        process.destroy();
                        break;
                    }
                    System.out.println("ffmpeg error logº"+new String(b,0,readbytes));
                }
                while((readbytes = is.read(b)) != -1 && !Thread.currentThread().isInterrupted()){
                    if(Thread.currentThread().isInterrupted()){
                        process.destroy();
                        break;
                    }
                    System.out.println("ffmpeg log"+new String(b,0,readbytes));
                }
                System.out.println("结束");
                process.destroy();
            }catch (Exception e){
                System.out.println(e.toString());
            }finally {
                error.close();
                is.close();
                System.out.println("关闭流");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
