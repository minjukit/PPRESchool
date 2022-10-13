package org.tensorflow.lite.examples.classification.pmode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.tensorflow.lite.examples.classification.LoginActivty;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.data.ChildData;
import org.tensorflow.lite.examples.classification.menu.main;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class PmodeFragment1 extends Fragment {

    Button p_change;
    Button p_home;
    TextView p_name;
    TextView p_level;
    TextView level_info;

    String name;
    ImageView p_profile;
    ImageView p_level_pic;
    int level_num;

    private String id;
    private ServiceApi service;
    List<ChildData> list = new ArrayList<>();
    ChildData childData;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.pmode_fragment1, container, false);
        service = RetrofitClient.getClient().create(ServiceApi.class);

        p_name = (TextView) rootView.findViewById(R.id.p_name);
        p_profile = (ImageView) rootView.findViewById(R.id.p_profile);
        p_level = (TextView) rootView.findViewById(R.id.p_level);
        p_level_pic = (ImageView) rootView.findViewById(R.id.p_level_pic);
        level_info = (TextView) rootView.findViewById(R.id.level_info);
        p_change = (Button) rootView.findViewById(R.id.p_change);
        p_home = (Button) rootView.findViewById(R.id.p_home);

        //이름 서버에서 가져와서 넣기
        id = ((LoginActivty)LoginActivty.login_con).id;
        getChild(new ChildData(id));

        p_profile.setImageResource(R.drawable.basic_profile);

        p_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ChildPopupActivity.class);
                startActivity(i);
            }
        });

        p_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(getActivity(), main.class);
                startActivity(i2);
            }
        });


        return rootView;
    }

    //서버에 child 테이블 조회
    private void getChild(ChildData data) {
        service.getChild(data).enqueue(new Callback<List<ChildData>>() {
            @Override
            public void onResponse(Call<List<ChildData>> call, retrofit2.Response<List<ChildData>> response) {

                if (response.isSuccessful() && response.body() != null)
                {
                    Log.d("데이터 조회 응답",response.body().toString());
                    childData = response.body().get(0);
                    //아이 이름
                    p_name.setText(childData.getUserNick());
                    //아이 레벨
                    level_num = childData.getUserLevel();
                    if(level_num == 1) {
                        p_level_pic.setImageResource(R.drawable.seeds);
                        p_level.setText("씨앗");
                        level_info.setText("A-Z 사이의 알파벳이 가진 소리와 발음을 배우는 단계");
                    } else if(level_num == 2) {
                        p_level_pic.setImageResource(R.drawable.plant);
                        p_level.setText("새싹");
                        level_info.setText("간단한 단어의 발음과 뜻을 배우는 단계");
                    } else if(level_num == 3) {
                        p_level_pic.setImageResource(R.drawable.flower);
                        p_level.setText("꽃");
                        level_info.setText("심화된 단어의 발음과 뜻을 배우며 나아가 문장을 배우는 단계");
                    }
                }


            }

            @Override
            public void onFailure(Call<List<ChildData>> call, Throwable t) {
                Toast.makeText(getActivity(), "데이터 조회 서버 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("데이터 조회 서버 에러 발생", t.getMessage());
            }
        });
    }

}