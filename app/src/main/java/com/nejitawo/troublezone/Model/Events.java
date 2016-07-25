package com.nejitawo.troublezone.Model;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.nejitawo.troublezone.GlobalClass;
import com.parse.ParseObject;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by devthehomes on 7/19/16.
 */
public class Events implements Comparable<Events> {
    private String eventType;
    private String senderName;
    private Date postedDate;
    private String description;
    private String locality;
    private String id;
    private static float mydistance = 0;
    private String distanceAway;
    private float distanceFrom;
    private String videoclip;
    private String phone;
    private Double latitude;
    private Double longitude;
    private String image;
    private String seenstatus;
    private String contentType;
    private String userImage;

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSeenstatus() {
        return seenstatus;
    }

    public void setSeenstatus(String seenstatus) {
        this.seenstatus = seenstatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static float getMydistance() {
        return mydistance;
    }

    public static void setMydistance(float mydistance) {
        Events.mydistance = mydistance;
    }

    public String getDistanceAway() {
        return distanceAway;
    }

    public void setDistanceAway(String distanceAway) {
        this.distanceAway = distanceAway;
    }

    public float getDistanceFrom() {
        return distanceFrom;
    }

    public void setDistanceFrom(float distanceFrom) {
        this.distanceFrom = distanceFrom;
    }

    public String getVideoclip() {
        return videoclip;
    }

    public void setVideoclip(String videoclip) {
        this.videoclip = videoclip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public static Events giveFullDetails(ParseObject revision, Context context) {
        /**
         * SET LOCATION FROM USER TO WHERE DJ IS
         * **/

        Events t = new Events();
        ParseObject docs = revision;
        final GlobalClass globalVariable = (GlobalClass) context;

        Location locationA = new Location("point A");
        locationA.setLatitude((globalVariable.getLatitude()));  //(GPS CAPTURED LATITUDE
        locationA.setLongitude((globalVariable.getLongitude())); //GPS CAPTURED LONGITUDE;

        //   Location firstLocation = myLocation.getLocation();
        Location LocationB = new Location("point B");
        LocationB.setLatitude(((Double) docs.get("latitude")));
        LocationB.setLongitude(((Double) docs.get("longitude")));

        mydistance = locationA.distanceTo(LocationB);

        try{
            t.setEventType((String) docs.get("incident"));
            t.setDescription((String) docs.get("description"));
            t.setImage((String) docs.get("mainimage"));
            t.setLocality((String) docs.get("location"));
            t.setPostedDate((Date) docs.get("datetimeof"));
            t.setContentType((String) docs.get("contenttype"));
            t.setId((String) docs.getObjectId());
            t.setSenderName((String) docs.get("user"));
            t.setSeenstatus((String)docs.get("status"));
            t.setLatitude((Double)docs.get("latitude"));
            t.setLongitude((Double)docs.get("longitude"));
            t.setUserImage((String)docs.get("userimage"));
            t.setDistanceFrom(mydistance);
            t.setDistanceAway((String.valueOf(roundTwoDecimals(mydistance / 1000) + " km away")));

        } catch (Exception e){
            e.printStackTrace();
            Log.e("tease","Error" + e.getMessage());
        }
        return t;
    }

    public Location getLocation(Double latitude, Double longitude){
        Location itemLocation = new Location("ItemLocation");
        itemLocation.setLatitude(latitude);
        itemLocation.setLongitude(longitude);

        return itemLocation;
    }

    private static float roundTwoDecimals(float d)
    {
        DecimalFormat twoDForm = new DecimalFormat("####.#");
        return Float.valueOf(twoDForm.format(d));

    }

    public int compareTo(Events compareItems) {

        float compareDistance = ((Events) compareItems).getDistanceFrom();

        //ascending order - nearest locations shown first

        return (int)(this.distanceFrom - compareDistance);

        //descending order - farthest locations shown first
        //return compareQuantity - this.quantity;

    }
}
