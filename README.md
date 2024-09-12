## HomeCCTVApplication

이 프로젝트는 아두이노에 할당된 명령어를 IP서버를 통해 Android Studio에 연결하여 원하는 명령을 전송할 수 있도록 구현한 프로그램입니다.<br>
이 프로그램을 작동하기 위해선 Python,java,arduino를 함께 사용해야합니다.<br>
전체적인 프로젝트는 아래 링크에서 확인해 주시면 됩니다.
## [HomeCCTV Project-all](https://github.com/leeyooseok/HomeCCTVApp-project.git)
------------------------------------------------------

### 현재까지 가능한 기능은<br>
- ip서버가 같은 네트워크에 연결된 카메라를 통해 어플리케이션에서 실시간 스트리밍이 가능합니다.
- 아두이노에 명령을 전송하여 상하좌우 버튼을 눌러 카메라의 움직임을 구현하였습니다.
- 음성명령을 openAI를 통해 상하좌우 카메라의 움직임이 가능하도록 구현했습니다.
- 아두이노에 명령을 전송하여 조명제어기능을 추가하였습니다.
  
--------------------------------------------------------------------------------------------

### 추가 예정 기능<br>
- 현재 구현된 조명제어기능으론 집안의 전등제어가 불가능하여 아두이노의 모터 움직임을 통해 전등을 제어하는 기능 추가할 예정입니다.
- 워키토키 형식으로 음성을 주고 받을 수 있게 하여 소리가 날 경우 자동으로 음성알림을 어플리케이션에서 받아 보안성을 높힐 수 있도록 기능 추가예정입니다.
- 아두이노를 통해 집안의 온도와 습도를 확인할 수 있는 기능 추가 예정입니다.

----------------------------------------------------------------------------------------------
## 1. [LoginActivity](#loginactivity)
- ### [로그인 정보(DatabaseHelper)](#databasehelper)
## 2. [MainActivity](#mainactivity)
- ### [CCTV 영상(CCTVActivity)](#cctvactivity)
- ### [CCTV제어(CCTVControlActivity)](#cctvcontrolactivity)
- ### [영상 스트림(StreamCCTV)](#streamcctv)
- ### [조명 밝기 제어 (LightControlActivity)](#lightcontrolactivity)
-----------------------------------------
## LoginActivity
![123](https://github.com/user-attachments/assets/d1205bf4-2199-4d3a-b2ad-51012422913c)




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
            Toast.makeText(getApplicationContext(), "로그인 실패: ID 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
        }
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

## MainActivity

![Screenshot_20240911_171451](https://github.com/user-attachments/assets/c3ea7638-2faa-47bb-9680-620942e5c939)


MainActivity 클래스는 HomeCCTV 애플리케이션의 주요 기능인 CCTV제어와 조명제어 기능으로 이동할 수 있게 나누어 둔 클래스입니다.
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

        // 조명 제어 화면으로 이동
        lightControlButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LightControlActivity.class);
            startActivity(intent);
        });
    }
}
```

---------------------------------------------------------------------

### CCTVActivity

![Screenshot_20240911_172125](https://github.com/user-attachments/assets/d87a1925-1f60-4068-b24d-30002819c080)


3개의 IP주소를 받아 영상을 스트리밍을 하였고 화면을 클릭하였을때 CCTV를 제어할 수 있는 CCTVControlActivity로 이동할 수 있습니다.<br>
여기서는 2개의 컴퓨터로 다른 IP주소를 사용하여 ```private void openCCTVControlActivity(String url,String IP)```함수를 사용했을때 URL과 IP주소를 같이 기입해줘야 해당 주소의 서버로 명령어가 전달되어 원하는 영상의 움직임을 제어할 수 있습니다.<br>
현재 세번째 CCTV는 임의의 URL주소를 받아서 스트리밍만 구현해둔 상태로 움직임을 제어할 수 없는 상태이기 떄문에 cameraIP를 null로 지정해둔 상태입니다.

```java
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

-----------------------------------------------------------------------------------------

### CCTVControlActivity


