package com.chatserver.contactdemo.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.chatserver.contactdemo.ContactApplication;

/**
 * Created by ububtu on 26/7/17.
 * contact change monitor service class
 * for fetching contacts into app and sync the list with previous list
 * always running service
 */

public class ContactSyncService extends Service {
    public static Intent createIntent(Context context) {
        return new Intent(context, ContactSyncService.class);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        ContactApplication.getInstance().registerContactObserver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ContactApplication.getInstance().unRegisterContactObserver();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
