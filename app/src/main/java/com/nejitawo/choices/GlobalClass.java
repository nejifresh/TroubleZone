package com.nejitawo.choices;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Neji on 19/06/2016.
 */

public class GlobalClass extends Application {

    private String title;
    private String id;
    private String description;
    private String imageURL;
    private String duration;
    private String mainTitle;
    private String sectionA;
    private String sectionB;
    private String status;




    public void onCreate() {

        Parse.initialize(this, "n0VBHgewAOslWgACZVcHLFM7XJRHEwdtNv48oAw4", "pZmfjbaZxzQZJ2xwYlbyodMLZMEkSSj5SyfNadQx");
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSectionA() {
        return sectionA;
    }

    public void setSectionA(String sectionA) {
        this.sectionA = sectionA;
    }

    public String getSectionB() {
        return sectionB;
    }

    public void setSectionB(String sectionB) {
        this.sectionB = sectionB;
    }
    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }
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
}
