package com.example.homecctv;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class CCTVActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);
        // 뒤로가기 버튼 설정
        Button buttonBack = findViewById(R.id.backmainBTN);

        buttonBack.setOnClickListener(v -> openMainlActivity());

        StreamCCTV cctvSurfaceView1 = findViewById(R.id.cctvSurfaceView1);
        StreamCCTV cctvSurfaceView2 = findViewById(R.id.cctvSurfaceView2);
        StreamCCTV cctvSurfaceView3 = findViewById(R.id.cctvSurfaceView3);

        // 각 뷰에 다른 URL 설정
        cctvSurfaceView1.setStreamUrl("http://192.168.0.109:8000/camera/mjpeg");
        cctvSurfaceView2.setStreamUrl("http://192.168.0.109:8000/camera/mjpeg");
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
    private void openMainlActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // 새 액티비티 시작
        startActivity(intent);
    }
}


