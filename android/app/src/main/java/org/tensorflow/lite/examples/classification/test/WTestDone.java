package org.tensorflow.lite.examples.classification.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.examples.classification.LoginActivty;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.data.JoinResponse;
import org.tensorflow.lite.examples.classification.data.ScoreData;
import org.tensorflow.lite.examples.classification.menu.Study;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;

public class WTestDone extends Activity {

    private int myscore;
    private TextView dtscore;
    Button stop;
    //network
    private ServiceApi service;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_done);
        //network
        service = RetrofitClient.getClient().create(ServiceApi.class);

        dtscore = findViewById(R.id.dt_score);

        Intent intent = getIntent();
        myscore = intent.getIntExtra("score", 0);
        int testScore = myscore*10;
        dtscore.setText("내 점수 : "+testScore+"점");


        //아이디는 로그인 액티비티꺼 쓰고
        String id = ((LoginActivty)LoginActivty.login_con).id;
        savewScore(new ScoreData(id,String.valueOf(testScore),"")); //날짜는 서버에서

        stop = findViewById(R.id.stopBtn);

        stop.setOnClickListener(v -> {
            Intent intent2 = new Intent(getApplicationContext(), Study.class);
            startActivity(intent2);
            finish();

        });



    }




    //db 저장위한 레트로핏 통신
    private void savewScore(ScoreData data) {
        service.saveWScore(data).enqueue(new Callback<JoinResponse>() {

            @Override
            public void onResponse(Call<JoinResponse> call, retrofit2.Response<JoinResponse> response) {

                JoinResponse result = response.body();

                if (result.getResult()) {
                    Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<JoinResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"에러에러에러", Toast.LENGTH_SHORT).show();
                Log.e("에러 발생", t.getMessage() + t.toString() + t.getLocalizedMessage());

            }

        });
    }




}