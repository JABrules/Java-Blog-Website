package com.sg.blogwebsite.entities;

import com.sg.blogwebsite.dao.BlogDao;
import com.sg.blogwebsite.dao.HashtagDao;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Hashtag {
    private int id;
    private int blogpostID;
    private Blogpost blogpost;
    private String tagName;

    public Hashtag(int id, int blogpostID, String tagName) {
        this.id = id;
        this.blogpostID = blogpostID;
        this.tagName = tagName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBlogpostID() {
        return blogpostID;
    }

    public void setBlogpostID(int blogpostID) {
        this.blogpostID = blogpostID;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public static List<Hashtag> removeDuplicateTagNames(List<Hashtag> inHashtags) {
        List<Hashtag> outHashtags = new ArrayList<>();

        for (Hashtag inH : inHashtags) {
            boolean shouldInsert = true;
            //System.out.println("Checking " + inH.getTagName());
            for (Hashtag outH : outHashtags) {
                //System.out.println("On " + outH.getTagName());
                if (inH.getTagName().equalsIgnoreCase(outH.getTagName())) {
                    shouldInsert = false;
                }
            }
            if (shouldInsert) {
                outHashtags.add(inH);
            }
        }

        return outHashtags;
    }

    public Blogpost getBlogpost() {
        return blogpost;
    }

    //sets the blogpost variable.
    public void setBlogpost(BlogDao blogDao) {
        blogpost = blogDao.getPostByID(blogpostID);
    }

    //sets the blogpost variables for a list of hashtag objects.
    public static void setBlogposts(List<Hashtag> hashtags, BlogDao blogDao) {
        for (Hashtag hashtag : hashtags) {
            hashtag.setBlogpost(blogDao);
        }
    }

    //takes in a string of space-separated words and splits them.
    public static String[] getTagNamesFromTagString(String string) {
        return string.split(" ");
    }
}
