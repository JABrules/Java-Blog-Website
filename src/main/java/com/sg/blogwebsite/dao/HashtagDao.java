package com.sg.blogwebsite.dao;

import com.sg.blogwebsite.entities.Blogpost;
import com.sg.blogwebsite.entities.Hashtag;

import java.util.List;

public interface HashtagDao {
    List<Hashtag> getHashtags(boolean orderByPost);

    List<Hashtag> getHashtagsByPostID(int id);

    Hashtag getHashtagByID(int id);

    Hashtag addHashtag(Hashtag hashtag);

    void deleteHashtagByID(int id);
}
