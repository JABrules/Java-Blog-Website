package com.sg.blogwebsite.dao;

import com.sg.blogwebsite.entities.Blogpost;
import com.sg.blogwebsite.entities.Hashtag;

import java.util.Date;
import java.util.List;

public interface BlogDao {
    List<Blogpost> getReviewedPosts();

    List<Blogpost> getUnreviewedPosts();

    List<Blogpost> getScheduledPosts();

    List<Blogpost> getExpiredPosts();

    List<Blogpost> getAllPosts();

    List<Blogpost> getReviewedPostsByHashtag(Hashtag hashtag);

    List<Blogpost> getAllPostsByHashtag(Hashtag hashtag);

    Blogpost getPostByID(int id);

    Blogpost addPost(Blogpost blogpost);

    void reviewPostByID(int id);

    void deletePostByID(int id);

    void deleteExpiredPosts();
}
