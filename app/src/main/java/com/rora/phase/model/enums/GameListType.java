package com.rora.phase.model.enums;

import com.rora.phase.model.ui.HomeUIData;

public enum GameListType {
    NONE,
    NEW,
    HOT,
    EDITOR,
    TRENDING,
    BY_CATEGORY,
    BY_PAY_TYPE,
    RECOMMENDED;

    public static GameListType getTypeFromHomeListType(HomeUIData.Type homeType) {
        switch (homeType) {
            case HOT:
                return HOT;
            case EDITOR:
                return EDITOR;
            case TRENDING:
                return TRENDING;
            case NEW:
                return NEW;
            case DISCOVER_BY_CATEGORY:
                return BY_CATEGORY;
            default:
                return null;
        }
    }
}
