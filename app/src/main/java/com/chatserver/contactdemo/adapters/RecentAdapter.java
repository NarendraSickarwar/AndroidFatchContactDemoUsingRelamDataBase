package com.chatserver.contactdemo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chatserver.contactdemo.R;
import com.chatserver.contactdemo.model.ContactModal;

import java.util.List;

/**
 * Created by ububtu on 27/7/17.
 */

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.MyViewHolder> {
    private List<ContactModal> arrayList;

    public RecentAdapter(List<ContactModal> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_items, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
        }

        public void setData(int position) {
            name.setText(arrayList.get(position).getDisplayName());
            number.setText(arrayList.get(position).getDeviceNumber());
        }
    }
}
