이 프로젝트는 아두이노에 할당된 명령어를 IP서버를 통해 Android Studio에 연결하여 원하는 명령을 전송할 수 있도록 구현한 프로그램입니다.<br>
현재까지 가능한 기능은<br>
- ip서버가 같은 네트워크에 연결된 카메라를 통해 어플리케이션에서 실시간 스트리밍이 가능합니다.
- 아두이노에 명령을 전송하여 상하좌우 버튼을 눌러 카메라의 움직임을 구현하였습니다.
- 간단한 음성명령을 통해 상하좌우 카메라의 움직임이 가능하도록 구현했습니다.
- 아두이노에 명령을 전송하여 전등제어기능을 추가하였습니다.
----------------------------------------------------------------------------------------------
## 1.LoginActivity
## 2.MainActivity
- CCTV 제어<br>
- 전등 제어<br>
-----------------------------------------
# LoginActivity
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
- 입력한 ID와 비밀번호를 가져와서 문자열로 반환하고 공백을 제거
- 'databaseHelper.checkUser(id,password)'로 데이터베이스에서 ID와 비밀번호를 확인
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
LayoutInflater를 사용하여 커스텀 레이아웃을 다이얼로그에 포함시킵니다.이  레이아웃은 dialog_register.xml 파일에 정의되어 있습니다.
builder.setView(dialogView);는 커스텀 레이아웃을 다이얼로그의 내용으로 설정합니다.
