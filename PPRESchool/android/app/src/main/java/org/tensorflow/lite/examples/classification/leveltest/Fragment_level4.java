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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.tensorflow.lite.examples.classification.R;

public class Fragment_level4 extends Fragment {

    SoundPool soundPool;
    int soundID;
    int correct;
    MediaPlayer mediaPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.bear_us_1);
        mediaPlayer.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.level_test_4, container, false);

        if(getArguments() != null) {
            correct = getArguments().getInt("correct");
        }

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        soundID = soundPool.load(getActivity(), R.raw.bear_us_1 ,1);

        Button listen = rootView.findViewById(R.id.listen);
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundID, 1f, 1f, 0, 0 , 1f);
            }
        });

        Button bear_button = rootView.findViewById(R.id.bear_button);
        bear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelMain activity = (LevelMain) getActivity();

                //프래그먼트 값전달
                Bundle bundle = new Bundle();
                bundle.putInt("correct", correct+1);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment_level5 fragment_level5 = new Fragment_level5();
                fragment_level5.setArguments(bundle);
                transaction.replace(R.id.gameFrame, fragment_level5);
                transaction.commit();

                new Handler().removeMessages(0);

                if (getView() != null) {
                   // activity.onFragmentChanged(4);
                }
            }
        });

        Button chicken_button = rootView.findViewById(R.id.chicken_button);
        chicken_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelMain activity = (LevelMain) getActivity();

                //프래그먼트 값전달
                Bundle bundle = new Bundle();
                bundle.putInt("correct", correct);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment_level5 fragment_level5 = new Fragment_level5();
                fragment_level5.setArguments(bundle);
                transaction.replace(R.id.gameFrame, fragment_level5);
                transaction.commit();

                new Handler().removeCallbacksAndMessages(null);

                if (getView() != null) {
                  //  activity.onFragmentChanged(4);
                }
            }
        });

        Button elephant_button = rootView.findViewById(R.id.elephant_button);
        elephant_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelMain activity = (LevelMain) getActivity();

                //프래그먼트 값전달
                Bundle bundle = new Bundle();
                bundle.putInt("correct", correct);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment_level5 fragment_level5 = new Fragment_level5();
                fragment_level5.setArguments(bundle);
                transaction.replace(R.id.gameFrame, fragment_level5);
                transaction.commit();

                new Handler().removeCallbacksAndMessages(null);
                if (getView() != null) {
                  //  activity.onFragmentChanged(4);
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
                   // new Handler().removeCallbacks(this);
                    if (getView() != null) {
                        activity.onFragmentChanged(4);
                    }
                }
            }
        }, 1000);

        return rootView;
    }
}
