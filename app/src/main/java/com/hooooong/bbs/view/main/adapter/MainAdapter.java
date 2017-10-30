package com.hooooong.bbs.view.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hooooong.bbs.R;
import com.hooooong.bbs.model.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Hong on 2017-10-27.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Data> dataList;

    public MainAdapter(List<Data> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Data data = dataList.get(position);
        holder.setTitle(data.getTitle());
        holder.setDate(data.getDate());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addDataAndRefresh(List<Data> data) {
        //dataList.addAll(data);
        List<Data> temp = new ArrayList<>();
        temp.addAll(dataList);
        temp.addAll(data);
        dataList = temp;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textTitle;
        private TextView textDate;

        public ViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDate = itemView.findViewById(R.id.textDate);
        }

        private void setTitle(String title){
            textTitle.setText(title);
        }

        private void setDate(String date){
            textDate.setText(date);
        }

    }
}
