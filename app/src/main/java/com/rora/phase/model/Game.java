package com.rora.phase.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rora.phase.model.api.SearchSuggestion;
import com.rora.phase.model.ui.Media;

import java.util.ArrayList;
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
    @SerializedName("trailers")
    @Expose
    private List<MediaVideo> trailers;
    @SerializedName("gamePlayClips")
    @Expose
    private List<MediaVideo> gamePlays;
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
    @SerializedName("nowPlaying")
    @Expose
    private Integer nowPlaying;
    @SerializedName("rentingPrice")
    @Expose
    private Integer rentingPrice;
    @SerializedName("creditRequired")
    @Expose
    private Integer creditRequired;
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
    @SerializedName("payTypeId")
    @Expose
    private int payTypeId;
    @SerializedName("payTypeName")
    @Expose
    private String payTypeName;
    @SerializedName("payTypeDesc")
    @Expose
    private String payTypeDesc;
    @SerializedName("expiredDate")
    @Expose
    private String expiredDate;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("background")
    @Expose
    private MediaImage background;
    @SerializedName("tile")
    @Expose
    private String tile;
    @SerializedName("banner")
    @Expose
    private MediaImage banner;
    @SerializedName("seriesId")
    @Expose
    private int seriesId;
    @SerializedName("seriesName")
    @Expose
    private String seriesName;
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
    @SerializedName("platforms")
    @Expose
    private List<Platform> platforms;
    @SerializedName("favorited")
    @Expose
    private boolean favorited;

    public Game() {
    }

    public static Game init(SearchSuggestion selectedItem) {
        Game game = new Game();
        game.setId(selectedItem.getId());

        return game;
    }

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
        return desc == null ? "" : desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<MediaVideo> getTrailers() {
        if (trailers == null)
            new ArrayList<>();

        return trailers;
    }

    public void setTrailers(List<MediaVideo> trailers) {
        this.trailers = trailers;
    }

    public List<MediaVideo> getGamePlays() {
        return gamePlays;
    }

    public void setGamePlays(List<MediaVideo> gamePlays) {
        this.gamePlays = gamePlays;
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

    public int getPayTypeId() {
        return payTypeId;
    }

    public void setPayTypeId(int payTypeId) {
        this.payTypeId = payTypeId;
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName;
    }

    public String getPayTypeDesc() {
        payTypeDesc = "Free games for Members \nClick and play, no account needed";
        return payTypeDesc;
    }

    public void setPayTypeDesc(String payTypeDesc) {
        this.payTypeDesc = payTypeDesc;
    }

    public MediaImage getBanner() {
        return banner == null ? new Banner() : banner;
    }

    public void setBanner(MediaImage banner) {
        this.banner = banner;
    }

    public String getTile() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
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

    public MediaImage getBackground() {
        return background == null ? new MediaImage() : background;
    }

    public void setBackground(MediaImage background) {
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

    public boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public List<MediaVideo> getVideos() {
        List<MediaVideo> list = new ArrayList<>();
        if (getTrailers() != null && getTrailers().size() != 0)
            list.addAll(getTrailers());
        else if (getGamePlays() != null && getGamePlays().size() != 0)
            list.addAll(getGamePlays());

        return list;
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

    public String toJson() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public static Game fromJson(String json) {
        Gson gson = new Gson();

        return gson.fromJson(json, Game.class);
    }

    //--------------------------------------------------

}
