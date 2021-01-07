package com.example.demo.utils.ffmpeg;

import com.example.demo.entity.EncodingScheme;
import com.example.demo.entity.Video;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 摄像头进行直播
 */
public class LivePushFile  implements Runnable {
    private static final String FFMPEG_PATH = "/storage/lss/ffmpegbuild/bin/ffmpeg";
    private static final String PUSH_URL = "rtmp://10.112.79.206:1935/hls/home1";
    private EncodingScheme vodEncodeParm;
    private Video video;
    private static Process process;
    public LivePushFile(EncodingScheme vodEncodeParm,Video video){
        this.vodEncodeParm = vodEncodeParm;
        this.video = video;
    }
    /**
     *ffmpeg
     * -re
     * -pix_fmt yuv420p
     * -s 3840x2160
     * -framerate 120
     * -i /storage/wpt2/4Kvideo/4K8bit/120fps/Beauty_3840x2160.yuv
     * -c:v h264_nvenc
     * -b:v 32000k
     * -minrate 32000k
     * -maxrate 32000k
     * -bufsize 64000k
     * -acodec aac
     * -s 1920x1080
     * -f flv
     * rtmp://10.112.79.206:1935/live/home1
     *
     */

    @Override
    public void run() {
        try {
            List<String> commands = new ArrayList<String>();
            commands.add(FFMPEG_PATH);
            commands.add("-re"); //模拟直播
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
            commands.add("-s");  //设置的分辨率
            commands.add(vodEncodeParm.getResolution());
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
            System.out.println("live退流结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
