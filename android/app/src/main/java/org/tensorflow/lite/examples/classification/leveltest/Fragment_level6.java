package org.tensorflow.lite.examples.classification.leveltest;

import android.content.Intent;
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

import org.tensorflow.lite.examples.classification.R;


public class Fragment_level6 extends Fragment {

    int correct;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //프래그먼트 값 받기

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.level_test_6, container, false);

        if(getArguments() != null) {
            correct = getArguments().getInt("correct");
        }

        Button f_button = rootView.findViewById(R.id.f_button);
        f_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelMain activity = (LevelMain) getActivity();

                //프래그먼트 값전달
                Intent intent = new Intent(getActivity(), level_test_score.class);
                correct = correct + 1;
                intent.putExtra("correct", correct);
                intent.setAction(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(intent);

                new Handler().removeCallbacksAndMessages(null);

                if(getView() != null) {
                }
            }
        });

        Button w_button = rootView.findViewById(R.id.w_button);
        w_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    LevelMain activity = (LevelMain) getActivity();

                    //프래그먼트 값전달
                    Intent intent = new Intent(getActivity(), level_test_score.class);
                    intent.putExtra("correct", correct);
                    intent.setAction(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(intent);
                    new Handler().removeCallbacksAndMessages(null);
            }
        });

        ProgressBar pb = rootView.findViewById(R.id.progressBar);

        pb.setMax(15);
        pb.setProgress(15);

        final int[] currentProgress = {15};
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                currentProgress[0] -= 1;
                pb.setProgress(currentProgress[0]);
                if(currentProgress[0] != 0){
                    new Handler().postDelayed(this, 1000);
                }else {

                    if(getView() != null) {

                        LevelMain activity = (LevelMain) getActivity();

                        if(level_test_score.levelcontext == null) {
                            //프래그먼트 값전달
                            Intent intent = new Intent(getActivity(), level_test_score.class);
                            intent.putExtra("correct", correct);
                            intent.setAction(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).addCategory(Intent.CATEGORY_LAUNCHER);
                            startActivity(intent);
                            new Handler().removeCallbacksAndMessages(null);
                        }
                    }
                }
            }
        }, 1000);

        return rootView;
    }
}
