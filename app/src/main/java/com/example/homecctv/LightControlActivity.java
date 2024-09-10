package com.example.homecctv;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LightControlActivity extends AppCompatActivity {

    private static final String SERVER_IP = "192.168.0.109"; // 서버의 IP 주소
    private static final int SERVER_PORT = 7777; // 서버의 포트 번호

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightcontrol);

        // 뒤로가기 버튼 설정
        Button buttonBack2 = findViewById(R.id.backbutton2);
        buttonBack2.setOnClickListener(v -> openMainActivity());

        // 조명 버튼 설정
        setupLightControls();

        // 전체 조명 제어 버튼 설정
        setupAllLightsControl();

    }
    private void setupLightControls() {
        setupLightControl(R.id.lightImage1, R.id.lightImage1On, R.id.buttonLight1, 'a', 'b');
        setupLightControl(R.id.lightImage2, R.id.lightImage2On, R.id.buttonLight2, 'c', 'd');
        setupLightControl(R.id.lightImage3, R.id.lightImage3On, R.id.buttonLight3, 'e', 'f');
        setupLightControl(R.id.lightImage4, R.id.lightImage4On, R.id.buttonLight4, 'g', 'h');
    }

    private void setupLightControl(int offImageId, int onImageId, int buttonId, char onCommand, char offCommand) {
        ImageView lightImageOff = findViewById(offImageId);
        ImageView lightImageOn = findViewById(onImageId);
        Button controlButton = findViewById(buttonId);

        // Default state (off)
        lightImageOff.setVisibility(ImageView.VISIBLE);
        lightImageOn.setVisibility(ImageView.GONE);

        controlButton.setOnClickListener(v -> {
            if (lightImageOff.getVisibility() == ImageView.VISIBLE) {
                lightImageOff.setVisibility(ImageView.GONE);
                lightImageOn.setVisibility(ImageView.VISIBLE);
                sendCommand2(onCommand);
            } else {
                lightImageOff.setVisibility(ImageView.VISIBLE);
                lightImageOn.setVisibility(ImageView.GONE);
                sendCommand2(offCommand);
            }
        });
    }

    private void setupAllLightsControl() {
        ToggleButton toggleAllLights = findViewById(R.id.toggleAllLights);

        toggleAllLights.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sendCommand2('i'); // 모든 조명을 켜는 명령을 서버로 전송
                setAllLightVisibility(true);
            } else {
                sendCommand2('j'); // 모든 조명을 끄는 명령을 서버로 전송
                setAllLightVisibility(false);
            }
        });
    }

    private void setAllLightVisibility(boolean isOn) {
        int visibilityOff = isOn ? ImageView.GONE : ImageView.VISIBLE;
        int visibilityOn = isOn ? ImageView.VISIBLE : ImageView.GONE;

        for (int i = 1; i <= 4; i++) {
            int offImageId = getResources().getIdentifier("lightImage" + i, "id", getPackageName());
            int onImageId = getResources().getIdentifier("lightImage" + i + "On", "id", getPackageName());

            findViewById(offImageId).setVisibility(visibilityOff);
            findViewById(onImageId).setVisibility(visibilityOn);
        }
    }

    public void sendCommand2(char command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    DatagramSocket ds = new DatagramSocket();
                    InetAddress ia=InetAddress.getByName(SERVER_IP);
                    byte[] data = new String(new char[]{command}).getBytes();
                    DatagramPacket dp = new DatagramPacket(data,data.length,ia,SERVER_PORT);
                    ds.send(dp);
                    ds.close();
                }catch (Exception e){
                    Log.d("UDPClient","Error: " + e.getMessage());
                }
            }
        }).start();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, CCTVActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
