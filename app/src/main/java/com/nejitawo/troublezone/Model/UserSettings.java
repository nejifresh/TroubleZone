package com.nejitawo.troublezone.Model;

/**
 * Created by devthehomes on 7/28/16.
 */
public class UserSettings {
    private String timeLineLocation;
    private Boolean enableAlerts;
    private int alertRadius;
    private Boolean enableSMS;
    private  String smsNumbers;
    private int noOfHomezones;
    private String activeHomeZone;
    private String userName;

    public String getTimeLineLocation() {
        return timeLineLocation;
    }

    public void setTimeLineLocation(String timeLineLocation) {
        this.timeLineLocation = timeLineLocation;
    }

    public Boolean getEnableAlerts() {
        return enableAlerts;
    }

    public void setEnableAlerts(Boolean enableAlerts) {
        this.enableAlerts = enableAlerts;
    }

    public int getAlertRadius() {
        return alertRadius;
    }

    public void setAlertRadius(int alertRadius) {
        this.alertRadius = alertRadius;
    }

    public Boolean getEnableSMS() {
        return enableSMS;
    }

    public void setEnableSMS(Boolean enableSMS) {
        this.enableSMS = enableSMS;
    }

    public String getSmsNumbers() {
        return smsNumbers;
    }

    public void setSmsNumbers(String smsNumbers) {
        this.smsNumbers = smsNumbers;
    }

    public int getNoOfHomezones() {
        return noOfHomezones;
    }

    public void setNoOfHomezones(int noOfHomezones) {
        this.noOfHomezones = noOfHomezones;
    }

    public String getActiveHomeZone() {
        return activeHomeZone;
    }

    public void setActiveHomeZone(String activeHomeZone) {
        this.activeHomeZone = activeHomeZone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
