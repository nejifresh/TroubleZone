package com.nejitawo.choices.Model;

import android.content.Context;

import com.parse.ParseObject;

/**
 * Created by Neji on 19/06/2016.
 */

public class Choices {
    private String title;
    private String id;
    private String description;
    private String imageURL;
    private String duration;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public static Choices giveFullDetails(ParseObject revision, Context context) {
        Choices t = new Choices();
        ParseObject docs = revision;
        try{
            t.setTitle((String) docs.get("title"));
            t.setDescription((String) docs.get("description"));
            t.setDuration((String) docs.get("duration"));
            t.setImageURL((String) docs.get("imageURL"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return t;
    }
}
