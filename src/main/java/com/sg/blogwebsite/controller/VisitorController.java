package com.sg.blogwebsite.controller;

import com.sg.blogwebsite.dao.BlogDaoDB;
import com.sg.blogwebsite.dao.HashtagDaoDB;
import com.sg.blogwebsite.entities.Blogpost;
import com.sg.blogwebsite.entities.Hashtag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// Controller class for visitors.
// Used for the blogposts, blogpost and blogpostsByHashtag pages.
@Controller
public class VisitorController {
    @Autowired
    BlogDaoDB blogDaoDB;

    @Autowired
    HashtagDaoDB hashtagDaoDB;

    //displays the blogposts.html page.
    //this page lists all currently public pages.
    @GetMapping("blogposts")
    public String displayBlogposts(Model model) {
        List<Blogpost> blogposts = blogDaoDB.getReviewedPosts();
        Blogpost.setTagStrings(blogposts, hashtagDaoDB);
        model.addAttribute("blogposts", blogposts);
        return "blogposts";
    }

    //displays the blogpostsByHashtag.html page.
    //this page displays all posts tagged with a certain hashtag.
    @GetMapping("blogpostsByHashtag")
    public String displayBlogpostsByHashtag(HttpServletRequest request, Model model) {
        List<Hashtag> hashtags = hashtagDaoDB.getHashtags(false);
        hashtags = Hashtag.removeDuplicateTagNames(hashtags);
        Hashtag.setBlogposts(hashtags, blogDaoDB);
        //the user can enter this page without having a tag selected, so we need to check if that happened.
        Hashtag hashtag;
        int hashtagID = 0;
        try {
            //check for the selected tag
            hashtagID = Integer.parseInt(request.getParameter("hashtagId"));
            hashtag = hashtagDaoDB.getHashtagByID(hashtagID);
        } catch (Exception e) {
            //if there isn't a tag selected, default to the first one in the database.
            hashtag = hashtags.get(0);
            hashtagID = hashtag.getId();
        }
        List<Blogpost> blogposts = blogDaoDB.getReviewedPostsByHashtag(hashtag);
        Blogpost.setTagStrings(blogposts, hashtagDaoDB);
        model.addAttribute("blogposts", blogposts);
        model.addAttribute("hashtags", hashtags);
        model.addAttribute("inHashtag", hashtag);
        return "blogpostsByHashtag";
    }

    //displays the blogpost.html page.
    //this page displays a single blogpost, including the title, post date, tags and content.
    @GetMapping("blogpost")
    public String displayBlogpost(HttpServletRequest request, Model model) {
        Blogpost blogpost;
        try {
            Integer blogpostID = Integer.parseInt(request.getParameter("id"));
            blogpost = blogDaoDB.getPostByID(blogpostID);
        } catch (Exception e) {
            //in case the user somehow selects a blogpost that doesn't exist, display the first one in the database instead.
            List<Blogpost> blogposts = blogDaoDB.getReviewedPosts();
            blogpost = blogposts.get(0);
        }

        blogpost.setTagString(hashtagDaoDB);
        model.addAttribute("blogpost", blogpost);
        return "blogpost";
    }
}
