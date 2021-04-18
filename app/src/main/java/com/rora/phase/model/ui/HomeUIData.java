package com.rora.phase.model.ui;

import android.content.Context;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;

import java.util.List;

import javax.annotation.Nullable;

public class HomeUIData {

    public enum Type {
        NEW(1),
        TRENDING(2),
        HOT(3),
        EDITOR(4),
        DISCOVER_BY_CATEGORY(5),
        ;

        private final int value;
        Type(int value) {
            this.value = value;
        }

        public static Type valueOf(int viewType) {
            switch (viewType) {
                case 1:
                    return NEW;
                case 2:
                    return TRENDING;
                case 3:
                    return HOT;
                case 4:
                    return EDITOR;
                case 5:
                    return DISCOVER_BY_CATEGORY;
                default:
                    return null;
            }
        }

        public int getValue() {
            return value;
        }
    }

    public Type type = Type.NEW;
    public List<Tag> tagList;
    public List<Game> gameList;

    public HomeUIData(Type type, List<Game> gameList, @Nullable List<Tag> tagList) {
        this.type = type;
        this.tagList = tagList;
        this.gameList = gameList;
    }

    public HomeUIData(Type type, List<Game> gameList) {
        this.type = type;
        this.gameList = gameList;
    }

    public String getSessionName(Context context) {
        switch (type) {
            case NEW:
                return context.getResources().getString(R.string.new_game_title);
            case TRENDING:
                return context.getResources().getString(R.string.trending_title);
            case EDITOR:
                return context.getResources().getString(R.string.editor_choice_title);
            case HOT:
                return context.getResources().getString(R.string.hot_title);
            case DISCOVER_BY_CATEGORY:
                return context.getResources().getString(R.string.discover_title);
            default:
                return "";
        }
    }

}
