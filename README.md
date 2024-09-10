이 프로젝트는 아두이노에 할당된 명령어를 IP서버를 통해 Android Studio에 연결하여 원하는 명령을 전송할 수 있도록 구현한 프로그램입니다.<br>
현재까지 가능한 기능은<br>
- ip서버가 같은 네트워크에 연결된 카메라를 통해 어플리케이션에서 실시간 스트리밍이 가능합니다.
- 아두이노에 명령을 전송하여 상하좌우 버튼을 눌러 카메라의 움직임을 구현하였습니다.
- 음성명령을 openAI를 통해 상하좌우 카메라의 움직임이 가능하도록 구현했습니다.
- 아두이노에 명령을 전송하여 전등제어기능을 추가하였습니다.
----------------------------------------------------------------------------------------------
## 1.LoginActivity
 - ### DatabaseHelper
## 2.MainActivity
- ### CCTV 제어(CCTVActivity)<br>
- ### CCTVControlActivity
- ### StreamCCTV
- ### 전등 제어(LightControlActivity)<br>
-----------------------------------------
# LoginActivity
- 입력한 ID와 비밀번호를 가져와서 문자열로 반환하고 공백을 제거합니다<br>
- 'databaseHelper.checkUser(id,password)'로 데이터베이스에서 ID와 비밀번호를 확인합니다<br>
```java
    private void loginUser() {
        String id = editTextID.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (id.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (databaseHelper.checkUser(id, password)) {
            // 로그인 성공 시 MainActivity로 이동
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티를 종료하여 뒤로가기 시 로그인 화면으로 돌아가지 않게 함
        } else {
            Toast.makeText((), "로그인 실패: ID 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
        }
    }turn;
        }
```
LayoutInflater는 XML레이아웃 리소스를 Java 객체로 변환하는데 사용되는 클래스입니다.<br>
회원가입 버튼 클릭시 변환된 XML리소스가 나와서 회원가입뷰를 나타내줍니다.<br>
```Toast.makeText(getApplicationContext()```를 통해 현재 상황을 텍스트로 전달해줍니다.<br>
```java
private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회원가입");

        // Inflate the custom layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_register, null);
        builder.setView(dialogView);

        EditText editTextNewID = dialogView.findViewById(R.id.editTextNewID);
        EditText editTextNewPassword = dialogView.findViewById(R.id.editTextNewPassword);

        builder.setPositiveButton("완료", (dialog, which) -> {
            String id = editTextNewID.getText().toString().trim();
            String password = editTextNewPassword.getText().toString().trim();

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 데이터베이스에 사용자 정보 저장
            if (databaseHelper.insertUser(id, password)) {
                Toast.makeText(getApplicationContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "회원가입 실패! 같은 아이디가 존재합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();

        // 대화상자 외부를 클릭하여 닫히지 않도록 대화상자를 모달 형식으로 설정
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }
```

--------------------------------------

 ### DatabaseHelper
 DatabaseHelper클래스에서는 어플리케이션에서 사용자 정보를 관리하는 SQLite 데이터베이스를 다루기 위해 설계되었습니다.
<br>
SQLite를 상속받아 데이터베이스의 생성과 업그레이드를 관리합니다.<br>
테이블을 생성하여 사용자 데이터를 저장하고 로그인시 사용자 데이터 존재여부를 확인합니다.
이 클래스는 데이터베이스를 통해 사용자 정보를 안전하게 관리하고 사용자 인증 기능을 제공하는 것에 도움을 줍니다.
```java
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user_database.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // 사용자 추가 메서드
    public boolean insertUser(String id, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1; // 성공하면 true 반환
    }

    // 사용자 존재 확인 메서드
    public boolean checkUser(String id, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_ID + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{id, password}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

}
```

----------------------------------------------------------------------------------

# MainActivity

MainActivity 클래스는 HomeCCTV 애플리케이션의 주요 기능인 CCTV제어와 전등제어 기능으로 이동할 수 있게 나누어 둔 클래스입니다.
버튼을 클릭시 해당하는 액티비티로 전환하는 Intent 객체를 사용하여 액티비티간의 원활한 전환을 도와줍니다.
```java
public class MainActivity extends AppCompatActivity {

    private Button cctvButton;
    private Button lightControlButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 각 버튼에 대한 참조 초기화
        cctvButton = findViewById(R.id.cctvControlButton);
        lightControlButton = findViewById(R.id.lightControlButton);

        // CCTV 제어 화면으로 이동
        cctvButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CCTVActivity.class);
            startActivity(intent);
        });

        // 전등 제어 화면으로 이동
        lightControlButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LightControlActivity.class);
            startActivity(intent);
        });
    }
}
```

---------------------------------------------------------------------

# CCTVActivity

