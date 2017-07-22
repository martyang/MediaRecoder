package com.example.yangmin.meidarecorde;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

/**
 * 实现屏幕录制，录制其他界面是按home键退出，录制完后再次打开，点击stop即可，视频保存在内存根目录下
 * 实现步骤：
 * 1.获取MediaProcejecttionManaager，获取启动Intent，调用startActivityForResult方法获取录屏权限
 * 2.获取Mediaprojection,然后创建virtruedisplay,其中传入的surface使用MediaRecoder.getSurface获取，即可将获取的虚拟图像传入MediaRecoder
 * 3.初始化MediaRecoder,设置一些参数，然后将图像转换成MP4文件即可。
 */
public class MainActivity extends AppCompatActivity {

    String name = "recode";
    final int REQUEST_CODE = 1;
    final int FLAG_SERVICE = 10;
    MediaProjectionManager mpm;
    MediaProjection mediaProjection;
    MediaRecorder mMediaRecorder;
    boolean running = false;
    VirtualDisplay mVirtualDisplay;
    Button start ;
    boolean result_sucessed;
    File file;
    Intent intent;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaRecorder = new MediaRecorder();
        //恢复数据
//        if(savedInstanceState != null){
//            Log.i("Main","restory data");
//            running = savedInstanceState.getBoolean("runing");
//            if(running){
//                start.setText("Stop");
//            }
//        }
        if((intent=getIntent()) != null){
            name = intent.getStringExtra("casename");
            result_sucessed = intent.getBooleanExtra("result",false);
            type = intent.getIntExtra("type",-1);
        }

        file = new File(Environment.getExternalStorageDirectory().getPath()+"/"+name+".mp4");  //录屏生成文件
        start = (Button) findViewById(R.id.start);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},REQUEST_CODE);
        }

        if(type == 1){
            mpm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            Intent intent = mpm.createScreenCaptureIntent();
            startActivityForResult(intent,REQUEST_CODE);    //请求录屏权限
        }else if(running){
            mMediaRecorder.stop();
            release();
            Log.i("Main","stop recoder");
            running = false;
            start.setText("Start");

            if(result_sucessed){
                //case成功则删除视频
            }
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!running){
                    mpm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                    Intent intent = mpm.createScreenCaptureIntent();
                    startActivityForResult(intent,REQUEST_CODE);    //请求录屏权限

                }else {
                    mMediaRecorder.stop();
                    release();
                    Log.i("Main","stop recoder");
                    running = false;
                    start.setText("Start");

                    if(result_sucessed){
                        //case成功则删除视频
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        Log.i("Main","omstart 停止录屏");
        super.onStart();

        if(running) {
            mMediaRecorder.stop();
            release();
            Log.i("Main", "stop recoder");
            running = false;
            start.setText("Start");

            if (result_sucessed) {
                //case成功则删除视频
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("running",running);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){

            mediaProjection = mpm.getMediaProjection(resultCode,data);
            if(mediaProjection != null && !running){

                initMediaRecorder();
                VirtualDisplay virtualDisplay = mediaProjection.createVirtualDisplay("MainRecoder",480,720,120,FLAG_SERVICE,mMediaRecorder.getSurface(),null,null);
                mMediaRecorder.start();
                running = true;
                start.setText("Stop");
            }
        }
    }

    /**
     * 初始化MediaRecorder
     *
     * @return
     */
    public void initMediaRecorder() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(file.getPath());
        mMediaRecorder.setVideoSize(480, 720);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoEncodingBitRate(200000);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mMediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            mediaProjection.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }

    }
}
