package org.tensorflow.lite.examples.classification.voca;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.data.VocaData;

import java.util.List;

public class RecyAdapter extends RecyclerView.Adapter<RecyAdapter.CustomViewHolder> {

    private Context context;
    private List<VocaData> lists;
    //private int i;
    private ItemClickListener itemClickListener;

    public RecyAdapter(Context context, List<VocaData> lists, ItemClickListener itemClickListener) {
        this.context = context;
        this.lists = lists;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voca_list, parent, false);
        return new CustomViewHolder(view, itemClickListener);

/*
        View view = LayoutInflater.from(context).inflate(R.layout.voca_list, parent, false);
        return new CustomViewHolder(view, itemClickListener);

*/
    }


    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        VocaData vocaData = lists.get(position);


        holder.enText.setText(vocaData.getTitleName());
        holder.krText.setText(vocaData.getKrName());
        holder.date.setText(vocaData.getDate());


    }

    @Override
    public int getItemCount() {
        return this.lists.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView krText;
        TextView enText;
        TextView date;
        public LinearLayout linearLayout;
        ItemClickListener itemClickListener;

        public CustomViewHolder(View itemView,ItemClickListener itemClickListener) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.listlinearLayout);
            enText = itemView.findViewById(R.id.item_en);
            krText = itemView.findViewById(R.id.item_kr);
            date = itemView.findViewById(R.id.item_date);
            this.itemClickListener = itemClickListener;
            linearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
    }
}