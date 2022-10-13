package org.tensorflow.lite.examples.classification.pmode;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import org.tensorflow.lite.examples.classification.R;

public class PmodeActivity extends AppCompatActivity {

    TabLayout tabs;

    PmodeFragment1 fragment1;
    PmodeFragment2 fragment2;
    PmodeFragment3 fragment3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pmode_main);

        fragment1 = new PmodeFragment1();
        fragment2 = new PmodeFragment2();
        fragment3 = new PmodeFragment3();


        getSupportFragmentManager().beginTransaction().add(R.id.containers2,fragment1).commit();

        tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("학생정보"));
        tabs.addTab(tabs.newTab().setText("성적차트"));
        tabs.addTab(tabs.newTab().setText("설정"));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();

                Fragment selected = null;
                if(position == 0){
                    selected = fragment1;
                }else if(position == 1){
                    selected = fragment2;
                }
                else if(position == 2){
                    selected = fragment3;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.containers2, selected).commit();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });
        //클릭시 tab텍스트 색 변경
        tabs.setTabTextColors(Color.LTGRAY, Color.BLACK);

    }

}
