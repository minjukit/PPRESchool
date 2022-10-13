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

public class Fragment_level2 extends Fragment {

    MediaPlayer mediaPlayer;
    SoundPool soundPool;
    int soundID;
    int correct;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.u_us_2);
        mediaPlayer.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.level_test_2, container, false);

        if(getArguments() != null) {
            correct = getArguments().getInt("correct");
        }

       soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
       soundID = soundPool.load(getActivity(), R.raw.u_us_2,1);

       Button listen = rootView.findViewById(R.id.listen);
       listen.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               soundPool.play(soundID, 1f, 1f, 0, 0 , 1f);
           }
       });


       Button u_button = rootView.findViewById(R.id.u_button);
       u_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               LevelMain activity = (LevelMain) getActivity();

               //프래그먼트 값전달
               Bundle bundle = new Bundle();
               bundle.putInt("correct", correct+1);
               FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
               Fragment_level3 fragment_level3 = new Fragment_level3();
               fragment_level3.setArguments(bundle);
               transaction.replace(R.id.gameFrame, fragment_level3);
               transaction.commit();

               new Handler().removeCallbacksAndMessages(null);
               if(getView() != null) {
                  // activity.onFragmentChanged(2);
               }
           }
       });

       Button v_button = rootView.findViewById(R.id.v_button);
       v_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               LevelMain activity = (LevelMain) getActivity();

               //프래그먼트 값전달
               Bundle bundle = new Bundle();
               bundle.putInt("correct", correct);
               FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
               Fragment_level3 fragment_level3 = new Fragment_level3();
               fragment_level3.setArguments(bundle);
               transaction.replace(R.id.gameFrame, fragment_level3);
               transaction.commit();

               new Handler().removeCallbacksAndMessages(null);
               if(getView() != null) {
                   //activity.onFragmentChanged(2);
               }
           }
       });

        ProgressBar pb = rootView.findViewById(R.id.progressBar);

        pb.setMax(15);
        pb.setProgress(15);

        LevelMain activity = (LevelMain)getActivity();

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
                    if(getView() != null) {
                        activity.onFragmentChanged(2);
                    }
                }
            }
        }, 1000);

       return rootView;
    }
}
