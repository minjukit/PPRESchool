package org.tensorflow.lite.examples.classification.leveltest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.classification.R;

public class LevelMain extends AppCompatActivity {

    Fragment_level1 fragment_level1;
    Fragment_level2 fragment_level2;
    Fragment_level3 fragment_level3;
    Fragment_level4 fragment_level4;
    Fragment_level5 fragment_level5;
    Fragment_level6 fragment_level6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        fragment_level1 = (Fragment_level1) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
        fragment_level2 = new Fragment_level2();
        fragment_level3 = new Fragment_level3();
        fragment_level4 = new Fragment_level4();
        fragment_level5 = new Fragment_level5();
        fragment_level6 = new Fragment_level6();
    }


    public void onFragmentChanged(int index) {

        switch (index) {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.gameFrame, fragment_level1).addToBackStack(null).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.gameFrame, fragment_level2).addToBackStack(null).commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.gameFrame, fragment_level3).addToBackStack(null).commit();
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.gameFrame, fragment_level4).addToBackStack(null).commit();
                break;
            case 4:
                getSupportFragmentManager().beginTransaction().replace(R.id.gameFrame, fragment_level5).addToBackStack(null).commit();
                break;
            case 5:
                getSupportFragmentManager().beginTransaction().replace(R.id.gameFrame, fragment_level6).addToBackStack(null).commit();
                break;
        }
    }


}
