package com.bss.bishnoi.models;

public class SahityaModel {

    String bookTitle, bookAuthor, pdfUrl, iconUrl, key;
    int views;

    public SahityaModel() {
    }

    public SahityaModel(String bookTitle, String bookAuthor, String pdfUrl, String iconUrl, int views, String key) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.pdfUrl = pdfUrl;
        this.iconUrl = iconUrl;
        this.views = views;
        this.key = key;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void incrementViews() {
        this.views++;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
