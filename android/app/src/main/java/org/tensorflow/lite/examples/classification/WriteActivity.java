package org.tensorflow.lite.examples.classification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.classification.menu.Study;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class WriteActivity extends AppCompatActivity{



    private static final String TAG = "MLKDI.Activity";

    @VisibleForTesting
    final StrokeManager strokeManager = new StrokeManager();
    Context wContext;


    //가져온 문장 스트링과 보여줄 텍스트뷰
    private String enString;
    private String krString;
    private String result;
    private String enLowString;
    private int level;
    int level_num;
    StatusTextView statusTextView;
    String phonics;
    String data;
    int record = 0;
    int vocanum;

    TextView textView;
    TextView textviewKr;
    ImageView resultView;
    ImageButton exit_btn;
    ImageButton next_btn;
    //check()에서 쓰일 bool

    boolean got_intent = false;

    Level1 level1 = new Level1();

    Object[] keys; //key값 담을 배열 = a,b,c...

    //level 2에서 단어와 한글 뜻 저장할 변수
    ArrayList<String> word = new ArrayList<String>();
    ArrayList<String> mean = new ArrayList<String>();

    int mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        level = ((hamburger_toolbar)hamburger_toolbar.hamContext).level;
        mode = 2;
        level_num = hamburger_toolbar.level;

        exit_btn =(ImageButton)findViewById(R.id.pause);
        next_btn = (ImageButton)findViewById(R.id.next);


        wContext = getApplicationContext();
        //textviewKr =(TextView)findViewById(R.id.krTextview);
        textView = (TextView) findViewById(R.id.enTextview);
        resultView = (ImageView)findViewById(R.id.write_result);

        //드로잉뷰
        DrawingView drawingView = findViewById(R.id.drawing_view);
        statusTextView = findViewById(R.id.status_text_view);
        drawingView.setStrokeManager(strokeManager);
        statusTextView.setStrokeManager(strokeManager);

        strokeManager.setStatusChangedListener(statusTextView);
        strokeManager.setStatusChangedListener(statusTextView);
        strokeManager.setContentChangedListener(drawingView);
        strokeManager.setClearCurrentInkAfterRecognition(true);
        strokeManager.setTriggerRecognitionAfterInput(false);


        //언어 설정 US로 통일
        String languageCode = "en";
        strokeManager.setActiveModel(languageCode);
        //알아서 영어모델 다운로드
        strokeManager.download();
        strokeManager.reset();

        //다음, 종료 버튼 모드마다 다르게 보이게
        //exit_btn.setVisibility(View.INVISIBLE);
        next_btn.setVisibility(View.INVISIBLE);

        //intent로 가져온 문장 설정
        Bundle writeIntent = getIntent().getExtras();
        level = writeIntent.getInt("myLevel");
        enString = writeIntent.getString("enText");

        try{
            int mode = writeIntent.getInt("mode");
            //Toast.makeText(this, String.valueOf(mode), Toast.LENGTH_SHORT).show();

            if(mode == 1){ //데일리테스트아니면 중지 넥스트 안보이도록
                exit_btn.setVisibility(View.VISIBLE);
                next_btn.setVisibility(View.INVISIBLE);
            }else if (mode ==2){
                exit_btn.setVisibility(View.VISIBLE);
                next_btn.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){ }




        if(enString != null){

            got_intent = true;


            if(level_num == 1){
                enLowString = writeIntent.getString("enLowText");
                textView.setText(enString);
                textView.append("  "+ enLowString);
            } else {
                textView.setText(enString);
            }}else{


            if(level_num == 3){ //level3
                //글씨크기 조정
                Level3 level3 = new Level3();
                //textviewKr.setTextSize(level3.setFloatSize());
                textView.setTextSize(level3.setFloatSize());

            }else if(level_num == 1){//level1
                Level1 level1 = new Level1();
                //textviewKr.setTextSize(level1.setFloatSize());
                textView.setTextSize(level1.setFloatSize());

            }else{ }

            if (level_num == 1) {//level1 새싹 단계일 때


                level1.setPhonicsMap();


                //hashmap의 key값들을 keys배열에 저장

                keys = level1.phonicsMap.keySet().toArray(); //key값 담을 배열 = a,b,c...


                // key 반환 (key = a,b,c...)
                phonics = keys[record].toString();
                enString = phonics;
                textView.setText(phonics);
                textView.append("  "+ phonics.toLowerCase());


            } else { //level2 & 나머지


                //String data = null; //단어 저장할 변수
                InputStream inputStream = getResources().openRawResource(R.raw.voca);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                int i;

                try {
                    i = inputStream.read();
                    while (i != -1) {    //텍스트 데이터 읽어오기기
                        byteArrayOutputStream.write(i);
                        i = inputStream.read();
                    }
                    data = new String(byteArrayOutputStream.toByteArray());

                    String t = "";
                    //문자열 분해
                    for (int k = 0; k < data.length(); k++) {
                        if (!String.valueOf('|').equals(String.valueOf(data.charAt(k)))
                                && !String.valueOf('/').equals(String.valueOf(data.charAt(k))))
                            t += data.charAt(k);

                        else if (String.valueOf('|').equals(String.valueOf(data.charAt(k)))) {
                            word.add(t);
                            t = "";
                        } else if (String.valueOf('/').equals(String.valueOf(data.charAt(k)))) {
                            mean.add(t);
                            t = "";
                        }
                    }
                    inputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                vocanum = word.size();

                //무작위로 단어 추출
                Random random = new Random();
                int r = random.nextInt(vocanum);

                enString = word.get(r).trim();

                textView.setText(enString);
            }

        }



        exit_btn.setOnClickListener(v ->{

            exit(v);
        });



        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(level_num == 1){
                    startlevel1();
                }else{
                    startlevel2();
                }
            }
        });


    }



    public void startlevel1(){

        record += 1;

        phonics = keys[record].toString();
        enString = phonics;
        textView.setText(phonics);
        textView.append("  "+ phonics.toLowerCase());


    }


    public void startlevel2(){
        Random random = new Random();
        int r = random.nextInt(vocanum);

        enString = word.get(r).trim();

        textView.setText(enString);

    }




    public void recognizeClick(View v) {

        //statusTextView.onStatusChanged();
        //statusview 에 글자 띄우기
        strokeManager.recognize();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                check();
            }
        }, 2000);

    }


    public void check() {
        //result가 내가 쓴 글씨
        result = strokeManager.getStatus();
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

        //level 1은 대소문자 구분없이 맞도록
        if (result.equals(enString)){
            rightResult();
        } else {
            if (level_num == 1){
                if(result.equals(enLowString)) {
                    rightResult();
                } else if(result.equals(enString +" " + enLowString)) {
                    rightResult();
                }else if(result.equals(phonics)){rightResult();}
                else if(result.equals(phonics.toLowerCase())){rightResult();}
                else { wrongResult();}
            } else{
                wrongResult();
            }
        }
    }


    public void clearClick (View v){ //드로잉뷰 리셋
        strokeManager.reset();
        DrawingView drawingView = findViewById(R.id.drawing_view);
        drawingView.clear();
    }

    //정답 이미지 보여주는 함수
    private void rightResult(){
        //3초간 정답 이미지 보여주기
        resultView.setImageResource(R.drawable.sttgood);
        resultView.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resultView.setVisibility(View.INVISIBLE);
            }
        }, 3000);
        if(!got_intent){
            if(level_num == 1){
                startlevel1();
            }else{startlevel2();}}
    }

    //오답 이미지 보여주는 함수
    private void wrongResult(){
        //3초간 틀림 이미지 보여주기
        resultView.setImageResource(R.drawable.sttbad);
        resultView.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resultView.setVisibility(View.INVISIBLE);
            }
        }, 3000);
    }



    //다이얼로그 띄워서 학습 종료하기
    public void exit (View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("학습 종료").setMessage("이대로 학습을 끝낼까요?");

        builder.setPositiveButton("끝내기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Study.class);
                startActivity(intent);
                finish();

            }
        });



        builder.setNeutralButton("학습 계속하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Toast.makeText(getApplicationContext(), "Neutral Click", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }
}