[![Video Title](https://img.youtube.com/vi/KwEdhXuhTcs/0.jpg)](https://www.youtube.com/watch?v=KwEdhXuhTcs)




CCTVControlActivity클래스는 CCTVActivity에서 선택한 카메라를 제어할 수 있도록 구현해둔 클래스로 버튼을 통한 움직임과 음성인식을 통한 움직임이 가능하도록 하였습니다.<br>
제일 많은 시간이 들었던 클래스였으며 기능을 수정하거나 추가하였을때 쓰레드간의 충돌이 생겨 어플리케이션이 강제종료되는 경우가 발생하여 다른 방법을 찾아볼 수 있는 좋은 기화가 되었습니다.<br>
Intent를 통해 화면 전환을 하는 과정에서 스택이 계속 쌓이는 부분을 해결하였습니다.<br>
주석처리 된 부분은 블루투스로 송수신하는 방법으로 원거리 통신에 제한이 있어 HomeCCTV의 목적성에 부적합하다 생각하여 서버를 통해 송수신하는 방법으로 구현하였습니다.<br>

```java
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
private void startVoiceRecognition(){
    }
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

### StreamCCTV

StreamCCTV클래스에서는 네트워크를 통해 특정 URL에서 비디오 데이터를 계속하여 받아오고 실시간으로 화면에 랜더링하는 역할을 합니다.<br>
네트워크를 통해 전송된 비디오 스트림은 일반적으로 프레임 단위로 나누어져 전송되는데 이 코드는 비디오 프레임을 하나씩 받아와 각각의 이미지를 화면에 그리는 방식으로 처리하였습니다.<br>
```java
public class StreamCCTV extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final int THREAD_POOL_SIZE = 1;
    private ExecutorService executorService;
    private boolean threadRunning = true;
    private String streamUrl;

    public StreamCCTV(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void setStreamUrl(String url) {
        this.streamUrl = url;
        if (threadRunning) {
            executorService.submit(this);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        }
        if (threadRunning) {
            executorService.submit(this);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        threadRunning = false;
        executorService.shutdownNow(); // 모든 쓰레드 종료
    }

    @Override
    public void run() {
        final int maxImgSize = 10000000;
        byte[] arr = new byte[maxImgSize];
        try {
            URL url = new URL(streamUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream in = con.getInputStream();
            while (threadRunning) {
                int i = 0;
                for (; i < 2000; i++) {
                    int b = in.read();
                    if (b == 0xff) {
                        int b2 = in.read();
                        if (b2 == 0xd8)
                            break;
                    }
                }
                if (i > 999) {
                    Log.e("MyHomeCCTV", "Bad head!");
                    continue;
                }
                arr[0] = (byte) 0xff;
                arr[1] = (byte) 0xd8;
                i = 2;
                for (; i < maxImgSize; i++) {
                    int b = in.read();
                    arr[i] = (byte) b;
                    if (b == 0xff) {
                        i++;
                        int b2 = in.read();
                        arr[i] = (byte) b2;
                        if (b2 == 0xd9) {
                            break;
                        }
                    }
                }
                i++;
                int nBytes = i;
                Log.e("MyHomeCCTV", "got an image, " + nBytes + " bytes!");

                Bitmap bitmap = BitmapFactory.decodeByteArray(arr, 0, nBytes);
                bitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), false);

                SurfaceHolder holder = getHolder();
                Canvas canvas = null;
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        } catch (Exception e) {
            Log.e("MyHomeCCTV", "Error:" + e.toString());
        }
    }
}
```

``` private static final int THREAD_POOL_SIZE = 1;```쓰레드 풀 크기를 1로 설정하여 단일 스레드로 실행되는 ```ExecutorService```를 사용합니다. 이를 통해 스트리밍 작업이 별도의 쓰레드에서 비동기적으로 실행됩니다.<br>
```ExecutorService```는 Java에서 제공되는 인터페이스로 쓰레드 관리를 위한 도구입니다. 이 도구는 비동기 작업을 실행하고 쓰레드 풀을 사용 해 여러 쓰레드를 효율적으로 관리합니다.
- 스레드 관리: ExecutorService는 스레드를 직접 생성하고 관리하는 대신, 스레드 풀을 사용해 스레드가 필요할 때 재사용할 수 있습니다. 이 방식은 스레드를 매번 새로 생성하는 것보다 성능이 더 좋고 리소스를 절약할 수 있습니다.
- 비동기 작업 실행: ExecutorService는 작업을 비동기적으로 처리합니다. 즉, 특정 작업을 다른 스레드에서 실행하면서, 메인 스레드(UI 스레드)는 계속해서 다른 작업을 수행할 수 있습니다. 이렇게 하면 UI가 멈추지 않고 부드럽게 동작할 수 있습니다.
- 스레드 종료: 작업이 끝난 후 스레드를 적절하게 종료하거나 재활용할 수 있습니다. shutdown()이나 shutdownNow() 같은 메서드를 사용해 스레드 풀을 안전하게 종료할 수 있습니다.<br>

```java
public void setStreamUrl(String url) {
    }
```
setStreamUrl() 메서드는 스트리밍 URL을 설정하고, 스레드가 실행 중이면 해당 URL로부터 비디오 데이터를 가져오는 작업을 스레드 풀에 제출합니다.<br>

```java
public void run() {
    }
```
이 메서드는 비디오 스트림을 실시간으로 처리하여 화면에 랜더링하는 주요 작업을 수행하며 비디오 데이터의 전송과 처리를 매끄럽게 관리합니다.<br>
maxImgSize는 최대 이미지 크기를 정의하는 상수이며 이 값은 비디오 프레임의 최대 크기를 의미합니다.<br>
스트림 URL에서 HttpURLConnection을 사용해 데이터를 가져오고, 스트림에서 받은 이미지를 처리하여 화면에 그립니다.<br>
JPEG 파일 형식의 비디오 데이터를 읽어와, 헤더(0xFFD8)와 푸터(0xFFD9)를 기준으로 각 이미지 프레임을 추출합니다.<br>
추출한 이미지를 Bitmap으로 변환하고, SurfaceView에 그려 줍니다.<br>
이 과정에서 다른 방법으로 영상을 랜더링하는 방법을 찾아봤으나 랜더링의 오류가 많아 이 방법을 선택하였습니다.<br>

--------------------------------------------------------------------------------------------------------

### LightControlActivity

[Screen_recording_20240911_171537.webm](https://github.com/user-attachments/assets/e584b52f-6350-4980-a2c4-a78e8354a284)

LightControlActivity클래스는 조명 밝기 제어 목적으로 구현해둔 클래스로 4개의 조명제어가 가능하고 전체 점등,소등 버튼을 추가하여 편리성을 높혔습니다.<br>
이 클래스의 xml은 on/off스위치 이미지를 겹쳐서 버튼을 클릭할 경우 이미지가 전환되는 형식으로 현재의 조명상태를 확인할 수 있습니다.
집안의 전등은 아두이노 모터를 통해 점등,소등이 제어 가능한 거로 알고 있으나 현재 주어진 상황에선 조명제어만 가능하여 조명제어만 구현하였습니다.<br>
코드의 매서드가 CCTVControlActivity와 유사하여 중복되는 코드는 위의 CCTVControlActivity클래스 참고해주시면 되겠습니다.
```java
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
```
버튼 하나에 on/off형식인 토글형식으로 제어하도록 버튼을 클릭하였을때 on,off이미지가 전환되고 커맨드가 전달되는 형식의 코드입니다.<br>
기본 상태는 전원 off상태로 설정해둡니다.<br>

```java
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
```
매개변수 ```isOn````을 사용하여 isOn이 true일 경우 모든 조명을 켜고 false일 경우 모든 조명을 끄는 상태로 설정합니다.<br>

```java
int visibilityOff = isOn ? ImageView.GONE : ImageView.VISIBLE;
int visibilityOn = isOn ? ImageView.VISIBLE : ImageView.GONE;
```
조명이 켜진 상태일경우 off이미지를 GONE상태로, 꺼진 상태일 경우 VISIBLE상태로 설정합니다.<br>
조명이 꺼진 상태일경우 on이미지를 VISIBLE상태로, 꺼진 상태일 경우 GONE상태로상태로 설정합니다.<br>
```java
for (int i = 1; i <= 4; i++) {  
 }
```
반복문을 통해 1번부터 4번까지의 조명을 차례로 처리합니다.<br>
getIdentifier() 메서드를 사용해 조명의 꺼짐(lightImage1, lightImage2 등) 및 켜짐(lightImage1On, lightImage2On 등) 이미지를 동적으로 찾습니다.<br>
findViewById()를 사용하여 찾은 이미지 뷰의 가시성을 앞서 정의한 visibilityOff 및 visibilityOn 값에 따라 설정합니다.<br>

------------------------------------------------------------------------------------
