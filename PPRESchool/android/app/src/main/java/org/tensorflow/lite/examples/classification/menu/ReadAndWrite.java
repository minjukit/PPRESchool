package org.tensorflow.lite.examples.classification.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.tensorflow.lite.examples.classification.Level3StudyActivity;
import org.tensorflow.lite.examples.classification.ListenSpeak;
import org.tensorflow.lite.examples.classification.MyDictionaryRequest;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.WriteActivity;
import org.tensorflow.lite.examples.classification.hamburger_toolbar;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class ReadAndWrite extends hamburger_toolbar {

    int level;
    String data;
    private String worden;
    private String url;
    public String name;

    private Button btn_read;
    private Button btn_write;

    private ServiceApi service;

    int level_num;

    @Override
    public void make_hamburger() {
        super.make_hamburger();
    }


    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_write);
        make_hamburger();

        btn_read = (Button) findViewById(R.id.btn_read);
        btn_write = (Button) findViewById(R.id.btn_write);

        service = RetrofitClient.getClient().create(ServiceApi.class);


        //가져온 level 넣기
        level_num = ((hamburger_toolbar) hamburger_toolbar.hamContext).level;


        //단어와 한글 뜻 저장할 변수
        ArrayList<String> wordarr = new ArrayList<String>();
        ArrayList<String> meanarr = new ArrayList<String>();

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

        int vocanum = wordarr.size();

        //무작위로 단어 추출
        Random random = new Random();
        int r = random.nextInt(vocanum);


        worden = wordarr.get(r).trim();

        name = worden;

        url = inflections();

        MyDictionaryRequest myDictionaryRequest = new MyDictionaryRequest(this);
        myDictionaryRequest.execute(url);

        btn_read.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (level_num == 3) {


                    Intent intent = new Intent(getApplicationContext(),
                            Level3StudyActivity.class);
                    intent.putExtra("Objectname", name );

                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), ListenSpeak.class);
                    intent.putExtra("mylevel", level_num);
                    startActivity(intent);
                }
            }

        });

        btn_write.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
                intent.putExtra("mode", 2);
                startActivity(intent);
            }
        });

    }

    private String inflections() {
        final String language = "en_US";

        final String word = name;
        //final String word = worden; //원래코드
        //final String word_lo = word.toLowerCase();
        return "https://api.dictionaryapi.dev/api/v2/entries/" + language + "/"+word;
    }

}