package com.rora.phase.model.ui;

import com.rora.phase.model.NumberOfPlayer;
import com.rora.phase.model.SupportPlayType;
import com.rora.phase.model.Tag;
import com.rora.phase.model.enums.PayTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilterParams {
    private String name;
    private SortType sortBy;
    private Platforms platforms;
    private PayTypes payTypes;
    private PlayTypes playTypes;
    private AgeRating pegis;
    private NoP numberOfPlayers;
    private FeatureGamesType featureGamesType;
    private boolean onlyHdr;
    private boolean onlyRayTracing;
    private boolean onlyDlss;
    private Resolutions resolutions;
    private List<Tag> tagList;

    public FilterParams() {
        sortBy = SortType.NONE;
        platforms = new Platforms();
        payTypes = new PayTypes();
        playTypes = new PlayTypes();
        pegis = new AgeRating();
        numberOfPlayers = new NoP();
        featureGamesType = FeatureGamesType.ALL;
        onlyHdr = false;
        onlyRayTracing = false;
        onlyDlss = false;
        resolutions = new Resolutions();
        tagList = new ArrayList<>();
    }

    public FilterParams(FilterParams filterParams) {
        this.name = filterParams.name;
        this.sortBy = filterParams.sortBy;

        this.platforms = new Platforms();
        this.platforms.all = filterParams.platforms.all;
        this.platforms.steam = filterParams.platforms.steam;
        this.platforms.battle = filterParams.platforms.battle;
        this.platforms.epic = filterParams.platforms.epic;
        this.platforms.origin = filterParams.platforms.origin;
        this.platforms.ubisoft = filterParams.platforms.ubisoft;
        this.platforms.garena = filterParams.platforms.garena;
        this.platforms.riot = filterParams.platforms.riot;

        this.payTypes = new PayTypes();
        this.payTypes.all = filterParams.payTypes.all;
        this.payTypes.free = filterParams.payTypes.free;
        this.payTypes.userAccount = filterParams.payTypes.userAccount;
        this.payTypes.licenceRequired = filterParams.payTypes.licenceRequired;
        this.payTypes.instancePlay = filterParams.payTypes.instancePlay;

        this.playTypes = new PlayTypes();
        this.playTypes.all = filterParams.playTypes.all;
        this.playTypes.mnk = filterParams.playTypes.mnk;
        this.playTypes.controller = filterParams.playTypes.controller;
        this.playTypes.joystick = filterParams.playTypes.joystick;
        this.playTypes.oscTouch = filterParams.playTypes.oscTouch;

        this.pegis = new AgeRating();
        this.pegis.all = filterParams.pegis.all;
        this.pegis._3 = filterParams.pegis._3;
        this.pegis._7 = filterParams.pegis._7;
        this.pegis._12 = filterParams.pegis._12;
        this.pegis._16 = filterParams.pegis._16;
        this.pegis._18 = filterParams.pegis._18;

        this.numberOfPlayers = new NoP();
        this.numberOfPlayers.all = filterParams.numberOfPlayers.all;
        this.numberOfPlayers.single = filterParams.numberOfPlayers.single;
        this.numberOfPlayers.coop = filterParams.numberOfPlayers.coop;
        this.numberOfPlayers.multi = filterParams.numberOfPlayers.multi;

        this.featureGamesType = filterParams.featureGamesType;

        this.onlyHdr = filterParams.onlyHdr;
        this.onlyRayTracing = filterParams.onlyRayTracing;
        this.onlyDlss = filterParams.onlyDlss;

        this.resolutions = new Resolutions();
        this.resolutions.all = filterParams.resolutions.all;
        this.resolutions._1280 = filterParams.resolutions._1280;
        this.resolutions._1920 = filterParams.resolutions._1920;
        this.resolutions._4k = filterParams.resolutions._4k;

        this.tagList = new ArrayList<>(filterParams.tagList);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SortType getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortType sortBy) {
        this.sortBy = sortBy;
    }

    public Platforms getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Platforms platforms) {
        this.platforms = platforms;
    }

    public PayTypes getPayTypes() {
        return payTypes;
    }

    public void setPayTypes(PayTypes payTypes) {
        this.payTypes = payTypes;
    }

    public PlayTypes getPlayTypes() {
        return playTypes;
    }

    public void setPlayTypes(PlayTypes playTypes) {
        this.playTypes = playTypes;
    }

    public AgeRating getAgeRatings() {
        return pegis;
    }

    public void setPegis(AgeRating pegis) {
        this.pegis = pegis;
    }

    public NoP getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(NoP numberOfPlayers) {
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

    public Resolutions getResolutions() {
        return resolutions;
    }

    public void setResolutions(Resolutions resolutions) {
        this.resolutions = resolutions;
    }

    public boolean isDefault() {
        return sortBy == SortType.NONE
                && platforms.all
                && payTypes.all
                && playTypes.all
                && pegis.all
                && numberOfPlayers.all
                && !onlyHdr
                && !onlyRayTracing
                && !onlyDlss
                && resolutions.all
                && getTags().size() == 0;

    }

    public void addTag(Tag selectedTag) {
        boolean isExist = false;
        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).equals(selectedTag)) {
                tagList.remove(i);
                isExist = true;
                break;
            }
        }

        if (!isExist)
            tagList.add(selectedTag);
    }

    public List<Tag> getTags() {
        return tagList == null ? new ArrayList<>() : tagList;
    }

    // Classes

    public class Platforms {
        public boolean all;
        public boolean steam;
        public boolean battle;
        public boolean epic;
        public boolean origin;
        public boolean ubisoft;
        public boolean garena;
        public boolean riot;

        public Platforms() {
            all = true;
            steam = false;
            battle = false;
            epic = false;
            origin = false;
            ubisoft = false;
            garena = false;
            riot = false;
        }

        public void setSteam(boolean steam) {
            this.all = !steam && !battle && !epic && !origin && !ubisoft && !garena && !riot;

            this.steam = steam;
        }

        public void setBattle(boolean battle) {
            this.all = !steam && !battle && !epic && !origin && !ubisoft && !garena && !riot;

            this.battle = battle;
        }

        public void setEpic(boolean epic) {
            this.all = !steam && !battle && !epic && !origin && !ubisoft && !garena && !riot;

            this.epic = epic;
        }

        public void setOrigin(boolean origin) {
            this.all = !steam && !battle && !epic && !origin && !ubisoft && !garena && !riot;

            this.origin = origin;
        }

        public void setUbisoft(boolean ubisoft) {
            this.all = !steam && !battle && !epic && !origin && !ubisoft && !garena && !riot;

            this.ubisoft = ubisoft;
        }

        public void setGarena(boolean garena) {
            this.all = !steam && !battle && !epic && !origin && !ubisoft && !garena && !riot;

            this.garena = garena;
        }

        public void setRiot(boolean riot) {
            this.all = !steam && !battle && !epic && !origin && !ubisoft && !garena && !riot;

            this.riot = riot;
        }
    }

    public class PayTypes {
        public boolean all;
        public boolean free;
        public boolean licenceRequired;
        public boolean instancePlay;
        public boolean userAccount;

        public PayTypes() {
            all = true;
            free = false;
            licenceRequired = false;
            instancePlay = false;
            userAccount = false;
        }

        public void setFree(boolean free) {
            this.all = !free && !licenceRequired && !userAccount && !instancePlay;

            this.free = free;
        }

        public void setLicenceRequired(boolean licenceRequired) {
            this.all = !free && !licenceRequired && !userAccount && !instancePlay;

            this.licenceRequired = licenceRequired;
        }

        public void setInstancePlay(boolean instancePlay) {
            this.all = !free && !licenceRequired && !userAccount && !instancePlay;

            this.instancePlay = instancePlay;
        }

        public void setUserAccount(boolean userAccount) {
            this.all = !free && !licenceRequired && !userAccount && !instancePlay;

            this.userAccount = userAccount;
        }
    }

    public class PlayTypes {
        public boolean all;
        public boolean mnk;
        public boolean controller;
        public boolean joystick;
        public boolean oscTouch;

        public PlayTypes() {
            all = true;
            mnk = false;
            controller = false;
            joystick = false;
            oscTouch = false;
        }

        public void setMnk(boolean mnk) {
            this.all = !mnk && !controller && !joystick && !oscTouch;

            this.mnk = mnk;
        }

        public void setController(boolean controller) {
            this.all = !mnk && !controller && !joystick && !oscTouch;

            this.controller = controller;
        }

        public void setJoystick(boolean joystick) {
            this.all = !mnk && !controller && !joystick && !oscTouch;

            this.joystick = joystick;
        }

        public void setOscTouch(boolean oscTouch) {
            this.all = !mnk && !controller && !joystick && !oscTouch;

            this.oscTouch = oscTouch;
        }
    }

    public class AgeRating {
        public boolean all;
        public boolean _3;
        public boolean _7;
        public boolean _12;
        public boolean _16;
        public boolean _18;

        public AgeRating() {
            all = true;
            _3 = false;
            _7 = false;
            _12 = false;
            _16 = false;
            _18 = false;
        }

        public void set_3(boolean _3) {
            this.all = !_3 && !_7 && !_12 && !_16 && !_18;

            this._3 = _3;
        }

        public void set_7(boolean _7) {
            this.all = !_3 && !_7 && !_12 && !_16 && !_18;

            this._7 = _7;
        }

        public void set_12(boolean _12) {
            this.all = !_3 && !_7 && !_12 && !_16 && !_18;

            this._12 = _12;
        }

        public void set_16(boolean _16) {
            this.all = !_3 && !_7 && !_12 && !_16 && !_18;

            this._16 = _16;
        }

        public void set_18(boolean _18) {
            this.all = !_3 && !_7 && !_12 && !_16 && !_18;

            this._18 = _18;
        }
    }

    public class NoP {
        public boolean all;
        public boolean single;
        public boolean coop;
        public boolean multi;

        public NoP() {
            all = true;
            single = false;
            coop = false;
            multi = false;
        }

        public void setSingle(boolean single) {
            this.all = !single && !coop && !multi;

            this.single = single;
        }

        public void setCoop(boolean coop) {
            this.all = !single && !coop && !multi;

            this.coop = coop;
        }

        public void setMulti(boolean multi) {
            this.all = !single && !coop && !multi;

            this.multi = multi;
        }
    }

    public enum Filter {
        ALL,
        SORT_BY,
        PAY_TYPES,
        PLAY_TYPES,
        PEGI,
        NUMBER_OF_PLAYER,
        BY_FEATURE,
        GAME_QLT,
        TAG,
        PLATFORMS;
    }

    public enum SortType {
        NONE,
        NEWEST,
        RELEASE,
        RATING,
        NOW_PLAYING
    }

    public enum FeatureGamesType {
        ALL,
        HOT,
        EDITOR_CHOICE,
        TRENDING,
        RECOMMENDED
    }

    public class Resolutions {
        public boolean all;
        public boolean _1280;
        public boolean _1920;
        public boolean _4k;

        public Resolutions() {
            all = true;
            _1280 = false;
            _1920 = false;
            _4k = false;
        }
    }

}
