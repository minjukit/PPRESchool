package org.tensorflow.lite.examples.classification.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.hamburger_toolbar;
import org.tensorflow.lite.examples.classification.voca.VocaMainActivity;
import org.tensorflow.lite.examples.classification.wrong.WrongMainActivity;

public class Review extends hamburger_toolbar {

    @Override
    public void make_hamburger() {
        super.make_hamburger();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        make_hamburger();

        Button btn_voca = (Button) findViewById(R.id.go_voca_btn);
        btn_voca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vocain = new Intent(getApplicationContext(), VocaMainActivity.class);
                startActivity(vocain);

            }
        });
        Button btn_wrong = (Button) findViewById(R.id.go_wrong_btn);
        btn_wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wrongin = new Intent(getApplicationContext(), WrongMainActivity.class);
                startActivity(wrongin);
            }
        });

    }

}