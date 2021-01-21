package com.rora.phase.model;

public class Game {

    private String id;
    private String name;
    private String imageSrc;

    public Game(String id, String name, String imageSrc) {
        this.id = id;
        this.name = name;
        this.imageSrc = imageSrc;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }
}
