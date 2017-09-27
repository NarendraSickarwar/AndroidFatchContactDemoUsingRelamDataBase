package com.chatserver.contactdemo.helpers;

import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by ubuntu on 18/11/16.
 */

public class ContactUris {


    public static final Uri CONTACT_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

    public static final String CONTACT_ID = ContactsContract.Contacts._ID;
    public static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

    public static final String CONTACT_ACCOUNT_URI = ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET;
    public static final Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    public static final String PHONE_ROW_ID = ContactsContract.CommonDataKinds.Phone._ID;

    public static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    public static final String PHONE_RAW_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID;
    public static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    public static final String PHONE_DISPLAYNAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

}
