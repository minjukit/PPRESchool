package org.tensorflow.lite.examples.classification;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.tensorflow.lite.examples.classification.menu.Study;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Level3StudyActivity extends AppCompatActivity {

    private TextView textEn;
    private Button translationButton;
    private TextView textKr;
    private TextView showspeakresult;
    private String result;
    private ImageButton goSttBtn;
    private ImageButton ttsBtn;
    private ImageButton write_btn;
    ImageButton sentencettsBtn;
    private ImageView result_image;
    SpeechRecognizer mRecognizer;
    private ImageButton exit_btn;


    //문자열 비교할 녹음된 스트링
    private String myString;

    private ImageButton goTtsBtn;
    private String name;
    //딕셔너리
    Context context;
    String sentence;
    int level;

    Intent intentEn;
    Intent intent;

    private TextToSpeech textToSpeech;

    final int PERMISSION = 1;
    //network
    private ServiceApi service;
    int dailyNum = 1;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lv3_listen_speak);

        this.context = context;

        sentencettsBtn = (ImageButton) findViewById(R.id.sentencettsbtn);

        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        intentEn = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentEn.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intentEn.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        goSttBtn = (ImageButton) findViewById(R.id.goStt);


/*
        write_btn = (ImageButton) findViewById(R.id.write_Btn);

        write_btn.setOnClickListener(v -> {//pen 모양 버튼 눌렀을때

            go_write();
        });*/

        /*



        write_btn.setOnClickListener(v -> {//pen 모양 버튼 눌렀을때

            Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
            intent.putExtra("enText", name);
            //intent.putExtra("krText", textKr.getText().toString());
            //intent.putExtra("myLevel", level);
            startActivity(intent);

        });

        */


        goSttBtn.setOnClickListener(v -> {//입술 모양 버튼 눌렀을때
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            //mRecognizer.startListening(intent);
            mRecognizer.startListening(intentEn);
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //사용할 언어를 설정
                    int result = textToSpeech.setLanguage(Locale.US);
                    //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(Level3StudyActivity.this, "이 언어는 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        goSttBtn.setEnabled(true);
                        //음성 톤
                        textToSpeech.setPitch(1.0f);
                        Set<String> a=new HashSet<>();
                        a.add("female");
                        //here you can give male if you want to select male voice.
                        Voice v=new Voice("en-us-x-sfg#female_2-local",new Locale("en","US"),400,200,true,a);
                        Voice voice =new Voice("en-us-x-sfg#female_2-local",new Locale("en","US"),400,200,true,a);
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



        sentencettsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeechSentence();
            }
        });

        exit_btn =(ImageButton)findViewById(R.id.pause);

        exit_btn.setOnClickListener(v ->{

            exit(v);
        });

        Intent intent = getIntent();

        name = intent.getStringExtra("Objectname");
        textEn = (TextView)findViewById(R.id.tven);
        textKr = (TextView)findViewById(R.id.tvkr);
        showspeakresult = findViewById(R.id.showspeakresult);
        result_image = (ImageView)findViewById(R.id.resultimage);


        //문장이니까 글씨크기 줄이기
        Level3 level3 = new Level3();
        textEn.setTextSize(level3.setFloatSize());
        textKr.setTextSize(level3.setFloatSize());
        sentence = MyDictionaryRequest.sentence;

        textEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go_write();
            }
        });
        textKr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go_write();
            }
        });

        //단어 찾아서 하이라이트 표시하는 코드

        if(sentence.length() > 3){//사전으로부터 받아온 문장이 있다면

            SpannableString spannableString = new SpannableString(sentence);

            //문장 내에서 해당 단어의 위치 알아내기
            int start = sentence.indexOf(name);
            int end = start + name.length();

            //단어를 강조해준다 글자색, 볼드처리
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#ffff00")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            textEn.setText(spannableString);
        } else{
            textEn.setText(name);//sentence가 없다면 단어를 보여주고 스피커를 안보이게처리
            sentencettsBtn.setVisibility(View.GONE);}

        //oncreate하면서 바로 번역들어가도록
        new BackgroundTask().execute();



    }

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer>{
        @Override
        protected Integer doInBackground(Integer... integers) {
            StringBuilder output = new StringBuilder();
            //naver api app id/secret
            String clientId = "ucCPDClWHqvVvhvIwJf9";
            String clientSecret = "jitlzZD5Vq";
            try {
                //번역문을 utf-8로 인코딩
                String text = URLEncoder.encode(textEn.getText().toString());
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";

                //파파고 api와 연결
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                //영어를 한글로 번역 후 가져온 텍스트를 파라미터로
                String postParams = "source=en&target=ko&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();

                //번역 결과를 받아옴
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode == 200){
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else{
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                while ((inputLine =br.readLine()) != null) { //가져온게 있다면
                    output.append(inputLine);
                }
                br.close();

            }catch (Exception e){
                Log.e("SampleHTTP", "Exception in processing response.",e);
                e.printStackTrace();
            }

            result = output.toString();
            return null;
        }
        protected void onPostExecute(Integer a){
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            if(element.getAsJsonObject().get("errorMessage") != null){
                Log.e("번역오류", "번역오류가 발생" + "[오류코드: "+element.getAsJsonObject().get("errorCode").getAsString() + "]");
            }else if (element.getAsJsonObject().get("message") != null){
                //번역결과 출력
                textKr.setText(element.getAsJsonObject().get("message").getAsJsonObject().get("result").getAsJsonObject().get("translatedText").getAsString());
            }
        }
        protected void onPreExecute(){

        }


    }

    private void Speech() {
        String text = name;
        // QUEUE_FLUSH: Queue 값을 초기화한 후 값을 넣는다.
        // QUEUE_ADD: 현재 Queue에 값을 추가하는 옵션이다.
        // API 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            // API 20
        else
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }

    private void SpeechSentence(){

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


    @Override
    protected void onStop() {
        super.onStop();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }


    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            //Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
            Toast.makeText(Level3StudyActivity.this, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
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

            if(myString.equals(name)){

                //Toast.makeText(MainActivity.this, "잘하셨어요!!!", Toast.LENGTH_SHORT).show();
                //3초간 정답 이미지 보여주기
                result_image.setImageResource(R.drawable.sttgood);
                result_image.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        result_image.setVisibility(View.INVISIBLE);
                        //정답이면 쓰기학습으로 넘어간다
                        Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
                        intent.putExtra("enText", name);
                        //intent.putExtra("krText", textKr.getText().toString());
                        //intent.putExtra("myLevel", level);
                        startActivity(intent);
                    }
                }, 3000);


            } else {
                //3초간 틀림 이미지 보여주기
                result_image.setImageResource(R.drawable.sttbad);
                result_image.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        result_image.setVisibility(View.INVISIBLE);
                    }
                }, 3000);
            }}

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }


    };

    public void go_write(){
        Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
        intent.putExtra("enText", name);
        //intent.putExtra("krText", textKr.getText().toString());
        //intent.putExtra("myLevel", level);
        startActivity(intent);
    }

    public void exit (View view) //다이얼로그 띄워서 학습 종료하기
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

