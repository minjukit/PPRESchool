package org.tensorflow.lite.examples.classification.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.classification.LoginActivty;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.data.VocaData;
import org.tensorflow.lite.examples.classification.data.VocaResponse;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyTest extends AppCompatActivity {

    int level;
    int[] numbers = new int[10]; //문제로 출제할 단어의 index 저장
    String[] words = new String[4]; //문제로 낼 단어들 저장
    String[] target_en = new String[11]; //서버에서 가져오는 10개의 단어들
    String[] target_kr = new String[11]; //서버에서 가져오는 10개의 단어들 - 한국어 뜻
    String data;
    private String word;
    private String example1;
    private String example2;
    private String example3;
    private String example4;
    private Button a1;
    private Button a2;
    private Button a3;
    private Button a4;
    private TextView question;
    private ImageView result_image;
    private int vocanum;

    private TextView current_sc;

    private int correct_num = 0;
    private int wrong_num = 0;
    private int record = 1;

    String eng_correct;
    String kr_correct;

    private TextView tv1;
    private TextView tv2;//둘다임시변수

    //단어와 한글 뜻 저장할 변수
    ArrayList<String> wordarr = new ArrayList<String>();
    ArrayList<String> meanarr = new ArrayList<String>();

    private ServiceApi service;


    List<VocaData> test_list = new ArrayList<>();
    VocaData VocaData;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_test);



        service = RetrofitClient.getClient().create(ServiceApi.class);


        a1 = findViewById(R.id.a1);
        a2 = findViewById(R.id.a2);
        a3 = findViewById(R.id.a3);
        a4 = findViewById(R.id.a4);

        question = findViewById(R.id.question);

        result_image = findViewById(R.id.resultimage);

        current_sc = findViewById(R.id.current_score_d);


        //eng_correct = test_list.get(0).toString();
        //kr_correct = test_list.get(0).toString();


        InputStream inputStream = getResources().openRawResource(R.raw.voca);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;

        try{
            i = inputStream.read();
            while (i != -1){    //텍스트 데이터 읽어오기기
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            data = new String(byteArrayOutputStream.toByteArray());

            String t = "";
            //문자열 분해
            for(int k=0;k<data.length();k++) {
                if (!String.valueOf('|').equals(String.valueOf(data.charAt(k)))
                        && !String.valueOf('/').equals(String.valueOf(data.charAt(k))))
                    t += data.charAt(k);

                else if (String.valueOf('|').equals(String.valueOf(data.charAt(k)))) {
                    wordarr.add(t);
                    t = "";
                } else if (String.valueOf('/').equals(String.valueOf(data.charAt(k)))) {
                    meanarr.add(t);
                    t = "";
                }
            }
            inputStream.close();

        }catch (IOException e){
            e.printStackTrace();           }

        vocanum = wordarr.size();




        for(int k = 0; k < numbers.length -2 ; k++){

            numbers[k] = (int)(Math.random() * vocanum ) ; //4개의 index 랜덤추출


            for(int j = 0; j < k; j ++){
                if(numbers[k] == numbers[j]){
                    k--; // 중복값이면 한칸 앞으로 가서 다시 추출
                    break;
                }
            }
        }

        getTest(0); //처음 한번만 gettest 호출








    }

    //버튼 클릭시 동작할 함수(문제 채점)
    public void check(View v){

        String choice; // 사용자가 고른 답

        current_sc.setText( record + "/ 10 ");

        choice = (((Button)v).getText()).toString();

        if(choice == target_en[record-1]){
            correct();
            correct_num += 1;


            for(int k = 0; k < numbers.length -2 ; k++){

                numbers[k] = (int)(Math.random() * vocanum ) ; //4개의 index 랜덤추출


                for(int j = 0; j < k; j ++){
                    if(numbers[k] == numbers[j]){
                        k--; // 중복값이면 한칸 앞으로 가서 다시 추출
                        break;
                    }
                }
            }

            //getTest(1);
            //setquestion(record);



            setquestion(record);
            record += 1;




        }
        else{ //틀린 답 골랐을때
            String id= ((LoginActivty)LoginActivty.login_con).id;

            //saveWrong(new VocaData(id,eng_correct,kr_correct,"2021-09-26")); //어차피 서버에서 날짜
            saveWrong(new VocaData(id,target_en[record-1],target_kr[record-1],"2021-09-26")); //어차피 서버에서 날짜

            //3초간 틀림 이미지 보여주기
            result_image.setImageResource(R.drawable.sttbad);
            result_image.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    result_image.setVisibility(View.INVISIBLE);
                }
            }, 500);


            wrong_num += 1;

            for(int k = 0; k < numbers.length -2; k++){

                numbers[k] = (int)(Math.random() * vocanum ) ; //4개의 index 랜덤추출


                for(int j = 0; j < k; j ++){
                    if(numbers[k] == numbers[j]){
                        k--; // 중복값이면 한칸 앞으로 가서 다시 추출
                        break;
                    }
                }
            }



            //setquestion(record);

            setquestion(record);
            record += 1;



        }



        if(record == 11){
            Intent intent = new Intent(getApplicationContext(), TestDone.class);
            intent.putExtra("score",correct_num);
            startActivity(intent);
            finish();
        }

        //getTest(1);
        //setquestion(record);
        //record += 1;








    }

    private void getTest(int i) {
        String id = ((LoginActivty) LoginActivty.login_con).id;
        Call<List<VocaData>> call = service.getTest(new VocaData(id));


        call.enqueue(new Callback<List<VocaData>>()
        {
            @Override
            public void onResponse(Call<List<VocaData>> call, Response<List<VocaData>> response) {
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.d("데이터 조회 응답",response.body().toString());
                    //VocaData = response.body().get(0);
                    test_list = response.body();
                    eng_correct = test_list.get(i).getTitleName();
                    kr_correct = test_list.get(i).getKrName();
                    // a1.setText(eng_correct);

                    for(int i = 0; i<10 ; i++){
                        target_en[i] = test_list.get(i).getTitleName();
                        target_kr[i] = test_list.get(i).getKrName();
                    }




                    words[0] = eng_correct;
                    //words 안에 단어들 집어넣기 (index = 0은 정해진값)

                    for(int n=0 ;  n  < 3 ; n++){
                        if (words[0] != wordarr.get(numbers[n])) {
                            words[n+1] = wordarr.get(numbers[n]);
                        }

                    }

                    //추출한 index 토대로 객관식 보기(1~4) 지정
                    //1-4 셔플하기

                    int qnum[] = new int[4];
                    for(int p =0; p<qnum.length; p++){
                        qnum[p] = p; //qmun = 1,2,3,4
                    }
                    shuffle(qnum);


                    //문제 출제 - 랜덤으로 1-4 보기중 하나 선택

                    Random random = new Random();
                    int r = random.nextInt(4);
                    a1.setText(words[qnum[0]]);
                    a2.setText(words[qnum[1]]);
                    a3.setText(words[qnum[2]]);
                    a4.setText(words[qnum[3]]);
                    question.setText(kr_correct);
                }
            }

            @Override
            public void onFailure(Call<List<VocaData>> call, Throwable t) {
                Toast.makeText(DailyTest.this, "테스트 조회 서버 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("테스트 조회 서버 에러 발생", t.getMessage());
            }

        });

    }

    public void setquestion(int index){

        words[0] = target_en[index];

        //words 안에 단어들 집어넣기 (index = 0은 정해진값)

        for(int n=0 ;  n  < 3 ; n++){
            if (words[0] != wordarr.get(numbers[n])) {
                words[n+1] = wordarr.get(numbers[n]);
            }

        }

        //추출한 index 토대로 객관식 보기(1~4) 지정
        //1-4 셔플하기

        int qnum[] = new int[4];
        for(int p =0; p<qnum.length; p++){
            qnum[p] = p; //qmun = 1,2,3,4
        }
        shuffle(qnum);


        //문제 출제 - 랜덤으로 1-4 보기중 하나 선택

        Random random = new Random();
        int r = random.nextInt(4);
        a1.setText(words[qnum[0]]);
        a2.setText(words[qnum[1]]);
        a3.setText(words[qnum[2]]);
        a4.setText(words[qnum[3]]);
        question.setText(target_kr[index]);


    }

    private void onGetResult(List<VocaData> lists)
    {
        test_list = lists;
    }

    public int[] shuffle(int[] arr){
        for(int x=0;x<arr.length;x++){
            int i = (int)(Math.random()*arr.length);
            int j = (int)(Math.random()*arr.length);

            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }

        return arr;
    }

    public void correct(){
        //3초간 정답 이미지 보여주기
        result_image.setImageResource(R.drawable.sttgood);
        result_image.bringToFront();
        result_image.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                result_image.setVisibility(View.INVISIBLE);
            }
        }, 500);
    }



    //wrong db 저장위한 레트로핏 통신
    private void saveWrong(VocaData data) {
        service.userWrong(data).enqueue(new Callback<VocaResponse>() {

            @Override
            public void onResponse(Call<VocaResponse> call, retrofit2.Response<VocaResponse> response) {
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