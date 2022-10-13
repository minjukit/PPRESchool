package org.tensorflow.lite.examples.classification.leveltest;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;

import org.tensorflow.lite.examples.classification.R;

public class level_test_tutorial extends Activity {

    Button speaker_test;
    Button level_test_start_button;
    SoundPool soundPool;
    int soundID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //팝업창 띄우기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.level_test_tutorial);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        soundID = soundPool.load(this, R.raw.b_us_1,1);

        speaker_test = (Button)findViewById(R.id.speaker_test);
        level_test_start_button = (Button)findViewById(R.id.level_test_start_button);

        speaker_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundID,1f,1f,0,0,1f);
            }
        });

        level_test_start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LevelMain.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭해도 닫히지 않게한다.
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }
}
