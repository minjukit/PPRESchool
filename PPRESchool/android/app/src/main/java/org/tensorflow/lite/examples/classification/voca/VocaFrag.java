package org.tensorflow.lite.examples.classification.voca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.classification.LoginActivty;
import org.tensorflow.lite.examples.classification.Papago;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.data.VocaData;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocaFrag extends Fragment {
    RecyAdapter adapter;
    RecyAdapter.ItemClickListener itemClickListener;
    //network
    private ServiceApi service;
    List<VocaData> vocalist = new ArrayList<>();
    private LinearLayoutManager llm;
    View view;
    RecyclerView rv;
    Context context;
    int set = 1; //초기설정은 일별


    public VocaFrag(int num) {
        this.set = num;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.voca_frag, container, false);
        context = view.getContext();

        //network
        service = RetrofitClient.getClient().create(ServiceApi.class);
        rv = (RecyclerView) view.findViewById(R.id.voca_recyView);
        llm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false); //단어 스크롤 가로로
        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);
        adapter = new RecyAdapter(context, vocalist, itemClickListener);
        getVoca(set);

        //클릭시
        itemClickListener = new RecyAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String date = vocalist.get(position).getDate();
                String krName = vocalist.get(position).getKrName();
                String enName = vocalist.get(position).getTitleName();

                Intent intent = new Intent(context,
                        Papago.class);
                intent.putExtra("Objectname", enName);
                intent.putExtra("KrText", krName);
                startActivity(intent);
            }
        };


        return view;
    }

    //db 저장위한 레트로핏 통신
    private void getVoca(int set) {
        String id = ((LoginActivty) LoginActivty.login_con).id;
        Call<List<VocaData>> call = service.getVoca(new VocaData(id, set));
        call.enqueue(new Callback<List<VocaData>>() {
            @Override
            public void onResponse(Call<List<VocaData>> call, Response<List<VocaData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onGetResult(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaData>> call, Throwable t) {
                Log.e("selectPerson()", "에러 : " + t.getMessage());
            }

        });
    }

    private void onGetResult(List<VocaData> lists) {
        adapter = new RecyAdapter(context, lists, itemClickListener);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
        vocalist = lists;

    }
}