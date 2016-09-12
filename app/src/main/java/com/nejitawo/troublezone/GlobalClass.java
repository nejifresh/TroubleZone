package com.nejitawo.troublezone;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
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
    private int homeID;
    private int EditOrCreate; //Integer variable defines whether we edit or create

    private Double userLat;
    private Double userLong;

    private String timeLineLocation;
    private Boolean enableAlerts;
    private int alertRadius;
    private Boolean enableSMS;
    private  String smsNumbers;
    private int noOfHomezones;
    private String activeHomeZone;
    private String userName;
    private String phone1;
    private String phone2;
    private String phone3;

    private Double lat1;
    private Double lon1;

    private Double lat2;
    private Double lon2;

    private Double lat3;
    private Double lon3;

    private Boolean isSubscribed;
    private Tracker mTracker;
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // Setting mTracker to Analytics Tracker declared in our xml Folder
            mTracker = analytics.newTracker(R.xml.analytics_tracker);
        }
        return mTracker;
    }

    public void onCreate() {

       Parse.initialize(this, "zjA1LTTPUlHZbGivmj98m6DTUizEYlDwtohpthMf", "eWuprSjfSEHz1lCuVVzIT2yUEst6Nze9tf5W5FPT");

       // Parse.initialize(this,"8s58iALLEntU87Kl8tAS5sJFn1tkAwumIpu5zoGC", "MgjFQeyhyQjAt1a7kdEra1R8p85CjUfLA9dDYMKJ");
    }

/* For dj app*/

    public Boolean getSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        isSubscribed = subscribed;
    }

    public Double getLat1() {
        return lat1;
    }

    public void setLat1(Double lat1) {
        this.lat1 = lat1;
    }

    public Double getLon1() {
        return lon1;
    }

    public void setLon1(Double lon1) {
        this.lon1 = lon1;
    }

    public Double getLat2() {
        return lat2;
    }

    public void setLat2(Double lat2) {
        this.lat2 = lat2;
    }

    public Double getLon2() {
        return lon2;
    }

    public void setLon2(Double lon2) {
        this.lon2 = lon2;
    }

    public Double getLat3() {
        return lat3;
    }

    public void setLat3(Double lat3) {
        this.lat3 = lat3;
    }

    public Double getLon3() {
        return lon3;
    }

    public void setLon3(Double lon3) {
        this.lon3 = lon3;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

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



    public int getEditOrCreate() {
        return EditOrCreate;
    }

    public void setEditOrCreate(int editOrCreate) {
        EditOrCreate = editOrCreate;
    }

    public int getHomeID() {
        return homeID;
    }

    public void setHomeID(int homeID) {
        this.homeID = homeID;
    }

    public Double getUserLat() {
        return userLat;
    }

    public void setUserLat(Double userLat) {
        this.userLat = userLat;
    }

    public Double getUserLong() {
        return userLong;
    }

    public void setUserLong(Double userLong) {
        this.userLong = userLong;
    }

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
