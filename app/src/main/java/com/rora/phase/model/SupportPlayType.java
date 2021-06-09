package com.rora.phase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rora.phase.R;

public class SupportPlayType {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("type")
    @Expose
    private String playType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlayType() {
        return playType;
    }

    public void setPlayType(String playType) {
        this.playType = playType;
    }

    public enum Type {
        ALL(0),
        MOUSE_KEYBOARD(1),
        CONTROLLER(2),
        JOYSTICK(3),
        ONSCREEN_TOUCH(4),
        UN_DETECTED(5);

        private int value;
        Type(int value) {
            this.value = value;
        }

        public static Type convertFromValue(int supportPlayTypeId) {
            switch (supportPlayTypeId) {
                case 1: return MOUSE_KEYBOARD;
                case 2: return CONTROLLER;
                case 3: return JOYSTICK;
                case 4: return ONSCREEN_TOUCH;
                default: return UN_DETECTED;
            }
        }

        public static int getIconForType(int supportPlayTypeId) {
            switch (supportPlayTypeId) {
                case 1: return android.R.drawable.ic_dialog_info;
                case 2: return R.drawable.ic_library;
                case 3: return R.drawable.ic_home;
                case 4: return R.drawable.ic_settings;
                default: return R.drawable.ic_help;
            }
        }

        public int getValue() {
            return value;
        }
    }
}
