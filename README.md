이 프로젝트는 아두이노에 할당된 명령어를 IP서버를 통해 Android Studio에 연결하여 원하는 명령을 전송할 수 있도록 구현한 프로그램입니다.<br>
현재까지 가능한 기능은<br>
- ip서버가 같은 네트워크에 연결된 카메라를 통해 어플리케이션에서 실시간 스트리밍이 가능합니다.
- 아두이노에 명령을 전송하여 상하좌우 버튼을 눌러 카메라의 움직임을 구현하였습니다.
- 음성명령을 openAI를 통해 상하좌우 카메라의 움직임이 가능하도록 구현했습니다.
- 아두이노에 명령을 전송하여 전등제어기능을 추가하였습니다.
----------------------------------------------------------------------------------------------
## 1.LoginActivity
 ### DatabaseHelper
## 2.MainActivity
- CCTV 제어<br>
- 전등 제어<br>
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
            Toast.makeText(ㅊ(), "로그인 실패: ID 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
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

MainActivity 클래스는 HomeCCTV 애플리케이션의 주요 기능인 CCTV제어와 전등제어 기능으로 이동할 수 있게 분류해둔 클래스입니다.
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
