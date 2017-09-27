package com.chatserver.contactdemo.model;

import io.realm.RealmObject;

/**
 * Created by ububtu on 26/7/17.
 * history contact list model
 */

public class HistoryContact extends RealmObject {
    long myId;
    long rawContactId;
    String displayName;
    String deviceNumber;
    int status;
    String changeNumber;
    String changeName;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getChangeNumber() {
        return changeNumber == null ? "" : changeNumber.equals("null") ? "" : changeNumber;
    }

    public void setChangeNumber(String changeNumber) {
        this.changeNumber = changeNumber;
    }

    public String getChangeName() {
        return changeName == null ? "" : changeName.equals("null") ? "" : changeName;
    }

    public void setChangeName(String changeName) {
        this.changeName = changeName;
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


    @Override
    public String toString() {
        return "raw_id=" + myId + ", display_name=" + displayName + ", device_number=" + deviceNumber;

    }
}
