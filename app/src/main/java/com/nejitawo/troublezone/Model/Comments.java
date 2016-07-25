package com.nejitawo.troublezone.Model;

import android.content.Context;

import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by devthehomes on 7/22/16.
 */
public class Comments {
    private String userName;
    private String comments;
    private Date postedDate;
    private String eventID;
    private String userLocation;
    private String userImageURL;
    private String incidentID;

    public String getIncidentID() {
        return incidentID;
    }

    public void setIncidentID(String incidentID) {
        this.incidentID = incidentID;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public static Comments giveFullDetails(ParseObject revision, Context context) {
        Comments t = new Comments();
        ParseObject docs = revision;
        try{
            t.setUserName((String) docs.get("username"));
            t.setUserImageURL((String) docs.get("userimage"));
t.setIncidentID((String)docs.get("incidentid"));
            t.setComments((String) docs.get("comments"));
            t.setUserLocation((String) docs.get("location"));
            t.setPostedDate((Date) docs.get("posteddate"));

        } catch (Exception e){
            e.printStackTrace();
        }
        return t;
    }
}
