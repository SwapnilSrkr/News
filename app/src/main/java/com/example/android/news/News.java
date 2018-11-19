package com.example.android.news;

public class News {
    private String mTitle;
    private String mSection;
    private String mAuthor;
    private String mThumbUrl;
    private String mDate;
    private String mNewsUrl;

    public News(String title, String section, String author, String thumbUrl, String date, String newsUrl) {
        this.mTitle = title;
        this.mSection = section;
        this.mAuthor = author;
        this.mThumbUrl = thumbUrl;
        this.mDate = date;
        this.mNewsUrl = newsUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getThumbUrl() {
        return mThumbUrl;
    }

    public String getDate() {
        return mDate;
    }

    public String getNewsUrl() {
        return mNewsUrl;
    }
}
