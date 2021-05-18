package com.rora.phase.model.ui;

import com.rora.phase.model.NumberOfPlayer;
import com.rora.phase.model.SupportPlayType;
import com.rora.phase.model.enums.PayTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilterParams implements Serializable {

    private Filter filterType;
    private SortType sortBy;
    private List<PayTypeEnum> payTypes;
    private List<SupportPlayType.Type> playTypes;
    private List<PEGI> pegis;
    private List<NumberOfPlayer.Type> numberOfPlayers;
    private FeatureGamesType featureGamesType;
    private boolean onlyHdr;
    private boolean onlyRayTracing;
    private boolean onlyDlss;
    private List<Resolution> resolutions;

    public FilterParams() {
        filterType = Filter.ALL;
        sortBy = SortType.NONE;
        payTypes = new ArrayList<>();
        playTypes = new ArrayList<>();
        pegis = new ArrayList<>();
        numberOfPlayers = new ArrayList<>();
        featureGamesType = FeatureGamesType.ALL;
        onlyHdr = false;
        onlyRayTracing = false;
        onlyDlss = false;
        resolutions = new ArrayList<>();
    }

    public Filter getFilterType() {
        return filterType;
    }

    public void setFilterType(Filter filterType) {
        this.filterType = filterType;
    }

    public SortType getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortType sortBy) {
        this.sortBy = sortBy;
    }

    public List<PayTypeEnum> getPayTypes() {
        return payTypes;
    }

    public void setPayTypes(List<PayTypeEnum> payTypes) {
        this.payTypes = payTypes;
    }

    public List<SupportPlayType.Type> getPlayTypes() {
        return playTypes;
    }

    public void setPlayTypes(List<SupportPlayType.Type> playTypes) {
        this.playTypes = playTypes;
    }

    public List<PEGI> getAgeRatings() {
        return pegis;
    }

    public void setPegis(List<PEGI> pegis) {
        this.pegis = pegis;
    }

    public List<NumberOfPlayer.Type> getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(List<NumberOfPlayer.Type> numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public FeatureGamesType getFeatureGamesType() {
        return featureGamesType;
    }

    public void setFeatureGamesType(FeatureGamesType featureGamesType) {
        this.featureGamesType = featureGamesType;
    }

    public boolean isOnlyHdr() {
        return onlyHdr;
    }

    public void setOnlyHdr(boolean onlyHdr) {
        this.onlyHdr = onlyHdr;
    }

    public boolean isOnlyRayTracing() {
        return onlyRayTracing;
    }

    public void setOnlyRayTracing(boolean onlyRayTracing) {
        this.onlyRayTracing = onlyRayTracing;
    }

    public boolean isOnlyDlss() {
        return onlyDlss;
    }

    public void setOnlyDlss(boolean onlyDlss) {
        this.onlyDlss = onlyDlss;
    }

    public List<Resolution> getResolutions() {
        return resolutions;
    }

    public void setResolutions(List<Resolution> resolutions) {
        this.resolutions = resolutions;
    }

    // Classes

    public enum Filter {
        ALL,
        SORT_BY,
        PAY_TYPES,
        PLAY_TYPES,
        PEGI,
        NUMBER_OF_PLAYER,
        BY_FEATURE,
        GAME_QLT,
        TAG
    }

    public enum SortType {
        NONE,
        NEWEST,
        RELEASE,
        RATING,
        NOW_PLAYING
    }

    public enum PEGI {
        _0,
        _3,
        _7,
        _12,
        _16,
        _18,
    }

    public enum FeatureGamesType {
        ALL,
        HOT,
        EDITOR_CHOICE,
        TRENDING,
        RECOMMENDED
    }

    public enum Resolution {
        _1280,
        _1920,
        _4k
    }

}
