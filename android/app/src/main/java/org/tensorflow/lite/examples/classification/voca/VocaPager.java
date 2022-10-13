package org.tensorflow.lite.examples.classification.voca;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class VocaPager extends FragmentPagerAdapter {
    int mNumOfTabs; //tab의 갯수

    public VocaPager(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm,numOfTabs);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                VocaFrag tab1 = new VocaFrag(1);
                return tab1;
            case 1:
                VocaFrag tab2 = new VocaFrag(2);
                return tab2;
            case 2:
                VocaFrag tab3 = new VocaFrag(3);
                return tab3;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return this.mNumOfTabs;
    }
}