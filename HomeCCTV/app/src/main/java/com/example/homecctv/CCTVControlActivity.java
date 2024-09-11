package com.example.homecctv;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Locale;

public class CCTVControlActivity extends AppCompatActivity {

    private StreamCCTV cctvSurfaceView;
//    private BluetoothController bluetoothController;
//    private static final String DEVICE_ADDRESS = "98:DA:60:07:6C:5B"; // 아두이노의 블루투스 주소
    private static final int SERVER_PORT = 7777; // 서버의 포트 번호
    private static final int VOICE_REQUEST_CODE = 101;
    private TextView voiceCommandResult;
    private String cameraIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv_control);

        cameraIP = getIntent().getStringExtra("cameraIP");

        // 뒤로가기 버튼 설정
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> openCCTVlActivity("url"));

        // CCTV 서페이스 뷰 설정
        cctvSurfaceView = findViewById(R.id.cctvSurfaceView);

        // 이전 액티비티에서 전달된 URL을 받습니다.
        String streamUrl = getIntent().getStringExtra("streamUrl");
        cctvSurfaceView.setStreamUrl(streamUrl);



        Button buttonUp = findViewById(R.id.buttonUp);
        Button buttonDown = findViewById(R.id.buttonDown);
        Button buttonLeft = findViewById(R.id.buttonLeft);
        Button buttonRight = findViewById(R.id.buttonRight);

        buttonUp.setOnClickListener(v -> sendCommand('U'));
        buttonDown.setOnClickListener(v -> sendCommand('D'));
        buttonLeft.setOnClickListener(v -> sendCommand('L'));
        buttonRight.setOnClickListener(v -> sendCommand('R'));

        //        bluetoothController = new BluetoothController(this);
//
//        // 블루투스 디바이스 연결 시도
//        if (!bluetoothController.connectToDevice(DEVICE_ADDRESS)) {
//            return; // 연결 실패 시 더 이상 진행하지 않음
//        }

        // CCTV 회전 버튼 설정
//        buttonUp.setOnClickListener(v -> bluetoothController.sendCommand("U"));
//        buttonDown.setOnClickListener(v -> bluetoothController.sendCommand("D"));
//        buttonLeft.setOnClickListener(v -> bluetoothController.sendCommand("L"));
//        buttonRight.setOnClickListener(v -> bluetoothController.sendCommand("R"));

        // 버튼 클릭 시 UDP 패킷으로 명령 전송
        voiceCommandResult = findViewById(R.id.voiceCommandResult);
        Button startVoiceRecognitionButton = findViewById(R.id.startVoiceRecognitionButton);

        startVoiceRecognitionButton.setOnClickListener(v -> startVoiceRecognition());
    }

    public void sendCommand(char command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    DatagramSocket ds = new DatagramSocket();
                    InetAddress ia=InetAddress.getByName(cameraIP);
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

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREA);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "명령어를 말하세요");

        try {
            startActivityForResult(intent, VOICE_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "음성 인식에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String command = matches.get(0);
                voiceCommandResult.setText("인식된 명령어: " + command);
                handleVoiceCommand(command);
            } else {
                voiceCommandResult.setText("음성 인식 결과가 없습니다.");
            }
        }
    }

    public void handleVoiceCommand(String msgText) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    DatagramSocket ds = new DatagramSocket();
                    InetAddress ia=InetAddress.getByName(cameraIP);
                    byte[] data = msgText.getBytes();
                    DatagramPacket dp = new DatagramPacket(data,data.length,ia,9999);
                    ds.send(dp);
                    ds.close();
                }catch (Exception e){
                    Log.d("UDPClient","Error: " + e.getMessage());
                }
            }
        }).start();
    }
    private void openCCTVlActivity(String url) {
        Intent intent = new Intent(this, CCTVActivity.class);
        intent.putExtra("streamUrl", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // 새 액티비티 시작
        startActivity(intent);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        bluetoothController.closeConnection();
//    }
}

