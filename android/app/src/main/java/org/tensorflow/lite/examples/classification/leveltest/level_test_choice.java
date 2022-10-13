package org.tensorflow.lite.examples.classification.leveltest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.classification.KidInfo;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.data.ChildData;
import org.tensorflow.lite.examples.classification.data.JoinResponse;
import org.tensorflow.lite.examples.classification.kakaologin.kakao_main;
import org.tensorflow.lite.examples.classification.menu.main;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;

public class level_test_choice extends AppCompatActivity {

    Button seed_level;
    Button plant_level;
    Button flower_level;
    Button level_test_button;
    Button delete_button;
    public int level2 = 0;
    public static Context levelcontext2;
    private ServiceApi service;
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_test_choice);

        service = RetrofitClient.getClient().create(ServiceApi.class);
        levelcontext2 = this;
        //계정 이메일아이디 가져오기
        id = ((KidInfo)KidInfo.kid_context).id;

        seed_level = (Button)findViewById(R.id.seed_level);
        plant_level = (Button)findViewById(R.id.plant_level);
        flower_level = (Button)findViewById(R.id.flower_level);
        level_test_button = (Button)findViewById(R.id.level_test_button);
        delete_button = (Button)findViewById(R.id.delete_button);

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), kakao_main.class);
                startActivity(i);
            }
        });

        seed_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level2 = 1;
                insertLevel(new ChildData(id,1));
            }
        });

        plant_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level2 = 2;
                insertLevel(new ChildData(id,2));
            }
        });

        flower_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level2 = 3;
                insertLevel(new ChildData(id,3));
            }
        });



        level_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), level_test_tutorial.class);
                startActivity(i);
            }
        });

    }

    //서버에 child 테이블에 레벨추가
    private void insertLevel(ChildData data) {
        service.userLevel(data).enqueue(new Callback<JoinResponse>() {
            @Override
            public void onResponse(Call<JoinResponse> call, retrofit2.Response<JoinResponse> response) {
                JoinResponse result = response.body();

                if (result.getResult()) {
                    Intent intent = new Intent(getApplicationContext(), main.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<JoinResponse> call, Throwable t) {
                Toast.makeText(level_test_choice.this, "정보추가 서버 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("레벨 정보추가 에러 발생", t.getMessage());
            }
        });
    }


}
