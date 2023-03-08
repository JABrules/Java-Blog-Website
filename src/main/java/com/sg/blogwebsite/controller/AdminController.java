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
import org.springframework.web.bind.annotation.PostMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// Controller class for admins (can only create unreviewed blog posts).
// Used for the admin and adminBlogpost pages.
@Controller
public class AdminController {

    @Autowired
    BlogDaoDB blogDaoDB;

    @Autowired
    HashtagDaoDB hashtagDaoDB;

    //displays the admin.html page.
    //this page lists all posts awaiting review, and lets the admin add more.
    @GetMapping("admin")
    public String displayAdminBlogposts(Model model) {
        List<Blogpost> blogposts = blogDaoDB.getUnreviewedPosts();
        Blogpost.setTagStrings(blogposts, hashtagDaoDB);
        model.addAttribute("blogposts", blogposts);
        return "admin";
    }

    //displays the adminBlogpost.html page.
    //this page displays a single blogpost, including the title, post date, tags and content.
    @GetMapping("adminBlogpost")
    public String displayAdminBlogpost(HttpServletRequest request, Model model) {
        Blogpost blogpost;
        try {
            Integer blogpostID = Integer.parseInt(request.getParameter("id"));
            blogpost = blogDaoDB.getPostByID(blogpostID);
        } catch (Exception e) {
            //in case the user somehow selects a blogpost that doesn't exist, display the first one in the database instead.
            List<Blogpost> blogposts = blogDaoDB.getUnreviewedPosts();
            blogpost = blogposts.get(0);
        }

        blogpost.setTagString(hashtagDaoDB);
        model.addAttribute("blogpost", blogpost);
        return "adminBlogpost";
    }

    //adds an unreviewed post to the database.
    @PostMapping("addUnreviewedBlogpost")
    public String addUnreviewedBlogpost(HttpServletRequest request) {
        String title = request.getParameter("postTitle");
        String contents = request.getParameter("postContents");

        //the schedule and expiry dates are both optional, so some extra code is required.
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date scheduledDate, expiryDate;
        try {
            //attempt to set the date given. this will throw an error if the parameter is blank (or not formatted correctly)
            expiryDate = simpleDateFormat.parse(request.getParameter("postExpiryDate"));
            System.out.println("Post expires on " + expiryDate.toString());
        } catch (Exception e) {
            //if so, set the expiry date to way in the future instead.
            System.out.println("no expiry date");
            try {
                expiryDate = simpleDateFormat.parse("2100-01-01");
            } catch (ParseException ex) {
                //this code will never trigger but is required to let the program compile.
                System.out.println("bruh");
                expiryDate = new Date(0);
            }
        }

        try {
            //attempt to set the date given. this will throw an error if the parameter is blank (or not formatted correctly)
            scheduledDate = simpleDateFormat.parse(request.getParameter("postScheduledDate"));
            System.out.println("Post is scheduled for " + scheduledDate.toString());
        } catch (Exception e) {
            //if so, set the schedule date to the current date instead.
            System.out.println("no scheduled date");
            scheduledDate = new Date();
        }

        Blogpost blogpost = new Blogpost(
                -1,
                title,
                contents,
                scheduledDate,
                expiryDate,
                true
        );

        blogpost = blogDaoDB.addPost(blogpost);

        //all the hashtags added to the post are inputted as one string,
        //so they need to be split up and added.
        String tagString = request.getParameter("postTags");
        String[] tagNames = Hashtag.getTagNamesFromTagString(tagString);
        for (String tagName : tagNames) {
            hashtagDaoDB.addHashtag(new Hashtag(-1, blogpost.getId(), tagName));
        }

        return "redirect:/admin";
    }
}
