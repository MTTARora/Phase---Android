package com.rora.phase.model.api;

import com.rora.phase.model.ui.FilterParams;

import java.util.HashMap;

import static com.rora.phase.model.ui.FilterParams.SortType.NEWEST;
import static com.rora.phase.model.ui.FilterParams.SortType.NONE;
import static com.rora.phase.model.ui.FilterParams.SortType.NOW_PLAYING;
import static com.rora.phase.model.ui.FilterParams.SortType.RATING;
import static com.rora.phase.model.ui.FilterParams.SortType.RELEASE;

public class FilterQuery extends HashMap<String, String> {

    public FilterQuery(FilterParams params) {
        if (params.getName() != null && !params.getName().isEmpty())
            put("name", params.getName());
        //tag

        if (params.getSortBy() != NONE) {
            String sortBy = "";
            if (params.getSortBy() == NEWEST)
                sortBy = "addedDate";
            if (params.getSortBy() == RELEASE)
                sortBy = "releaseDate";
            if (params.getSortBy() == RATING)
                sortBy = "rating";
            if (params.getSortBy() == NOW_PLAYING)
                sortBy = "nowPlaying";

            put(sortBy, "true");
        }

        if (params.getFeatureGamesType() != FilterParams.FeatureGamesType.ALL) {
            String feature = "";
            if (params.getFeatureGamesType() == FilterParams.FeatureGamesType.HOT)
                feature = "hotGame";
            if (params.getFeatureGamesType() == FilterParams.FeatureGamesType.EDITOR_CHOICE)
                feature = "editorChoice";
            if (params.getFeatureGamesType() == FilterParams.FeatureGamesType.TRENDING)
                feature = "trending";
            if (params.getFeatureGamesType() == FilterParams.FeatureGamesType.RECOMMENDED)
                feature = "recommended";

            put(feature, "true");
        }

        if (!params.getPlatforms().all) {
            String platforms = "";
            if (params.getPlatforms().steam)
                platforms = "1";
            if (params.getPlatforms().battle)
                platforms += platforms.isEmpty() ? "2" : ",2";
            if (params.getPlatforms().epic)
                platforms += platforms.isEmpty() ? "3" : ",3";
            if (params.getPlatforms().origin)
                platforms += platforms.isEmpty() ? "4" : ",4";
            if (params.getPlatforms().ubisoft)
                platforms += platforms.isEmpty() ? "5" : ",5";
            if (params.getPlatforms().garena)
                platforms += platforms.isEmpty() ? "6" : ",6";
            if (params.getPlatforms().riot)
                platforms += platforms.isEmpty() ? "7" : ",7";

            put("platforms", platforms);
        }

        if (!params.getPayTypes().all) {
            String payTypes = "";
            if (params.getPayTypes().free)
                payTypes = "1";
            if (params.getPayTypes().licenceRequired)
                payTypes += payTypes.isEmpty() ? "2" : ",2";
            if (params.getPayTypes().instancePlay)
                payTypes += payTypes.isEmpty() ? "3" : ",3";
            if (params.getPayTypes().userAccount)
                payTypes += payTypes.isEmpty() ? "4" : ",4";

            put("payType", payTypes);
        }

        if (!params.getPlayTypes().all) {
            String playTypes = "";
            if (params.getPlayTypes().mnk)
                playTypes = "1";
            if (params.getPlayTypes().controller)
                playTypes += playTypes.isEmpty() ? "2" : ",2";
            if (params.getPlayTypes().joystick)
                playTypes += playTypes.isEmpty() ? "3" : ",3";
            if (params.getPlayTypes().oscTouch)
                playTypes += playTypes.isEmpty() ? "4" : ",4";

            put("sp", playTypes);
        }

        if (!params.getAgeRatings().all) {
            String ageRatings = "";
            if (params.getAgeRatings()._3)
                ageRatings = "3";
            if (params.getAgeRatings()._7)
                ageRatings += ageRatings.isEmpty() ? "7" : ",7";
            if (params.getAgeRatings()._12)
                ageRatings += ageRatings.isEmpty() ? "12" : ",12";
            if (params.getAgeRatings()._16)
                ageRatings += ageRatings.isEmpty() ? "16" : ",16";
            if (params.getAgeRatings()._18)
                ageRatings += ageRatings.isEmpty() ? "18" : ",18";

            put("pegiAge", ageRatings);
        }

        if (!params.getNumberOfPlayers().all) {
            String nops = "";
            if (params.getNumberOfPlayers().single)
                nops = "1";
            if (params.getNumberOfPlayers().coop)
                nops += nops.isEmpty() ? "2" : ",2";
            if (params.getNumberOfPlayers().multi)
                nops += nops.isEmpty() ? "3" : ",3";

            put("nop", nops);
        }

        if (params.isOnlyHdr())
            put("hdr", "true");

        if (params.isOnlyRayTracing())
            put("rayTracing", "true");

        if (params.isOnlyDlss())
            put("dlss", "true");


        // Resolutions



    }
}
