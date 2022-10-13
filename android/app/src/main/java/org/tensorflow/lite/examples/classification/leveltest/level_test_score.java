package org.tensorflow.lite.examples.classification.leveltest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.classification.KidInfo;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.data.ChildData;
import org.tensorflow.lite.examples.classification.data.JoinResponse;
import org.tensorflow.lite.examples.classification.menu.main;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;

public class level_test_score extends AppCompatActivity {

    TextView level_name;
    Button start_next;

    public int level = 0;
    public static Context levelcontext;
    private ServiceApi service;
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_test_score);
        levelcontext = this;


        service = RetrofitClient.getClient().create(ServiceApi.class);
        id = ((KidInfo)KidInfo.kid_context).id;

        int total = getIntent().getIntExtra("correct",0);
        level_name = (TextView) findViewById(R.id.level_name);

        start_next = (Button) findViewById(R.id.start_next);

        if(total > 4) {
            level_name.setText("꽃");
            level=3;
            insertLevel(new ChildData(id,3));
        } else if (total < 3) {
            level_name.setText("씨앗");
            level=1;
            insertLevel(new ChildData(id,1));
        } else {
            level_name.setText("새싹");
            level=2;
            insertLevel(new ChildData(id,2));
        }

        start_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), main.class);
                startActivity(intent);
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

                }

            }

            @Override
            public void onFailure(Call<JoinResponse> call, Throwable t) {
                Toast.makeText(level_test_score.this, "정보추가 서버 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("레벨 정보추가 에러 발생", t.getMessage());
            }
        });
    }
}
