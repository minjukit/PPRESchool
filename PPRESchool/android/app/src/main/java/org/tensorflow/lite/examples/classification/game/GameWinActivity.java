package org.tensorflow.lite.examples.classification.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.menu.Study;

public class GameWinActivity extends AppCompatActivity {

    Button stopBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_win);

        stopBtn = (Button)findViewById(R.id.stopBtn);


        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Study.class);
                startActivity(intent);
            }
        });
    }
}