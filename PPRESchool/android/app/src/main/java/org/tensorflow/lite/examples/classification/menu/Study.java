package org.tensorflow.lite.examples.classification.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.tensorflow.lite.examples.classification.GamePopUpActivity;
import org.tensorflow.lite.examples.classification.MyDictionaryRequest;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.env.MyCamera;
import org.tensorflow.lite.examples.classification.hamburger_toolbar;

public class Study extends hamburger_toolbar {

    int level;

    //dictionary 변수
    private String worden;
    private String url;
    Context context;
    String data;
    public String name;

    private TextView test;//삭제할거

    @Override
    public void make_hamburger() {
        super.make_hamburger();
    }
    @Override

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.study);

        make_hamburger();



        if(level == 3){

            MyDictionaryRequest myDictionaryRequest = new MyDictionaryRequest(this);
            myDictionaryRequest.execute(url);}


        Button btn_listen_speak = (Button) findViewById(R.id.btn_listen_speak);
        btn_listen_speak.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if(level == 3){


                    Intent intent = new Intent(getApplicationContext(),
                            ReadAndWrite.class);
                    //intent.putExtra("Objectname", name );

                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), ReadAndWrite.class);
                    startActivity(intent);}

            }
        });

        Button btn_img_classification = (Button) findViewById(R.id.btn_img_classification);
        btn_img_classification.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent google_i = new Intent(getApplicationContext(), MyCamera.class);
                startActivity(google_i);

            }
        });
        Button btn_game = (Button) findViewById(R.id.btn_fairy_tale);
        btn_game.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), GamePopUpActivity.class);
                startActivity(i);
            }
        });

    }

    /*public void requestfreeDicApi(View v){


        if(level == 3){
            Intent intent = new Intent(getApplicationContext(),
                    Level3StudyActivity.class);
            intent.putExtra("Objectname", worden );

            startActivity(intent);

        }

        else{

            Intent intent = new Intent(getApplicationContext(),
                    ListenSpeak.class);
            //intent.putExtra("Objectname", objectname );

            startActivity(intent);
        }
    }*/






}
