package com.chatserver.contactdemo.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.chatserver.contactdemo.model.ContactModal;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ububtu on 26/7/17.
 */

public class ContactHelper {
    public static final String TAG = "ContactHelper";
    static ContactHelper instance;

    static {
        instance = new ContactHelper();
    }

    ContactLoadStatus contactLoadStatus = ContactLoadStatus.NOTRUNNING;
    ArrayList<ContactLoadedListner> contactLoadedListnerArrayList;

    private ContactHelper() {

    }

    public static ContactHelper getInstance() {
        return instance;
    }

    public static void printLog(String message) {
        Log.e("COntact", message);
    }

    public void addContactLoadListner(ContactLoadedListner contactLoadedListner) {
        if (contactLoadedListner == null) {
            return;
        }
        if (contactLoadedListnerArrayList == null) {
            contactLoadedListnerArrayList = new ArrayList<>();
        }
        contactLoadedListnerArrayList.add(contactLoadedListner);
    }

    public void removeContactLoadListner(ContactLoadedListner contactLoadedListner) {
        if (contactLoadedListner == null) {
            return;
        }
        if (contactLoadedListnerArrayList != null) {
            contactLoadedListnerArrayList.remove(contactLoadedListner);
        }
    }

    public void removeAllContactLoadListners() {
        if (contactLoadedListnerArrayList != null) {
            contactLoadedListnerArrayList.clear();
        }
    }

    public synchronized void getAllContact(final Context context,
                                           final ContactLoadedListner contactLoadedListner,
                                           final int countryPhoneCode) {
        printLog(TAG + " getAllContact");
        if (contactLoadStatus == ContactLoadStatus.RUNNING) {
            addContactLoadListner(contactLoadedListner);
            printLog(TAG + " getAllContact already running wait for result.");
            return;
        }
        AsyncTask<Void, Void, ArrayList<ContactModal>> contactLoadThread = new AsyncTask<Void, Void, ArrayList<ContactModal>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (contactLoadedListnerArrayList != null) {
                    for (ContactLoadedListner contactLoadedListner1 : contactLoadedListnerArrayList) {
                        contactLoadedListner1.contactSyncingStart();
                    }
                }
            }

            @Override
            protected void onPostExecute(ArrayList<ContactModal> contactModalArrayList) {
                super.onPostExecute(contactModalArrayList);
                printLog(TAG + " contactLoadThread onPostExecute.");
                contactLoadStatus = ContactLoadStatus.NOTRUNNING;
                if (contactLoadedListnerArrayList != null) {
                    for (ContactLoadedListner contactLoadedListner1 : contactLoadedListnerArrayList) {
                        contactLoadedListner1.contactLoaded(contactModalArrayList);
                    }
                }
                removeContactLoadListner(contactLoadedListner);
            }

            @Override
            protected ArrayList<ContactModal> doInBackground(Void... voids) {
                printLog(TAG + " contactLoadThread doInBackground.");
                contactLoadStatus = ContactLoadStatus.RUNNING;
                addContactLoadListner(contactLoadedListner);
                return getDeviceContacts(context.getContentResolver());
            }
        };
        contactLoadThread.execute();
    }

    public synchronized ArrayList<ContactModal> getDeviceContacts(ContentResolver cr) {
        ArrayList<ContactModal> contacts = new ArrayList<>();
        Cursor pCur = cr.query(
                ContactUris.PHONE_CONTENT_URI,
                new String[]{ContactUris.PHONE_NUMBER,
                        ContactUris.PHONE_DISPLAYNAME,
                        ContactUris.PHONE_CONTACT_ID,
                        ContactUris.PHONE_RAW_CONTACT_ID, ContactUris.PHONE_ROW_ID, ContactUris.CONTACT_ACCOUNT_URI}, null, null,
                ContactUris.PHONE_RAW_CONTACT_ID + " ASC");
        if (pCur != null) {
            if (pCur.getCount() > 0) {
                HashMap<Long, ArrayList<ContactModal>> phones = new HashMap<>();
                HashMap<String, String> numbersMap = new HashMap<>();
                while (pCur.moveToNext()) {
                    long PHONE_CONTACT_ID = pCur.getLong(pCur.getColumnIndex(ContactUris.PHONE_CONTACT_ID));
                    long PHONE_RAW_CONTACT_ID = pCur.getLong(pCur.getColumnIndex(ContactUris.PHONE_RAW_CONTACT_ID));
                    long PHONE_ROW_ID = pCur.getLong(pCur.getColumnIndex(ContactUris.PHONE_ROW_ID));
                    String MIME_TYPE = pCur.getString(pCur.getColumnIndex(ContactUris.CONTACT_ACCOUNT_URI));
                    String number = pCur.getString(pCur.getColumnIndex(ContactUris.PHONE_NUMBER));
                    if (number.trim().isEmpty()) {
                        continue;
                    }
                    number = number.replaceAll(" ", "");
                    if (!MIME_TYPE.equals("vnd.sec.contact.phone") && !MIME_TYPE.equals("com.google")) {
                        continue;
                    }
//                    if (numbersMap.containsKey(number)) {
//                        continue;
//                    }
                    ArrayList<ContactModal> curPhones = new ArrayList<>();
                    if (phones.containsKey(PHONE_CONTACT_ID)) {
                        curPhones = phones.get(PHONE_CONTACT_ID);
                    }
                    ContactModal contactModal = new ContactModal();
                    contactModal.setMyId(PHONE_CONTACT_ID);
                    contactModal.setRawContactId(PHONE_RAW_CONTACT_ID);
                    contactModal.setDeviceNumber(number);
                    contactModal.setRow_id(PHONE_ROW_ID);
                    contactModal.setMimeType(MIME_TYPE);
                    contactModal.setDisplayName(pCur.getString(pCur.getColumnIndex(ContactUris.PHONE_DISPLAYNAME)));
                    curPhones.add(contactModal);
                    phones.put(PHONE_CONTACT_ID, curPhones);
                    numbersMap.put(number, number);
                    contacts.add(contactModal);
                    Log.e("DEVICECONTACTS", contactModal.toString());

                }

//                Cursor cur = cr.query(
//                        ContactUris.CONTACT_CONTENT_URI,
//                        new String[]{ContactUris.CONTACT_ID, ContactUris.HAS_PHONE_NUMBER},
//                        ContactUris.HAS_PHONE_NUMBER + " > 0",
//                        null, "");
//                if (cur != null) {
//                    if (cur.getCount() > 0) {
//                        while (cur.moveToNext()) {
//                            long id = cur.getLong(cur.getColumnIndex(ContactUris.CONTACT_ID));
//                            if (phones.containsKey(id)) {
//                                contacts.addAll(phones.get(id));
//                            }
//                        }
//                    }
//                    cur.close();
//                }
            } else {
                printLog(TAG + " getDeviceContacts pCur count 0");
            }
            pCur.close();
        } else {
            printLog(TAG + " getDeviceContacts pCur is null");
        }
        return contacts;
    }

    public enum ContactLoadStatus {
        RUNNING, NOTRUNNING
    }

    public interface ContactLoadedListner {
        void contactLoaded(ArrayList<ContactModal> contactModalArrayList);

        void contactSyncingStart();
    }
}
