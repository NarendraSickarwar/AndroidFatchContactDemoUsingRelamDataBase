package com.chatserver.contactdemo.model;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ubuntu on 18/11/16.
 * recent contact list model
 */

public class ContactModal extends RealmObject {

    long myId;


    long rawContactId;

    @PrimaryKey
    long row_id;
    String displayName;
    String deviceNumber;
    boolean alreadyLocal = false;
    boolean newAdded = false;
    String mimeType;
    ContactModal tempContactModal;

    public long getRow_id() {
        return row_id;
    }

    public void setRow_id(long row_id) {
        this.row_id = row_id;
    }

    public String getMimeType() {
        return mimeType == null ? "" : mimeType.equals("null") ? "" : mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public ContactModal getTempContactModal() {
        return tempContactModal;
    }

    public void setTempContactModal(ContactModal tempContactModal) {
        this.tempContactModal = tempContactModal;
    }


    public long getRawContactId() {
        return rawContactId;
    }

    public void setRawContactId(long rawContactId) {
        this.rawContactId = rawContactId;
    }

    public long getMyId() {
        return myId;
    }

    public void setMyId(long myId) {
        this.myId = myId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }


    public boolean isAlreadyLocal() {
        return alreadyLocal;
    }

    public void setAlreadyLocal(boolean alreadyLocal) {
        this.alreadyLocal = alreadyLocal;
    }

    public boolean isNewAdded() {
        return newAdded;
    }

    public void setNewAdded(boolean newAdded) {
        this.newAdded = newAdded;
    }


    public boolean needChange() {
        if (tempContactModal == null) {
            return true;
        }
        if (tempContactModal.getDisplayName() != null || getDisplayName() != null) {
            if (tempContactModal.getDisplayName() != null && getDisplayName() != null) {
                if (!tempContactModal.getDisplayName().equals(getDisplayName())) {
                    return true;
                }
            } else if (tempContactModal.getDisplayName() != null && getDisplayName() == null) {
                return true;
            } else if (tempContactModal.getDisplayName() == null && getDisplayName() != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "row_id=" + row_id + "contact_id=" + myId + ", row_contact_id=" + rawContactId + ", display_name=" + displayName + ", device_number=" + deviceNumber + ", mimetype=" + mimeType;

    }

    @Override
    public boolean equals(Object obj) {
        ContactModal contactModal = (ContactModal) obj;
        return this.row_id == contactModal.getRow_id() && this.myId == contactModal.getMyId() && this.rawContactId == contactModal.getRawContactId() && this.displayName.equals(contactModal.getDisplayName()) && this.deviceNumber.equals(contactModal.getDeviceNumber());
    }
}