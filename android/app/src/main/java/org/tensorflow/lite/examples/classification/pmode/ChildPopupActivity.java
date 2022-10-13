package org.tensorflow.lite.examples.classification.pmode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.tensorflow.lite.examples.classification.LoginActivty;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.data.ChildData;
import org.tensorflow.lite.examples.classification.data.JoinResponse;
import org.tensorflow.lite.examples.classification.hamburger_toolbar;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;

public class ChildPopupActivity extends Activity {

    Button c_delete;
    Button c_change;
    EditText c_name1;
    String c_name;
    RadioGroup radioGroup;
    int c_level;
    private ServiceApi service;
    private String id;
    private String nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.p_mode_child_pop_up);

        service = RetrofitClient.getClient().create(ServiceApi.class);
        id = ((LoginActivty) LoginActivty.login_con).id;

        c_delete = (Button) findViewById(R.id.c_delete);
        c_change = (Button) findViewById(R.id.c_change);
        c_name1 = (EditText) findViewById(R.id.c_name);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        nick = ((hamburger_toolbar) hamburger_toolbar.hamContext).nick;

        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.seed:
                            c_level = 1;
                            break;
                        case R.id.plant:
                            c_level = 2;
                            break;
                        case R.id.flower:
                            c_level = 3;
                            break;
                    }
                }
            });
        }


        //수정버튼
        c_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                c_name = c_name1.getText().toString();
                if (c_name.isEmpty()) {
                    c_name = nick;
                } else { //있으면 가져오기
                }
                updateChild(new ChildData(id, c_level, c_name));
                finish();
                Intent i = new Intent(getApplicationContext(), PmodeActivity.class);
                startActivity(i);
            }
        });

        //취소버튼 누르면 꺼짐
        c_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //서버에 child 테이블에 정보변경
    private void updateChild(ChildData data) {
        service.updateChild(data).enqueue(new Callback<JoinResponse>() {
            @Override
            public void onResponse(Call<JoinResponse> call, retrofit2.Response<JoinResponse> response) {
                JoinResponse result = response.body();

                if (result.getResult()) {
                    Intent intent = new Intent(getApplicationContext(), PmodeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<JoinResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "정보변경 서버 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("정보변경 에러 발생", t.getMessage());
            }
        });
    }

}