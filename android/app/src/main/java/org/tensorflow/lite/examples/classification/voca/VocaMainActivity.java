package org.tensorflow.lite.examples.classification.voca;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.tensorflow.lite.examples.classification.R;

public class VocaMainActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voca_main);



        //TabLayout
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("일별 단어장"));
        tabs.addTab(tabs.newTab().setText("주별 단어장"));
        tabs.addTab(tabs.newTab().setText("월별 단어장"));
        tabs.setTabGravity(tabs.GRAVITY_FILL);


        //뷰페이저
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        //어답터설정
        final VocaPager vPagerAdapter = new VocaPager(getSupportFragmentManager(), 3);
        viewPager.setAdapter(vPagerAdapter);

        //탭메뉴를 클릭하면 해당 프래그먼트로 변경-싱크화
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        //클릭시 tab텍스트 색 변경
        tabs.setTabTextColors(Color.LTGRAY, Color.BLACK);
    }


}