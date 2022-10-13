package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.classification.data.LoginData;
import org.tensorflow.lite.examples.classification.data.LoginResponse;
import org.tensorflow.lite.examples.classification.menu.main;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivty extends AppCompatActivity {

    private Button ppre_login_btn;
    private EditText ppre_id;
    private EditText ppre_pwd;
    private ServiceApi service;
    public static String id;
    public static Context login_con;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ppre_login);
        login_con = this;
        service = RetrofitClient.getClient().create(ServiceApi.class);

        ppre_id = (EditText)findViewById(R.id.ppre_id);
        ppre_pwd = (EditText)findViewById(R.id.ppre_pwd);
        ppre_login_btn = (Button)findViewById(R.id.ppre_login_btn);




        ppre_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });


    }


    //login server
    private void attemptLogin() {
        ppre_id.setError(null);
        ppre_pwd.setError(null);
        id = ppre_id.getText().toString();
        String pw = ppre_pwd.getText().toString();
        boolean cancel = false;
        View focusView = null;
        // 패스워드의 유효성 검사
        if (pw.isEmpty()) {
            ppre_pwd.setError("비밀번호를 입력해주세요.");
            focusView = ppre_pwd;
            cancel = true;
        } else if (!isPasswordValid(pw)) {
            ppre_pwd.setError("6자 이상의 비밀번호를 입력해주세요.");
            focusView = ppre_pwd;
            cancel = true;
        }
        // 이메일의 유효성 검사
        if (id.isEmpty()) {
            ppre_id.setError("이메일을 입력해주세요.");
            focusView = ppre_id;
            cancel = true;
        } else if (!isEmailValid(id)) {
            ppre_id.setError("@를 포함한 유효한 이메일을 입력해주세요.");
            focusView = ppre_id;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            startLogin(new LoginData(id, pw));
        }
    }

    private void startLogin(LoginData data) {
        service.userLogin(data).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                LoginResponse result = response.body();

                if (result.getResult()) {
                    Log.d("로그인 성공", "login success" + response.message());

                    if(result.getfLogin()){ // 첫로그인시
                        Intent kidIntent = new Intent(getApplicationContext(), KidInfo.class);
                        kidIntent.putExtra("id", id);
                        startActivity(kidIntent);

                    }else { //첫 로그인이 아니면
                        Intent mainIntent = new Intent(getApplicationContext(), main.class);
                        startActivity(mainIntent);
                    }

                }else{
                    Toast.makeText(LoginActivty.this, "아이디 또는 비밀번호 오류", Toast.LENGTH_SHORT).show();
                    Log.d("로그인 실패", "login error"+ response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivty.this, "로그인을 성공하지 못했습니다", Toast.LENGTH_SHORT).show();
                Log.e("로그인 에러 발생", t.getMessage());

            }
        });
    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }
}
