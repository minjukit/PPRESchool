package org.tensorflow.lite.examples.classification.leveltest;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.tensorflow.lite.examples.classification.R;

public class Fragment_level5 extends Fragment {

    int correct;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //프래그먼트 값 받기

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.level_test_5,container,false);

        if(getArguments() != null) {
            correct = getArguments().getInt("correct");
        }


        Button dog_button = rootView.findViewById(R.id.dog_button);
        dog_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelMain activity = (LevelMain) getActivity();


                //프래그먼트 값전달
                Bundle bundle = new Bundle();
                bundle.putInt("correct", correct+1);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment_level6 fragment_level6 = new Fragment_level6();
                fragment_level6.setArguments(bundle);
                transaction.replace(R.id.gameFrame, fragment_level6);
                transaction.commit();

                new Handler().removeCallbacksAndMessages(null);
                if(getView() != null) {
                  //  activity.onFragmentChanged(5);
                }
            }
        });

        Button cat_button = rootView.findViewById(R.id.cat_button);
        cat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelMain activity = (LevelMain) getActivity();

                //프래그먼트 값전달
                Bundle bundle = new Bundle();
                bundle.putInt("correct", correct);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment_level6 fragment_level6 = new Fragment_level6();
                fragment_level6.setArguments(bundle);
                transaction.replace(R.id.gameFrame, fragment_level6);
                transaction.commit();

                new Handler().removeCallbacksAndMessages(null);
                if(getView() != null) {
                 //   activity.onFragmentChanged(5);
                }
            }
        });

        ProgressBar pb = rootView.findViewById(R.id.progressBar);

        pb.setMax(15);
        pb.setProgress(15);
        LevelMain activity = (LevelMain) getActivity();

        final int[] currentProgress = {15};
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                currentProgress[0] -= 1;
                pb.setProgress(currentProgress[0]);
                if(currentProgress[0] != 0){
                    new Handler().postDelayed(this, 1000);
                }else {
                    LevelMain activity = (LevelMain) getActivity();

                    new Handler().removeCallbacksAndMessages(null);
                   //MainActivity activity = (MainActivity) getActivity();
                    if(getView() != null) {
                        activity.onFragmentChanged(5);
                    }
                }
            }
        }, 1000);

        return rootView;
    }
}
