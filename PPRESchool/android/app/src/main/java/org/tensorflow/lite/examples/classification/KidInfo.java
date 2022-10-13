package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.classification.data.ChildData;
import org.tensorflow.lite.examples.classification.data.JoinResponse;
import org.tensorflow.lite.examples.classification.data.VisitorData;
import org.tensorflow.lite.examples.classification.data.VocaResponse;
import org.tensorflow.lite.examples.classification.kakaologin.kakao_main;
import org.tensorflow.lite.examples.classification.leveltest.level_test_choice;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;


public class KidInfo extends AppCompatActivity {

    private Button kid_delete;
    private EditText kid_name;
    private EditText kid_year;
    private EditText kid_month;
    private Button kid_next;
    private RadioGroup radioGroup;
    private RadioButton male;
    private RadioButton female;
    public static Context kid_context;
    public String kidname;
    public String kidbirth;
    public int kid_gender; //여자면 true 1
    private ServiceApi service;
    public String id;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.kid_info);
        service = RetrofitClient.getClient().create(ServiceApi.class);
        kid_context = this;

        kid_delete = (Button) findViewById(R.id.kid_delete);
        kid_name = (EditText) findViewById(R.id.kid_name);
        kid_year = (EditText) findViewById(R.id.kid_year);
        kid_month = (EditText) findViewById(R.id.kid_month);
        kid_next = (Button) findViewById(R.id.kid_next);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        //계정주
        Bundle loginIntent = getIntent().getExtras();
        id = loginIntent.getString("id");

        String nameStr = kid_name.getText().toString();

        kid_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent delete = new Intent(getApplicationContext(), kakao_main.class);
                startActivity(delete);
            }
        });

        kid_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kid_info();
                insertChild(new ChildData(id, kidname, kid_gender, kidbirth, 0));
                Intent next = new Intent(getApplicationContext(), level_test_choice.class);
                startActivity(next);
            }
        });

        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.male:
                            kid_gender = 0;
                            break;

                        case R.id.female:
                            kid_gender = 1;
                            break;
                    }

                }

            });
        }
    }

    private void kid_info() {
        kidname = kid_name.getText().toString();
        kidbirth = kid_year.getText().toString() + "-" + kid_month.getText().toString();
    }

    //서버에 child 테이블에 아이정보추가
    private void insertChild(ChildData data) {
        service.userChild(data).enqueue(new Callback<JoinResponse>() {
            @Override
            public void onResponse(Call<JoinResponse> call, retrofit2.Response<JoinResponse> response) {
                JoinResponse result = response.body();

                if (result.getResult()) {
                    Intent next = new Intent(getApplicationContext(), level_test_choice.class);
                    next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(next);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<JoinResponse> call, Throwable t) {
                Toast.makeText(KidInfo.this, "정보추가 서버 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 정보추가 에러 발생", t.getMessage());
            }
        });
    }

    //서버에 visitor 테이블에 정보추가
    private void insertVisitor(VisitorData data) {
        service.saveVisitor(data).enqueue(new Callback<VocaResponse>() {
            @Override
            public void onResponse(Call<VocaResponse> call, retrofit2.Response<VocaResponse> response) {
                VocaResponse result = response.body();

            }

            @Override
            public void onFailure(Call<VocaResponse> call, Throwable t) {
                Log.e("visitor 정보추가 에러 발생", t.getMessage());
            }
        });
    }
}