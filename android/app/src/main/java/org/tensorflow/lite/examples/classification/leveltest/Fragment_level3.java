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

public class Fragment_level3 extends Fragment {

    int correct;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //프래그먼트 값 받기

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.level_test_3,container,false);

        if(getArguments() != null) {
            correct = getArguments().getInt("correct");
        }

        Button orange_button = rootView.findViewById(R.id.orange_button);
        orange_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelMain activity = (LevelMain) getActivity();

                //프래그먼트 값전달
                Bundle bundle = new Bundle();
                bundle.putInt("correct", correct+1);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment_level4 fragment_level4 = new Fragment_level4();
                fragment_level4.setArguments(bundle);
                transaction.replace(R.id.gameFrame, fragment_level4);
                transaction.commit();

                new Handler().removeCallbacksAndMessages(null);
                if (getView() != null) {
                   // activity.onFragmentChanged(3);
                }
            }
        });

        Button banana_button = rootView.findViewById(R.id.banana_button);
        banana_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelMain activity = (LevelMain) getActivity();

                new Handler().removeCallbacksAndMessages(null);
                if (getView() != null) {

                    //프래그먼트 값전달
                    Fragment fragment_level4 = new Fragment_level4();
                    Bundle bundle = new Bundle();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    bundle.putInt("correct", correct);
                    fragment_level4.setArguments(bundle);
                    transaction.replace(R.id.gameFrame, fragment_level4);
                    transaction.commit();

                    //activity.onFragmentChanged(3);
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

                    if (getView() != null) {
                        activity.onFragmentChanged(3);
                    }
                }
            }
        }, 1000);

        return rootView;
    }
}
