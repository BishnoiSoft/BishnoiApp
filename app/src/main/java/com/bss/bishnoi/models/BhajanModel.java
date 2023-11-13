package com.bss.bishnoi.models;

public class BhajanModel {

    String title;
    String imageUrl;
    String audioUrl;
    String artist;
    String duration;
    String type;
    String lyrics;
    String keywords;
    String credits;
    String size;
    int downloads;

    String id;

    public BhajanModel() {
        // Empty constructor required
    }

    public BhajanModel(String id, String title, String imageUrl, String audioUrl, String artist, String duration, String type, String lyrics, String keywords, String size, int downloads) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.artist = artist;
        this.duration = duration;
        this.type = type;
        this.lyrics = lyrics;
        this.keywords = keywords;
        this.size = size;
        this.downloads = downloads;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

}