```java
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

        MyHomeCCTV cctvSurfaceView1 = findViewById(R.id.cctvSurfaceView1);
        MyHomeCCTV cctvSurfaceView2 = findViewById(R.id.cctvSurfaceView2);
        MyHomeCCTV cctvSurfaceView3 = findViewById(R.id.cctvSurfaceView3);

        // 각 뷰에 다른 URL 설정
        cctvSurfaceView1.setStreamUrl("http://192.168.0.109:8000/camera/mjpeg");
        cctvSurfaceView2.setStreamUrl("http://192.168.0.100:8000/camera/mjpeg");
        cctvSurfaceView3.setStreamUrl("http://109.236.111.203/mjpg/video.mjpg");
        //cctvSurfaceView2.setStreamUrl("http://192.168.0.109:8000/camera/mjpeg");

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
```
3개의 IP주소를 받아 영상을 스트리밍을 하였고 화면을 클릭하였을때 CCTV를 제어할 수 있는 액티비티로 넘어갈 수 있습니다.<br>
여기서는 2개의 컴퓨터로 다른 IP주소를 사용하여 ```private void openCCTVControlActivity(String url,String IP)```함수를 사용했을때 URL과 IP주소를 같이 기입해줘야 해당 주소의 서버로 명령어가 전달되어 원하는 영상의 움직임을 제어할 수 있습니다.<br>
현재 세번째 CCTV는 임의의 주소를 받아서 스트리밍만 구현해둔 상태로 움직임을 제어할 수 없는 상태이기 떄문에 cameraIP를 null로 지정해둔 상태입니다.

-----------------------------------------------------------------------------------------

CCTVControlActivity클래스는 CCTVActivity에서 선택한 카메라를 제어할 수 있도록 구현해둔 클래스로 버튼을 통한 움직임과 음성인식을 통한 움직임이 가능하도록 하였습니다.<br>
제일 많은 시간이 들었던 클래스였으며 기능을 수정하거나 추가하였을때 쓰레드간의 충돌이 생겨 어플리케이션이 강제종료되는 경우가 발생하여 다른 방법을 찾아볼 수 있는 좋은 경험이 되었습니다.<br>
Intent를 통해 화면 전환을 하는 과정에서 스택이 계속 쌓이는 부분을 해결하였습니다.<br>
주석처리 된 부분은 블루투스로 송수신하는 방법으로 원거리 통신에 제한이 있어 HomeCCTV의 목적성에 부적합하다 생각하여 서버를 통해 송수신하는 방법으로 구현하였습니다.<br>
# CCTVControlActivity

```java
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
```
<br>

```java
public void sendCommand(char command) {
    }
```
이 메서드는 UDP소켓을 사용해 지정해둔 명령어를 해당 카메라의 IP와 서버 포트로 전송합니다. 새로운 쓰레드를 만들어 네트워크 통신을 실행하며 이는 UI 쓰레드의 성능 저하를 방지합니다.<br>

``` InetAddress ia=InetAddress.getByName(cameraIP);``` 를 통해 카메라의 IP를 받아올 수 있으며 ``` DatagramPacket dp = new DatagramPacket(data,data.length,ia,SERVER_PORT);``` 에서는 위에서 정의해둔 SERVER_PORT=7777 사용합니다.<br>
<br>
```java
private void startVoiceRecognition()
```
<br>
```java
protected void onActivityResult(int requestCode, int resultCode, Intent data)
```

에서는 RecognizerIntent를 사용해 음성 인식 기능을 시작하며, 사용자의 음성을 텍스트로 변환합니다.<br>
EXTRA_LANGUAGE로 언어를 한국어로 설정하고, 사용자가 음성 명령을 입력하도록 프롬프트를 표시합니다.<br>
사용자의 음성이 텍스트로 변환된 후, onActivityResult() 메서드에서 그 결과를 처리합니다.<br>
인식된 명령어는 화면에 표시되고, handleVoiceCommand() 메서드를 통해 적절한 제어 명령으로 변환되어 UDP 패킷으로 전송됩니다.<br>
<br>
```java
public void handleVoiceCommand(String msgText) {      
    }
```
이 매서드는 위의 sendCommand와 형식은 비슷하나 이 메서드에선 커맨드가 아닌 텍스트형으로 데이터가 전송됩니다.<br>
포트는 9999번으로 Python에 연결하여 openai를 통해 텍스트가 적절한 커맨드로 변환되어 출력되도록 도와줍니다.<br>
<br>
```java
private void openCCTVlActivity(String url) {
    }
```
이 매서드는 뒤로가기버튼을 클릭시 새로운 Intent를 생성하여 CCTVActivity로 돌아가는 매서드입니다.<br>
새로운 Intent가 생성될 때마다 스택이 쌓여 CCTVActivity가 계속 충첩이 되는 오류가 있었습니다. 이를 해결하기 위해 ```intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);```를 사용하여 만약 CCTVActivity가 이미 백그라운드 스택에 존재하는 경우 해당 액티비티를 맨 위로 이동시키고 그 위의 다른 액티비티들을 제거하도록 하였습니다.<br>
이를 통해 불필요하게 액티비티가 중복되는 것을 방지하고 메모리 효율을 높힐 수 있게 되었습니다.<br>

-------------------------------------------------















