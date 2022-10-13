package org.tensorflow.lite.examples.classification.pmode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.menu.main;

import java.util.Random;

public class PmodePopUpActivity extends Activity {

    TextView p_result;

    TextView p_num1;
    TextView p_num2;

    Button p_correct;
    Button p_delete;

    String user_writing_pwd;

    int p_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.p_mode_pop_up);

        //결과
        p_result = findViewById(R.id.p_result);

        //문제
        p_num1 = findViewById(R.id.p_num1);
        p_num2 = findViewById(R.id.p_num2);

        //확인 버튼
        p_correct = findViewById(R.id.p_correct);
        p_delete = findViewById(R.id.p_delete);

        //랜덤숫자발생
        Random random = new Random();
        Random random2 = new Random();
        //문제
        int pnum1 = random.nextInt(10);
        int pnum2 = random2.nextInt(10);
        p_num1.setText(String.valueOf(pnum1));
        p_num2.setText(String.valueOf(pnum2));

        //문제 답
        int pwd_result1 = pnum1 + pnum2;
        String pwd_result = String.valueOf(pwd_result1);

        p_correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_writing_pwd = p_result.getText().toString();

                if(pwd_result.equals(user_writing_pwd)) {
                    p_pwd = 0;
                } else {
                    p_pwd = 1;
                }
                switch (p_pwd) {
                    case 0:
                        Intent i = new Intent(getApplicationContext(), PmodeActivity.class);
                        startActivity(i);
                        break;
                    case 1:
                        Intent i2 = new Intent(getApplicationContext(), main.class);
                        Toast.makeText(getApplicationContext(),"비밀번호 오류입니다.",Toast.LENGTH_SHORT).show();
                        startActivity(i2);
                        break;
                }
            }
        });



        p_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), main.class);
                startActivity(i);
            }
        });

    }

    //확인 버튼 클릭
    public void mOnClose(View v) {
        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

}
