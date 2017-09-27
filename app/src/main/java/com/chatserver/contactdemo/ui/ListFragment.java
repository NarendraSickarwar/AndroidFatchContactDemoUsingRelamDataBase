package com.chatserver.contactdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatserver.contactdemo.ContactApplication;
import com.chatserver.contactdemo.R;
import com.chatserver.contactdemo.adapters.HistoryAdapter;
import com.chatserver.contactdemo.adapters.RecentAdapter;
import com.chatserver.contactdemo.model.ContactModal;
import com.chatserver.contactdemo.model.HistoryContact;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by ububtu on 27/7/17.
 * single list fragment class used for both list's
 * recent and history lists
 */

public class ListFragment extends Fragment {
    View view = null;
    int value;
    RecyclerView recyclerView;
    List<ContactModal> recentList;
    List<HistoryContact> historyList;
    RecentAdapter recentAdapter;
    HistoryAdapter historyAdapter;

    public static ListFragment newInstance(int val) {
/**
 * value =0 for the recent contact list
 * value=1 for the history of contact list
 */
        Bundle args = new Bundle();
        args.putInt("value", val);
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //if (view == null) {
        view = inflater.inflate(R.layout.list_fragment, container, false);
        // }
        Bundle bundle = getArguments();
        value = bundle.getInt("value");
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * initialization of fragment views
     * and setting lists to the adapter
     *
     * @param view
     */
    private void init(View view) {
        Realm realm = Realm.getDefaultInstance();
        recentList = new ArrayList<>();
        recentList = ContactApplication.getInstance().getRecentList(realm);
        historyList = new ArrayList<>();
        historyList = ContactApplication.getInstance().getHistoryList();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclearView);
        setAdapters();


    }

    /**
     * setting the adapters according to the bundle values
     * for the lists of recent contacts and history contacts.
     */

    public void setAdapters() {
        if (value == 0) {
            recentAdapter = new RecentAdapter(recentList);
            recyclerView.setAdapter(recentAdapter);
        } else if (value == 1) {
            historyAdapter = new HistoryAdapter(historyList);
            recyclerView.setAdapter(historyAdapter);
        }
    }

    public void notifyAdapterDataSet(List<ContactModal> recentList, List<HistoryContact> historyList) {

        if (value == 1) {
            this.historyList.clear();
            this.historyList.addAll(historyList);
            historyAdapter.notifyDataSetChanged();
        } else if (value == 0) {
            this.recentList.clear();
            this.recentList.addAll(recentList);
            recentAdapter.notifyDataSetChanged();
        }
    }

}
