package com.rora.phase.model;

import androidx.annotation.Nullable;

import com.rora.phase.nvstream.http.ComputerDetails;

public class UserPlayingData {

    private PlayingState state;
    private ComputerDetails computer;

    public enum PlayingState {
        IDLE("0"),
        IN_PROGRESS("1"),
        PLAYING("2"),
        STOP("3");

        public final String id;

        PlayingState(String id) {
            this.id = id;
        }
    }

    public UserPlayingData() {
    }

    public UserPlayingData(@Nullable PlayingState state, @Nullable ComputerDetails computer) {
        this.state = state;
        this.computer = computer;
    }

    public UserPlayingData(PlayingState state) {
        this.state = state;
    }

    public UserPlayingData(ComputerDetails computer) {
        this.computer = computer;
    }

    public PlayingState getState() {
        return state;
    }

    public void setState(PlayingState state) {
        this.state = state;
    }

    public ComputerDetails getComputer() {
        return computer;
    }

    public void setComputer(ComputerDetails computer) {
        this.computer = computer;
    }
}
