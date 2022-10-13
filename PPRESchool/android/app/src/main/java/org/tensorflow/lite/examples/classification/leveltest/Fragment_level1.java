package org.tensorflow.lite.examples.classification.leveltest;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.tensorflow.lite.examples.classification.R;

public class Fragment_level1 extends Fragment {

    MediaPlayer mediaPlayer;
    SoundPool soundPool;
    int soundID;
    int correct = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.b_us_1);
        mediaPlayer.start();
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.level_test_1, container, false);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        soundID = soundPool.load(getActivity(), R.raw.b_us_1,1);


        Button listen = rootView.findViewById(R.id.listen);
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundID,1f,1f,0,0,1f);
            }
        });


        Button A_button = rootView.findViewById(R.id.A_button);
        A_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelMain activity = (LevelMain) getActivity();
                new Handler().removeCallbacksAndMessages(null);

                //프래그먼트 값전달
                Bundle bundle = new Bundle();
                bundle.putInt("correct", correct);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment_level2 fragment_level2 = new Fragment_level2();
                fragment_level2.setArguments(bundle);
                transaction.replace(R.id.gameFrame, fragment_level2);
                transaction.commit();

                if(getView() != null) {
                    //결과 프래그먼트 교체
                   // activity.onFragmentChanged(1);
                }
            }
        });

        Button B_button = rootView.findViewById(R.id.B_button);
        B_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelMain activity = (LevelMain) getActivity();

                new Handler().removeCallbacksAndMessages(null);

                //프래그먼트 값전달
                Bundle bundle = new Bundle();
                bundle.putInt("correct", correct+1);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment_level2 fragment_level2 = new Fragment_level2();
                fragment_level2.setArguments(bundle);
                transaction.replace(R.id.gameFrame, fragment_level2);
                transaction.commit();

                if(getView() != null) {
                    //결과 프래그먼트 교체
                   // activity.onFragmentChanged(1);
                }
            }
        });

        ProgressBar pb = rootView.findViewById(R.id.progressBar);

        pb.setMax(15);
        pb.setProgress(15);

        final int[] currentProgress = {15};
        LevelMain activity = (LevelMain) getActivity();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                currentProgress[0] -= 1;
                pb.setProgress(currentProgress[0]);

                if(currentProgress[0] != 0){
                    new Handler().postDelayed(this, 1000);
                } else {
                    LevelMain activity = (LevelMain) getActivity();

                    new Handler().removeCallbacksAndMessages(null);

                    if(getView() != null) {
                        activity.onFragmentChanged(1);
                    }
                }

            }
        }, 1000);


        return rootView;
    }
}

