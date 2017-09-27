package com.chatserver.contactdemo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chatserver.contactdemo.ContactApplication;
import com.chatserver.contactdemo.R;
import com.chatserver.contactdemo.model.HistoryContact;

import java.util.List;

/**
 * Created by ububtu on 27/7/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private List<HistoryContact> list;

    public HistoryAdapter(List<HistoryContact> list) {
        this.list = list;
    }

    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.MyViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, number, status;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            status = (TextView) itemView.findViewById(R.id.status);
        }

        public void setData(int position) {
            switch (list.get(position).getStatus()) {
                case ContactApplication.DELETED_ENTRY:
                    setDeletedEntry(position);
                    break;
                case ContactApplication.NEW_ENTRY:
                    setNewEntry(position);
                    break;
                case ContactApplication.UPDATE_ENTRY:
                    setUpdateEntry(position);
                    break;
            }
        }

        public void setDeletedEntry(int pos) {
            name.setText(list.get(pos).getDisplayName());
            number.setText(list.get(pos).getDeviceNumber());
            status.setText("Deleted");
        }

        public void setNewEntry(int pos) {
            name.setText(list.get(pos).getDisplayName());
            number.setText(list.get(pos).getDeviceNumber());
            status.setText("new contact");
        }

        public void setUpdateEntry(int pos) {
            name.setText(list.get(pos).getDisplayName() + "-->" + list.get(pos).getChangeName());
            number.setText(list.get(pos).getDeviceNumber() + "-->" + list.get(pos).getChangeNumber());
            status.setText("update");
        }
    }


}
