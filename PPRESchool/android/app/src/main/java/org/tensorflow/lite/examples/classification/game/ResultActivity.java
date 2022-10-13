package org.tensorflow.lite.examples.classification.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.classification.R;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class ResultActivity extends AppCompatActivity implements Serializable {

    private TextView result_tv;
    private ArrayList<String> results;
    private Button game_start;
    private Button reset;
    public static Activity ResultActivity;
    String obj1;
    String obj2;
    String obj3;
    //인식된 사물 개수
    public static int amount;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.classificatioin_result);
        result_tv = (TextView) findViewById(R.id.result);
        game_start = (Button) findViewById(R.id.game);
        reset = (Button) findViewById(R.id.reset);


        Intent intent = getIntent();
        results = (ArrayList<String>) intent.getSerializableExtra("results");
        amount = results.size(); //저장된 사물의 개수
        for(String str : results){
            result_tv.append(str);
            result_tv.append("\n");

        }


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), ClassifierActivity.class);
                startActivity(intent);
               // finish();

            }

        });

        game_start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent intent = new Intent(getApplicationContext(), GameActivity.class);

                if(amount == 1){

                    obj1 = results.get(0);

                    intent.putExtra("obj1", obj1);
                }
                else if(amount == 2){

                    obj1 = results.get(0);
                    obj2 = results.get(1);

                    intent.putExtra("obj1", obj1);
                    intent.putExtra("obj2", obj2);
                }
                else if(amount >= 3){

                   int bound = amount;
                   int a[] = new int[amount];

                   Random random = new Random();
                   for (int i =0; i<bound ; i++){
                       a[i] = random.nextInt(bound);
                       for(int j = 0; j<i; j++){
                           if(a[i] == a[j])
                               i--;
                       }
                   }

                    obj1 = results.get(a[0]);
                    obj2 = results.get(a[1]);
                    obj3 = results.get(a[2]);

                    intent.putExtra("obj1", obj1);
                    intent.putExtra("obj2", obj2);
                    intent.putExtra("obj3", obj3);


                }

                //setResult(RESULT_OK, intent);
                //finish();
                startActivity(intent);

            }

        });


    }


}
