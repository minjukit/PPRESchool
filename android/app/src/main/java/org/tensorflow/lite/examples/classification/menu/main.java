package org.tensorflow.lite.examples.classification.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.hamburger_toolbar;

//상속가능한지 확인하기
public class main extends hamburger_toolbar {

    Button btn_study;
    Button btn_review;
    Button btn_test;

    @Override
    public void make_hamburger() {
        super.make_hamburger();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        make_hamburger();

        //학습하기
        btn_study = (Button) findViewById(R.id.btn_study);
        btn_study.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Study.class);
                startActivity(intent);

            }
        });
        //복습하기
        btn_review = (Button) findViewById(R.id.btn_review);

        btn_review.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent_review = new Intent(getApplicationContext(), Review.class);
                startActivity(intent_review);
            }
        });

        //시험보기

        btn_test = (Button) findViewById(R.id.btn_test);

        btn_test.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent_review = new Intent(getApplicationContext(), Test.class);
                startActivity(intent_review);
            }
        });

    }

}