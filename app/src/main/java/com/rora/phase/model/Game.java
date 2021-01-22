package com.rora.phase.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Game implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("trailer")
    @Expose
    private String trailer;
    @SerializedName("releaseDate")
    @Expose
    private String releaseDate;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("share")
    @Expose
    private String share;
    @SerializedName("totalPlaytime")
    @Expose
    private Integer totalPlaytime;
    @SerializedName("pay")
    @Expose
    private Boolean pay;
    @SerializedName("nowPlaying")
    @Expose
    private Integer nowPlaying;
    @SerializedName("rentingPrice")
    @Expose
    private Integer rentingPrice;
    @SerializedName("creditRequired")
    @Expose
    private Integer creditRequired;
    @SerializedName("fileName")
    @Expose
    private String fileName;
    @SerializedName("extensionPackages")
    @Expose
    private String extensionPackages;
    @SerializedName("pegiAge")
    @Expose
    private Integer pegiAge;
    @SerializedName("numberOfAvailableRentDay")
    @Expose
    private Integer numberOfAvailableRentDay;
    @SerializedName("addedDate")
    @Expose
    private String addedDate;
    @SerializedName("platformId")
    @Expose
    private Integer platformId;
    @SerializedName("platformName")
    @Expose
    private String platformName;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("expiredDate")
    @Expose
    private String expiredDate;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("background")
    @Expose
    private String background;
    @SerializedName("screenshots")
    @Expose
    private List<Screenshot> screenshots = null;
    @SerializedName("pictures")
    @Expose
    private List<Picture> pictures = null;
    @SerializedName("gameAccounts")
    @Expose
    private List<Integer> gameAccounts = null;
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;
    @SerializedName("supportPlayTypes")
    @Expose
    private List<SupportPlayType> supportPlayTypes = null;
    @SerializedName("numberOfPlayers")
    @Expose
    private List<NumberOfPlayer> numberOfPlayers = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public Integer getTotalPlaytime() {
        return totalPlaytime;
    }

    public void setTotalPlaytime(Integer totalPlaytime) {
        this.totalPlaytime = totalPlaytime;
    }

    public Boolean getPay() {
        return pay;
    }

    public void setPay(Boolean pay) {
        this.pay = pay;
    }

    public Integer getNowPlaying() {
        return nowPlaying;
    }

    public void setNowPlaying(Integer nowPlaying) {
        this.nowPlaying = nowPlaying;
    }

    public Integer getRentingPrice() {
        return rentingPrice;
    }

    public void setRentingPrice(Integer rentingPrice) {
        this.rentingPrice = rentingPrice;
    }

    public Integer getCreditRequired() {
        return creditRequired;
    }

    public void setCreditRequired(Integer creditRequired) {
        this.creditRequired = creditRequired;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtensionPackages() {
        return extensionPackages;
    }

    public void setExtensionPackages(String extensionPackages) {
        this.extensionPackages = extensionPackages;
    }

    public Integer getPegiAge() {
        return pegiAge;
    }

    public void setPegiAge(Integer pegiAge) {
        this.pegiAge = pegiAge;
    }

    public Integer getNumberOfAvailableRentDay() {
        return numberOfAvailableRentDay;
    }

    public void setNumberOfAvailableRentDay(Integer numberOfAvailableRentDay) {
        this.numberOfAvailableRentDay = numberOfAvailableRentDay;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public List<Screenshot> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<Screenshot> screenshots) {
        this.screenshots = screenshots;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public List<Integer> getGameAccounts() {
        return gameAccounts;
    }

    public void setGameAccounts(List<Integer> gameAccounts) {
        this.gameAccounts = gameAccounts;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<SupportPlayType> getSupportPlayTypes() {
        return supportPlayTypes;
    }

    public void setSupportPlayTypes(List<SupportPlayType> supportPlayTypes) {
        this.supportPlayTypes = supportPlayTypes;
    }

    public List<NumberOfPlayer> getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(List<NumberOfPlayer> numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }


    //---------- PARCELABLE FUNCTION ----------

    public static final Creator<Game> CREATOR = new Creator<Game>() {

        @Override
        public Game createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    //--------------------------------------------------

}
