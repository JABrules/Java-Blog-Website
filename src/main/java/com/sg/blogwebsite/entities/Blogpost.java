package com.sg.blogwebsite.entities;

import com.sg.blogwebsite.dao.HashtagDao;

import java.util.Date;
import java.util.List;

public class Blogpost {
    private int id;
    private String title;
    private String contents;
    private Date postDate;
    private Date expiryDate;
    private boolean needsReview;

    //the tagString is a single string containing all the tags on a post.
    //used for displaying said tags on the web-page.
    private String tagString = "";

    public Blogpost(int id, String title, String contents, Date postDate, Date expiryDate, boolean needsReview) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.postDate = postDate;
        this.expiryDate = expiryDate;
        this.needsReview = needsReview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean getNeedsReview() {
        return needsReview;
    }

    public void setNeedsReview(boolean needsReview) {
        this.needsReview = needsReview;
    }

    //sets the tag string variable.
    public void setTagString(HashtagDao hashtagDao) {
        List<Hashtag> hashtags = hashtagDao.getHashtagsByPostID(this.id);
        this.tagString = "";
        for (int i = 0; i < hashtags.size(); i++) {
            this.tagString += "#" + hashtags.get(i).getTagName() + (i == hashtags.size() - 1 ? "" : ", ");
        }
    }

    //sets the tag string variables for a list of blogpost objects.
    public static void setTagStrings(List<Blogpost> blogposts, HashtagDao hashtagDao) {
        for (Blogpost blogpost : blogposts) {
            blogpost.setTagString(hashtagDao);
        }
    }

    public String getTagString() {
        return tagString;
    }
}
