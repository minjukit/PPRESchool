package org.tensorflow.lite.examples.classification;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.tensorflow.lite.examples.classification.data.VocaData;
import org.tensorflow.lite.examples.classification.data.VocaResponse;
import org.tensorflow.lite.examples.classification.menu.Study;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;

public class ListenSpeak extends Activity {

    int record = 0;


    int study_check=0;

    final int PERMISSION = 1;

    private TextView textEn;
    private TextView textEnLow;
    private TextView textKr;
    private TextView showspeakresult;
    private TextView progress_number;
    private String result;
    private ImageButton goSttBtn;
    private ImageButton write_btn;
    private ImageButton exit_btn;
    private ImageButton next_btn;
    //private ImageButton writeBtn;
    private ImageButton ttsBtn;
    ImageButton sentencettsBtn;

    private ImageView result_image;
    SpeechRecognizer mRecognizer;

    //문자열 비교할 녹음된 스트링
    private String myString;

    private ImageButton goTtsBtn;
    private String name;
    //딕셔너리
    Context context;
    String sentence;
    String phonics;

    Intent intentEn;
    Intent intent;


    int vocanum = 0;
    String data;

    int wrong = 0; //3회 오답이면 다음학습으로 넘어가도록 체크하는 변수



    private TextToSpeech textToSpeech;

    Level1 level1 = new Level1();

    Object[] keys; //key값 담을 배열 = a,b,c...

