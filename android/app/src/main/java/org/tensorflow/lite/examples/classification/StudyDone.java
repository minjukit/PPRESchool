package org.tensorflow.lite.examples.classification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import org.tensorflow.lite.examples.classification.menu.Study;

public class StudyDone extends Activity {

    Button stop;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_done);

        stop = findViewById(R.id.stopBtn);

        stop.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Study.class);
            startActivity(intent);
            finish();

        });


    }
}