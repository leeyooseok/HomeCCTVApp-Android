package com.example.homecctv;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class CCTVActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);

        StreamCCTV cctvSurfaceView1 = findViewById(R.id.cctvSurfaceView1);
        StreamCCTV cctvSurfaceView2 = findViewById(R.id.cctvSurfaceView2);
        StreamCCTV cctvSurfaceView3 = findViewById(R.id.cctvSurfaceView3);

        // 각 뷰에 다른 URL 설정
        cctvSurfaceView1.setStreamUrl("http://192.168.0.109:8000/camera/mjpeg");
        cctvSurfaceView2.setStreamUrl("http://192.168.0.100:8000/camera/mjpeg");
        cctvSurfaceView3.setStreamUrl("http://109.236.111.203/mjpg/video.mjpg");;

        // 첫 번째 CCTV 클릭 시
        cctvSurfaceView1.setOnClickListener(v -> openCCTVControlActivity("http://192.168.0.109:8000/camera/mjpeg","192.168.0.109"));

        // 두 번째 CCTV 클릭 시
        cctvSurfaceView2.setOnClickListener(v -> openCCTVControlActivity("http://192.168.0.100:8000/camera/mjpeg","192.168.0.100"));

        // 세 번째 CCTV 클릭 시
        cctvSurfaceView3.setOnClickListener(v -> openCCTVControlActivity("http://109.236.111.203/mjpg/video.mjpg",null));
    }


    private void openCCTVControlActivity(String url,String IP) {
        Intent intent = new Intent(this, CCTVControlActivity.class);
        intent.putExtra("streamUrl", url);
        intent.putExtra("cameraIP", IP);
        startActivity(intent);
    }
}


