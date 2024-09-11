package com.example.homecctv;

import static com.example.homecctv.DatabaseHelper.COLUMN_ID;
import static com.example.homecctv.DatabaseHelper.COLUMN_PASSWORD;
import static com.example.homecctv.DatabaseHelper.TABLE_USERS;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText editTextID;
    private EditText editTextPassword;
    private Button loginButton;
    private Button registerButton;

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//
//        // 필요한 블루투스 권한 요청
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
//        }
//


        databaseHelper = new DatabaseHelper(this);

        editTextID = findViewById(R.id.editTextID);
        editTextPassword = findViewById(R.id.editTextpassword);
        loginButton = findViewById(R.id.loginbtn);
        registerButton = findViewById(R.id.showDialogButton);

        // 로그인 버튼 클릭 리스너
        loginButton.setOnClickListener(v -> loginUser());

        // 회원가입 버튼 클릭 리스너
        registerButton.setOnClickListener(v -> showRegisterDialog());
    }

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
}
