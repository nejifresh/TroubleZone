package com.nejitawo.troublezone;

import android.app.Application;

import com.parse.Parse;

import java.util.Date;

/**
 * Created by Neji on 19/06/2016.
 */

public class GlobalClass extends Application {

    private String id;
    private String imageURL;
    private String duration;
    private String mainTitle;
    private String sectionA;
    private String sectionB;
    private String status;

//For DJ App
    private String name;
    private String genre;

    private String fees;
    private String soundclip;
    private String phone;
    private String videoclip;
    private int rating;


    //For property
    private String title;
    private String address;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private String state;
    private String locality;
    private String description;
    private int toilets;
    private int bathrooms;
    private int kitchens;
    private int livingroom;
    private int bedrooms;
    private String category; //for sale or rent
    private  String agentid;
    private Integer price;
    private String tenure;
    private Date postedDate;
    private String otherinfo;
    private String mainimage;
    private String image2;
    private String image3;
    private String image4;
    private String image5;
    private String image6;
    private int lowerpricerange;

    private String incidentID;

    private int screenIndex;


    public void onCreate() {

       Parse.initialize(this, "zjA1LTTPUlHZbGivmj98m6DTUizEYlDwtohpthMf", "eWuprSjfSEHz1lCuVVzIT2yUEst6Nze9tf5W5FPT");

       // Parse.initialize(this,"8s58iALLEntU87Kl8tAS5sJFn1tkAwumIpu5zoGC", "MgjFQeyhyQjAt1a7kdEra1R8p85CjUfLA9dDYMKJ");
    }

/* For dj app*/


    public int getScreenIndex() {
        return screenIndex;
    }

    public void setScreenIndex(int screenIndex) {
        this.screenIndex = screenIndex;
    }

    public String getIncidentID() {
        return incidentID;
    }

    public void setIncidentID(String incidentID) {
        this.incidentID = incidentID;
    }

    public int getLowerpricerange() {
        return lowerpricerange;
    }

    public void setLowerpricerange(int lowerpricerange) {
        this.lowerpricerange = lowerpricerange;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public int getToilets() {
        return toilets;
    }

    public void setToilets(int toilets) {
        this.toilets = toilets;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public int getKitchens() {
        return kitchens;
    }

    public void setKitchens(int kitchens) {
        this.kitchens = kitchens;
    }

    public int getLivingroom() {
        return livingroom;
    }

    public void setLivingroom(int livingroom) {
        this.livingroom = livingroom;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getOtherinfo() {
        return otherinfo;
    }

    public void setOtherinfo(String otherinfo) {
        this.otherinfo = otherinfo;
    }

    public String getImage6() {
        return image6;
    }

    public void setImage6(String image6) {
        this.image6 = image6;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getSoundclip() {
        return soundclip;
    }

    public void setSoundclip(String soundclip) {
        this.soundclip = soundclip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVideoclip() {
        return videoclip;
    }

    public void setVideoclip(String videoclip) {
        this.videoclip = videoclip;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getMainimage() {
        return mainimage;
    }

    public void setMainimage(String mainimage) {
        this.mainimage = mainimage;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public String getImage5() {
        return image5;
    }

    public void setImage5(String image5) {
        this.image5 = image5;
    }

    /* For dj app*/
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
