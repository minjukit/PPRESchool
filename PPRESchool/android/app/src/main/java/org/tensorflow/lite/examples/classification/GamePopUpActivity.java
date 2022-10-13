package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.tensorflow.lite.examples.classification.game.ClassifierActivity;

public class GamePopUpActivity extends AppCompatActivity {

    Button goGamebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_pop_up);


        goGamebtn = (Button) findViewById(R.id.goGamBtn);
        goGamebtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), ClassifierActivity.class);
                startActivity(i);
            }
        });
    }
}