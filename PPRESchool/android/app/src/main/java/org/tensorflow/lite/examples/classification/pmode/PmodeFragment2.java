package org.tensorflow.lite.examples.classification.pmode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.tensorflow.lite.examples.classification.LoginActivty;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.data.ScoreData;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PmodeFragment2 extends Fragment {

    ListView dailylist;
    ListView wronglist;

    List<ScoreData> d_score_list = new ArrayList<>();
    List<ScoreData> w_score_list = new ArrayList<>();

    SimpleAdapter w_adapter;
    SimpleAdapter adapter;

    private String id;
    private ServiceApi service;

    //데일리
    ArrayList<HashMap<String, String>> scoreList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> listData = null;

    //오답노트
    ArrayList<HashMap<String, String>> w_scoreList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> w_listData = null;

    String[] d_from = {"score", "date"};
    int[] d_to = new int[]{android.R.id.text1, android.R.id.text2};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.pmode_fragment2, container, false);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        dailylist = (ListView) rootView.findViewById(R.id.daily_list);
        wronglist = (ListView) rootView.findViewById(R.id.wrong_list);

        scoreList.clear();
        w_scoreList.clear();
        listData = new HashMap<String, String>();
        listData.clear();
        w_listData = new HashMap<String, String>();
        w_listData.clear();
        //데일리 점수
        id = ((LoginActivty) LoginActivty.login_con).id;
        getWScore(new ScoreData(id));
        getScore(new ScoreData(id));

        adapter = new SimpleAdapter(getActivity(), scoreList, android.R.layout.simple_list_item_2, d_from, d_to);
        dailylist.setAdapter(adapter);

        //오답테스트
        w_adapter = new SimpleAdapter(getActivity(), w_scoreList, android.R.layout.simple_list_item_2, d_from, d_to);
        wronglist.setAdapter(w_adapter);

        return rootView;
    }

    //db 저장위한 레트로핏 통신
    private void getScore(ScoreData data) {

        Call<List<ScoreData>> call = service.getScore(data);
        call.enqueue(new Callback<List<ScoreData>>() {
            @Override
            public void onResponse(Call<List<ScoreData>> call, Response<List<ScoreData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    d_score_list = response.body();
                    int i = 0;
                    while (i < d_score_list.size()) {
                        listData = new HashMap<String, String>();
                        listData.put("score", d_score_list.get(i).getScore());
                        listData.put("date", d_score_list.get(i).getDate());
                        scoreList.add(listData);
                        i++;
                    }
                    adapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onFailure(Call<List<ScoreData>> call, Throwable t) {
                Log.e("selectPerson()", "에러 : " + t.getMessage());
            }

        });
    }


    //db 저장위한 레트로핏 통신
    private void getWScore(ScoreData data) {

        Call<List<ScoreData>> call = service.getWScore(data);
        call.enqueue(new Callback<List<ScoreData>>() {
            @Override
            public void onResponse(Call<List<ScoreData>> call, Response<List<ScoreData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    w_score_list = response.body();
                    int i = 0;
                    while (i < w_score_list.size()) {
                        w_listData = new HashMap<String, String>();
                        w_listData.put("score", w_score_list.get(i).getScore());
                        w_listData.put("date", w_score_list.get(i).getDate());
                        w_scoreList.add(w_listData);
                        i++;
                    }
                    w_adapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onFailure(Call<List<ScoreData>> call, Throwable t) {
                Log.e("selectPerson()", "에러 : " + t.getMessage());
            }

        });
    }

}