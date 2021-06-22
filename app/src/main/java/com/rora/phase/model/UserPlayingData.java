package com.rora.phase.model;

import androidx.annotation.Nullable;

import com.rora.phase.nvstream.http.ComputerDetails;

public class UserPlayingData {

    private double balance;
    private int playtime;

    public enum PlayingState {
        IDLE("0"),
        IN_PROGRESS("1"),
        IN_QUEUE("2"),
        PLAYING("3"),
        PAUSED("4"),
        IN_STOP_PROGRESS("5"),
        STOPPED("6");

        public final String id;

        PlayingState(String id) {
            this.id = id;
        }
    }

    public UserPlayingData() {
        this.balance = 0;
        this.playtime = 0;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getPlaytime() {
        return playtime;
    }

    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }
}