    //level 2에서 단어와 한글 뜻 저장할 변수
    ArrayList<String> word = new ArrayList<String>();
    ArrayList<String> mean = new ArrayList<String>();
    //network
    private ServiceApi service;
    int level_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_speak);
        //network
        service = RetrofitClient.getClient().create(ServiceApi.class);

        level_num = hamburger_toolbar.level;



        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        textEn = findViewById(R.id.tven);
        textKr = findViewById(R.id.tvkr);
        textEnLow = findViewById(R.id.tvenLow);
        showspeakresult = findViewById(R.id.showspeakresult);
        result_image = (ImageView) findViewById(R.id.resultimage);
        next_btn = (ImageButton)findViewById(R.id.next);
        progress_number = (TextView)findViewById(R.id.progress_number);


        if (level_num == 1) {//level1 새싹 단계일 때


            level1.setPhonicsMap();


            //hashmap의 key값들을 keys배열에 저장

            keys = level1.phonicsMap.keySet().toArray(); //key값 담을 배열 = a,b,c...


            // key 반환 (key = a,b,c...)
            phonics = keys[record].toString();
            textEn.setText(phonics);
            textEnLow.setText(phonics.toLowerCase());


            // value 반환 (value = 에이, 비, 씨...)
            textKr.setText(level1.getPhonicsMap().get(keys[record]));

            textEn.setTextSize(level1.setFloatSize()); //폰트 크기 조정


            //String[] value = new String[26]; //value 담을 배열 = 에이,비,씨...

            /*Iterator<Map.Entry<String, String>> entries = level1.phonicsMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry<String, String> entry = entries.next();

            }*/


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

            textEn.setText(word.get(r).trim());
            textKr.setText(mean.get(r));

        }


        intentEn = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentEn.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intentEn.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");


        goSttBtn = (ImageButton) findViewById(R.id.goStt);


        goSttBtn.setOnClickListener(v -> {//입술 모양 버튼 눌렀을때
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            //mRecognizer.startListening(intent);
            mRecognizer.startListening(intentEn);
        });
        textEn.setOnClickListener(v -> {//textview 클릭하면 write activity로 이동
            open_write();
        });
        textKr.setOnClickListener(v -> {//textview 클릭하면 write activity로 이동
            open_write();
        });
        textEnLow.setOnClickListener(v -> {//textview 클릭하면 write activity로 이동
            open_write();
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //사용할 언어를 설정
                    int result = textToSpeech.setLanguage(Locale.US);
                    //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(ListenSpeak.this, "이 언어는 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        goSttBtn.setEnabled(true);
                        //음성 톤
                        textToSpeech.setPitch(1.0f);
                        Set<String> a = new HashSet<>();
                        a.add("female");
                        //here you can give male if you want to select male voice.
                        Voice v = new Voice("en-us-x-sfg#female_2-local", new Locale("en", "US"), 400, 200, true, a);
                        Voice voice = new Voice("en-us-x-sfg#female_2-local", new Locale("en", "US"), 400, 200, true, a);
                        textToSpeech.setVoice(v);
                        // textToSpeech.setVoice(en-us-x-sfg # male_1-local);
                        //읽는 속도
                        textToSpeech.setSpeechRate(0.8f);
                    }
                }
            }
        });


        ttsBtn = (ImageButton) findViewById(R.id.ttsbtn);

        ttsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Speech();
            }
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



        exit_btn =(ImageButton)findViewById(R.id.pause);

        exit_btn.setOnClickListener(v ->{

            exit(v);
        });




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









    private void Speech() {
        String text = textEn.getText().toString();
        // QUEUE_FLUSH: Queue 값을 초기화한 후 값을 넣는다.
        // QUEUE_ADD: 현재 Queue에 값을 추가하는 옵션이다.
        // API 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            // API 20
        else
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            //Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
            Toast.makeText(ListenSpeak.this, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {


        }



        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < matches.size(); i++) {
                showspeakresult.setText(matches.get(i));
            }
            myString = showspeakresult.getText().toString();

            String low_myString = myString.toLowerCase();
            String low_enstring = textEn.getText().toString().toLowerCase();



            /*
            if(study_check == 5){

                finish();
                new Intent(getApplicationContext(), StudyDone.class);
                startActivity(intent);}*/

            if(low_myString.equals(low_enstring)){
                //db저장 쿼리보내기
                String enString = textEn.getText().toString();
                String krString = textKr.getText().toString();
                String id= ((LoginActivty)LoginActivty.login_con).id;
                saveVoca(new VocaData(id, enString, krString, "2021-09-26")); //어차피 날짜는 서버에서 저장함 이거랑 노상관
                //3초간 정답 이미지 보여주기
                study_check += 1;
                result_image.setImageResource(R.drawable.sttgood);
                result_image.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        result_image.setVisibility(View.INVISIBLE);
                    }
                }, 3000);
                if(study_check == 5){
                    Intent intent2 = new Intent(getApplicationContext(), StudyDone.class);
                    startActivity(intent2);

                    finish();


                }else {
                    if (level_num == 1) {
                        startlevel1();
                    } else {
                        startlevel2();
                    }
                }


            } else {
                if(wrong == 2){

                    if(level_num == 1){

                        startlevel1();


                    }else{startlevel2();}

                    //3초간 넘어간다는 이미지 보여주기

                    result_image.setImageResource(R.drawable.next_word);
                    result_image.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            result_image.setVisibility(View.INVISIBLE);
                        }
                    }, 3000);

                }else{
                    //3초간 틀림 이미지 보여주기
                    wrong += 1;
                    result_image.setImageResource(R.drawable.sttbad);
                    result_image.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            result_image.setVisibility(View.INVISIBLE);
                        }
                    }, 3000);

                    //progress_number.setText(String.valueOf(wrong));
                }


            }

            // onDestroy();


        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }


    };

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (textToSpeech != null) { textToSpeech.stop(); textToSpeech.shutdown(); }

        //unbindService();


    }

    public void startlevel1(){

        record += 1;//level1 index 넘기기

        // key 반환 (key = a,b,c...)
        phonics=keys[record].toString();
        textEn.setText(phonics);
        textEnLow.setText(phonics.toLowerCase());


        // value 반환 (value = 에이, 비, 씨...)
        textKr.setText(level1.getPhonicsMap().get(keys[record]));

        wrong = 0;

        progress_number.setText("현재 진도 : "+study_check+ " / 5" );

    }

    public void startlevel2(){

        //무작위로 단어 추출
        Random random = new Random();
        int r = random.nextInt(vocanum);

        textEn.setText(word.get(r).trim());
        textKr.setText(mean.get(r));

        wrong = 0;
        // study_check += 1;
        progress_number.setText("현재 진도 : "+study_check+ " / 5" );

    }

    public void open_write(){

        Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
        if(level_num == 1){
            intent.putExtra("enLowText", textEnLow.getText().toString());
        }
        intent.putExtra("myLevel", level_num);
        intent.putExtra("enText", textEn.getText().toString());
        intent.putExtra("mode",1);
        startActivity(intent);

    }



    //db 저장위한 레트로핏 통신
    private void saveVoca(VocaData data) {
        service.userVoca(data).enqueue(new Callback<VocaResponse>() {

            @Override
            public void onResponse(Call<VocaResponse> call, retrofit2.Response<VocaResponse> response) {
                //VocaResponse result = response.body();
                VocaResponse result = response.body();

                if (result.getResult()) {
                    Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<VocaResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"에러에러에러", Toast.LENGTH_SHORT).show();
                Log.e("에러 발생", t.getMessage() + t.toString() + t.getLocalizedMessage());

            }

        });
    }

}