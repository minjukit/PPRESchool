package org.tensorflow.lite.examples.classification.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.hamburger_toolbar;
import org.tensorflow.lite.examples.classification.test.DailyTest;
import org.tensorflow.lite.examples.classification.test.WrongTest;

public class Test extends hamburger_toolbar {

    Button btn_daily;
    Button btn_wrong;

    @Override
    public void make_hamburger() {
        super.make_hamburger();
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        make_hamburger();

        //단어장 테스트
        btn_daily = (Button) findViewById(R.id.btn_daily);
        btn_daily.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DailyTest.class);
                startActivity(intent);

            }
        });
        //오답노트 테스트
        btn_wrong = (Button)findViewById(R.id.btn_wrong);
        btn_wrong.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WrongTest.class);
                startActivity(intent);
            }
        });
    }


}
