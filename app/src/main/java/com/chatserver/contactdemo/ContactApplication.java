package com.chatserver.contactdemo;

import android.app.Application;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chatserver.contactdemo.helpers.ContactHelper;
import com.chatserver.contactdemo.helpers.ContactUris;
import com.chatserver.contactdemo.model.ContactModal;
import com.chatserver.contactdemo.model.HistoryContact;
import com.chatserver.contactdemo.prefs.PreferenceManager;
import com.chatserver.contactdemo.services.ContactSyncService;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ububtu on 26/7/17.
 * all the demo application code written in this class
 */

public class ContactApplication extends Application implements ContactHelper.ContactLoadedListner {
    public static final int NEW_ENTRY = 1;
    public static final int DELETED_ENTRY = 2;
    public static final int UPDATE_ENTRY = 3;
    public static ContactApplication instance;
    MyContentObserver contactObserver = new MyContentObserver();
    Realm realm;
    UiNotifyListener uiNotifyListener;
    List<HistoryContact> historyTransitionList;
    List<ContactModal> recentTransitionList;
    List<Long> recentTransitionList1;
    /**
     * service handler for syncof contacts when a change occurs
     * in observer
     */
    Handler serviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                Log.e("DEBUGLOGEER", "called from handler");
                ContactHelper.getInstance().getAllContact(instance, null, 91);

            }
        }
    };

    public static ContactApplication getInstance() {
        return instance;
    }


    public static void setInstance(ContactApplication instance) {
        ContactApplication.instance = instance;
    }

    public void setUiNotifyListener(UiNotifyListener uiNotifyListener) {
        this.uiNotifyListener = uiNotifyListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Realm.init(this);
        //getting the diffault instance of relam database
        realm = Realm.getDefaultInstance();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }

    /**
     * initialization of application
     * getting the contacts from contact list first tile all the contacts are fatched and directly added
     * to the recent contact table
     * and again when a change occurs in device contacts .
     * <p>
     * and starting of contact service and register the observer on start command of service
     */

    public void initApplication() {

        ContactHelper.getInstance().addContactLoadListner(this);
        Log.e("DEBUGLOGEER", "called from init");
        historyTransitionList = new ArrayList<>();
        recentTransitionList = new ArrayList<>();
        recentTransitionList1 = new ArrayList<>();
        ContactHelper.getInstance().getAllContact(this, null, 91);
        doStartContactService();
    }

    /**
     * register of content observer
     */

    public void registerContactObserver() {

        getContentResolver().registerContentObserver(ContactUris.PHONE_CONTENT_URI, true, contactObserver);
    }

    /**
     * unregister of contact server
     */
    public void unRegisterContactObserver() {
        getContentResolver().unregisterContentObserver(contactObserver);
    }

    /**
     * start contact service for monitoring the changes in device contact list
     * call this method from init of application class.
     */

    public void doStartContactService() {
        startService(ContactSyncService.createIntent(instance));
    }

    /**
     * stop of contact service class
     */
    public void doStopService() {
        stopService(ContactSyncService.createIntent(instance));
    }

    /**
     * when all the contacts are loaded in the application then this call back
     * invoked
     *
     * @param contactModalArrayList
     */
    @Override
    public void contactLoaded(final ArrayList<ContactModal> contactModalArrayList) {

        Log.e("DEBUGLOGEER", contactModalArrayList.size() + "=total contacts");
        Log.e("DEBUGLOGEER", "db path=" + realm.getPath());

        Realm realm1 = Realm.getDefaultInstance();

        if (contactModalArrayList != null) {
            if (!PreferenceManager.getFirstTime(instance)) {
                /**
                 * first when application runs load all the contacts to recent contact table
                 */
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(contactModalArrayList);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        if (uiNotifyListener != null) {
                            uiNotifyListener.notifyUi(contactModalArrayList, new ArrayList<HistoryContact>());
                        }
                    }
                });
