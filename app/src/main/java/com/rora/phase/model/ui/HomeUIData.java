package com.rora.phase.model.ui;

import android.content.Context;

import com.rora.phase.R;
import com.rora.phase.model.Game;

import java.util.List;

public class HomeUIData {

    public enum Type {
        NEW(1),
        TRENDING(2),
        HOT(3),
        EDITOR(4),
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
                default:
                    return null;
            }
        }

        public int getValue() {
            return value;
        }
    }

    public Type type = Type.NEW;
    public List<Game> gameList;

    public HomeUIData(Type type, List<Game> gameList) {
        this.type = type;
        this.gameList = gameList;
    }

    public String getSessionName(Context context) {
        String sessionName = "";
        switch (type) {
            case NEW:
                sessionName = context.getResources().getString(R.string.new_game_title);
                break;
            case TRENDING:
                sessionName = context.getResources().getString(R.string.trending_title);
                break;
            case EDITOR:
                sessionName = context.getResources().getString(R.string.editor_choice_title);
                break;
            case HOT:
                sessionName = context.getResources().getString(R.string.hot_title);
                break;
            default:
                sessionName = "";
                break;
        }
        return sessionName;
    }

}
