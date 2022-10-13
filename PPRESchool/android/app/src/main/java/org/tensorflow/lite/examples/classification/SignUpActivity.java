package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.classification.data.ChildData;
import org.tensorflow.lite.examples.classification.data.JoinData;
import org.tensorflow.lite.examples.classification.data.JoinResponse;
import org.tensorflow.lite.examples.classification.kakaologin.kakao_main;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class SignUpActivity extends AppCompatActivity {
    private Button btnSignUp;
    private Button btnEmailTest;
    private EditText userName;
    private EditText userEmail;
    private EditText userPwd;
    private EditText userConfirm;
    private EditText userYear;
    private EditText userMonth;
    private ServiceApi service;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        userName = (EditText) findViewById(R.id.signName);
        userEmail = (EditText) findViewById(R.id.signEmail);
        userPwd = (EditText) findViewById(R.id.signPwd);
        userConfirm = (EditText) findViewById(R.id.signConfirm);
        btnSignUp = (Button) findViewById(R.id.BtnSignUp);
        userYear = (EditText) findViewById(R.id.signYear);
        userMonth = (EditText) findViewById(R.id.signMonth);
        btnEmailTest = (Button)  findViewById(R.id.emailTest);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        btnSignUp.setOnClickListener(view -> {
            attemptJoin();
        });

        btnEmailTest.setOnClickListener(view -> {
            if (!email.isEmpty()) {
                getChild(new ChildData(email));
            }
        });

        Button btn_Return = (Button) findViewById(R.id.btn_cancel);
        btn_Return.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                finish();}
        });

    }
    private void attemptJoin() {
        userName.setError(null);
        userEmail.setError(null);
        userPwd.setError(null);

        String name = userName.getText().toString();
        email = userEmail.getText().toString();
        String password = userPwd.getText().toString();
        String passwordConfirm = userConfirm.getText().toString();
        String nickName = userName.getText().toString();
        String birth = userYear.getText().toString() + "-" + userMonth.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 패스워드의 유효성 검사
        if (password.isEmpty()) {
            userPwd.setError("비밀번호를 입력해주세요.");
            focusView =userPwd;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            userPwd.setError("6자 이상의 비밀번호를 입력해주세요.");
            focusView = userPwd;
            cancel = true;
        } else if (!password.equals(passwordConfirm)) {
            userPwd.setError("비밀번호가 일치하지 않습니다.");
            focusView = userPwd;
            cancel = true;
        }
        // 이메일의 유효성 검사
        if (email.isEmpty()) {
            userEmail.setError("이메일을 입력해주세요.");
            focusView = userEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            userEmail.setError("@를 포함한 유효한 이메일을 입력해주세요.");
            focusView = userEmail;
            cancel = true;
        }

        // 이름의 유효성 검사
        if (name.isEmpty()) {
            userName.setError("이름을 입력해주세요.");
            focusView = userName;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            startJoin(new JoinData(email, password, name));
        }

    }

    private void startJoin(JoinData data) {
        service.userJoin(data).enqueue(new Callback<JoinResponse>() {
            @Override
            public void onResponse(Call<JoinResponse> call, retrofit2.Response<JoinResponse> response) {
                JoinResponse result = response.body();

                if (result.getResult()) {
                    email = userEmail.getText().toString();
                    Intent intent = new Intent(SignUpActivity.this, kakao_main.class);
                    SignUpActivity.this.startActivity(intent);
                    finish();
                }


            }

            @Override
            public void onFailure(Call<JoinResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "회원가입 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 에러 발생", t.getMessage());
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }


    //서버에 child 테이블 조회
    public void getChild(ChildData data) {
        service.getChild(data).enqueue(new Callback<List<ChildData>>() {
            @Override
            public void onResponse(Call<List<ChildData>> call, retrofit2.Response<List<ChildData>> response) {

                if (response.isSuccessful() && response.body() != null)
                {
                    Log.d("이메일 조회 응답",response.body().toString());
                    userEmail.setError("이미 가입된 이메일입니다");
                }


            }

            @Override
            public void onFailure(Call<List<ChildData>> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "이메일 조회 서버 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("이메일 조회 서버 에러 발생", t.getMessage());
            }
        });
    }
}