//                Log.e("SIZE", result.size() + "");

                PreferenceManager.setFirstTime(instance, true);
            } else {
                // do sync here whenever a change occurs in device contact list
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);


                        if (recentTransitionList.size() > 0) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(recentTransitionList);

                                }
                            });
                        }
                        if (recentTransitionList1.size() > 0) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmResults<ContactModal> rows = realm.where(ContactModal.class).in("row_id", recentTransitionList1.toArray(new Long[recentTransitionList1.size()])).findAll();
                                    if (rows != null) {
                                        rows.deleteAllFromRealm();
                                    }
                                }
                            });
                        }

                        if (historyTransitionList.size() > 0) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealm(historyTransitionList);
                                }
                            });
                        } else {

                        }
                        List<HistoryContact> hisList = getHistoryList();
                        if (uiNotifyListener != null) {
                            uiNotifyListener.notifyUi(contactModalArrayList, hisList);
                        }

                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        Realm realm = Realm.getDefaultInstance();
                        List<ContactModal> previousList = getRecentList(realm);
                        syncContactLists(contactModalArrayList, previousList, realm);
                        return null;
                    }
                }.execute();
            }
        }
    }

    @Override
    public void contactSyncingStart() {

    }

    /**
     * update device contact list
     * and adding transition to the history list.
     *
     * @param recentcontactModal
     * @param preContactModel
     */

    public void updateDeviceContact(ContactModal recentcontactModal, ContactModal preContactModel) {
        final HistoryContact historyContact = new HistoryContact();
        historyContact.setMyId(recentcontactModal.getMyId());
        historyContact.setRawContactId(recentcontactModal.getRawContactId());
        historyContact.setStatus(UPDATE_ENTRY);
        historyContact.setChangeNumber(recentcontactModal.getDeviceNumber());
        historyContact.setChangeName(recentcontactModal.getDisplayName());
        historyContact.setDeviceNumber(preContactModel.getDeviceNumber());
        historyContact.setDisplayName(preContactModel.getDisplayName());
        historyTransitionList.add(historyContact);
        recentTransitionList.add(recentcontactModal);
    }

    /**
     * checking new entry of contacts
     * and add this transition to history list
     *
     * @param recentcontactModal
     */
    public void addNewCotact(ContactModal recentcontactModal) {
        final HistoryContact historyContact = new HistoryContact();
        historyContact.setMyId(recentcontactModal.getMyId());
        historyContact.setRawContactId(recentcontactModal.getRawContactId());
        historyContact.setDeviceNumber(recentcontactModal.getDeviceNumber());
        historyContact.setDisplayName(recentcontactModal.getDisplayName());
        historyContact.setStatus(NEW_ENTRY);
        historyTransitionList.add(historyContact);
        recentTransitionList.add(recentcontactModal);
    }

    /**
     * used to remove the old contacts from previous list
     * and added this transition to the history list
     *
     * @param preContactModel
     * @param realm
     */
    public void removeOldCOntact(final ContactModal preContactModel, Realm realm) {
        final HistoryContact historyContact = new HistoryContact();
        historyContact.setMyId(preContactModel.getMyId());
        historyContact.setRawContactId(preContactModel.getRawContactId());
        historyContact.setDeviceNumber(preContactModel.getDeviceNumber());
        historyContact.setDisplayName(preContactModel.getDisplayName());
        historyContact.setStatus(DELETED_ENTRY);
        historyTransitionList.add(historyContact);
        recentTransitionList1.add(preContactModel.getRow_id());


    }

    /**
     * get all the recent contacts from recent contact table
     *
     * @return
     */
    public List<ContactModal> getRecentList(Realm realm) {
        List<ContactModal> list = new ArrayList<>();
        RealmResults<ContactModal> result = realm.where(ContactModal.class)
                .findAll();
        if (result != null) {
            for (ContactModal contactModal : result
                    ) {
                list.add(contactModal);
            }
        }
        return list;
    }

    /**
     * get history contact list from history table
     *
     * @return
     */

    public List<HistoryContact> getHistoryList() {
        List<HistoryContact> list = new ArrayList<>();
        RealmResults<HistoryContact> result = realm.where(HistoryContact.class)
                .findAll();
        if (result != null) {
            for (HistoryContact contactModal : result
                    ) {
                list.add(contactModal);
            }
        }
        return list;

    }

    /**
     * invoke this method from async task of contact loaded listener
     * to check all the updates and new entries and deleted contacts
     * in the device contact list.
     *
     * @param contactModalArrayList
     * @param previousList
     * @param realm
     */

    public synchronized void syncContactLists(ArrayList<ContactModal> contactModalArrayList, List<ContactModal> previousList, Realm realm) {
        historyTransitionList.clear();
        recentTransitionList.clear();
        recentTransitionList1.clear();
        List<ContactModal> operationList_old = new ArrayList<>(previousList);
        List<ContactModal> operationList_new = new ArrayList<>(contactModalArrayList);
        for (ContactModal contactModal : operationList_old
                ) {
            Log.e("OLDLIST", contactModal.toString());
        }
        for (ContactModal contactModal : operationList_new
                ) {
            Log.e("NEWLIST", contactModal.toString());

        }
        operationList_new.removeAll(operationList_old);// new--> updated,added
        operationList_old.removeAll(contactModalArrayList);//--> update,deleted


        for (int i = 0; i < operationList_new.size(); i++) {
            boolean isupdated = false;
            ContactModal newContact = operationList_new.get(i);
            for (int j = 0; j < operationList_old.size(); j++) {

                ContactModal previousContact = operationList_old.get(j);
                if (newContact.getRow_id() == previousContact.getRow_id()) {
                    updateDeviceContact(newContact, previousContact);
                    operationList_new.remove(i);
                    operationList_old.remove(j);
                    isupdated = true;
                    i--;
                    break;
                }
            }
            if (!isupdated) {
                addNewCotact(newContact);
                operationList_new.remove(i);
                i--;
            }
        }
        for (int j = 0; j < operationList_old.size(); j++) {
            ContactModal previousContact = operationList_old.get(j);
            removeOldCOntact(previousContact, realm);
        }
    }

    public interface UiNotifyListener {
        void notifyUi(List<ContactModal> recentList, List<HistoryContact> historyList);
    }

    /**
     * compare model class compare numbers and display name of contact list
     */

    public static class CompareModels {
        public static boolean compareNumber(ContactModal previousModel, ContactModal latestModel) {
            if (previousModel.getDeviceNumber().equals(latestModel.getDeviceNumber())) {
                return true;
            }
            return false;
        }

        public static boolean compareName(ContactModal previousModel, ContactModal latestModel) {
            if (previousModel.getDisplayName().equals(latestModel.getDisplayName())) {
                return true;
            }
            return false;
        }

    }

    /**
     * content observer class which has a method on change
     */
    private class MyContentObserver extends ContentObserver {

        public MyContentObserver() {
            super(serviceHandler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //   ContactHelper.printLog(TAG + " MyContentObserver onChange selfChange=" + selfChange);
            serviceHandler.removeMessages(2);
            serviceHandler.sendEmptyMessageDelayed(2, 2000);
        }
    }
}
