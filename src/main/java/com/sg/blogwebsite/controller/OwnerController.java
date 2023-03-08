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

// Controller class for the owner.
// Used for the owner, ownerUnreviewed, ownerScheduled, ownerExpired,
// ownerBlogpostsByHashtag, ownerHashtags and ownerBlogpost pages.
@Controller
public class OwnerController {

    @Autowired
    BlogDaoDB blogDaoDB;

    @Autowired
    HashtagDaoDB hashtagDaoDB;

    //displays the owner.html page.
    //this page lists all currently public pages.
    @GetMapping("owner")
    public String displayOwnerBlogposts(Model model) {
        List<Blogpost> blogposts = blogDaoDB.getReviewedPosts();
        Blogpost.setTagStrings(blogposts, hashtagDaoDB);
        model.addAttribute("blogposts", blogposts);
        return "owner";
    }

    //displays the ownerUnreviewed.html page.
    //this page lists all posts awaiting review.
    @GetMapping("ownerUnreviewed")
    public String displayOwnerUnreviewedBlogposts(Model model) {
        List<Blogpost> blogposts = blogDaoDB.getUnreviewedPosts();
        Blogpost.setTagStrings(blogposts, hashtagDaoDB);
        model.addAttribute("blogposts", blogposts);
        return "ownerUnreviewed";
    }

    //displays the ownerScheduled.html page.
    //this page shows all posts that have not met their schedule date yet.
    @GetMapping("ownerScheduled")
    public String displayOwnerScheduledBlogposts(Model model) {
        List<Blogpost> blogposts = blogDaoDB.getScheduledPosts();
        Blogpost.setTagStrings(blogposts, hashtagDaoDB);
        model.addAttribute("blogposts", blogposts);
        return "ownerScheduled";
    }

    //displays the ownerExpired.html page.
    //this page shows all posts that are past their expiry date.
    @GetMapping("ownerExpired")
    public String displayOwnerExpiredBlogposts(Model model) {
        List<Blogpost> blogposts = blogDaoDB.getExpiredPosts();
        Blogpost.setTagStrings(blogposts, hashtagDaoDB);
        model.addAttribute("blogposts", blogposts);
        return "ownerExpired";
    }

    //displays the ownerBlogpost.html page.
    //this page displays a single blogpost, including the title, post date, tags and content.
    @GetMapping("ownerBlogpost")
    public String displayOwnerBlogpost(HttpServletRequest request, Model model) {
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
        return "ownerBlogpost";
    }

    //adds a reviewed post to the database.
    @PostMapping("addReviewedBlogpost")
    public String addReviewedBlogpost(HttpServletRequest request) {
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
                false
        );

        blogpost = blogDaoDB.addPost(blogpost);

        //all the hashtags added to the post are inputted as one string,
        //so they need to be split up and added.
        String tagString = request.getParameter("postTags");
        String[] tagNames = Hashtag.getTagNamesFromTagString(tagString);
        for (String tagName : tagNames) {
            hashtagDaoDB.addHashtag(new Hashtag(-1, blogpost.getId(), tagName));
        }

        return "redirect:/owner";
    }

    //adds an unreviewed post to the database.
    @PostMapping("ownerAddUnreviewedBlogpost")
    public String ownerAddUnreviewedBlogpost(HttpServletRequest request) {
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

        return "redirect:/ownerUnreviewed";
    }

    //sets an unreviewed blogpost as reviewed, making it public.
    @GetMapping("ownerReviewBlogpost")
    public String ownerReviewBlogpost(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        blogDaoDB.reviewPostByID(id);

        return "redirect:/ownerUnreviewed";
    }

    //deletes a blogpost from the reviewed page.
    @GetMapping("ownerDeleteBlogpost")
    public String ownerDeleteBlogpost(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        blogDaoDB.deletePostByID(id);

        return "redirect:/owner";
    }

    //deletes a blogpost from the unreviewed page.
    @GetMapping("ownerDeleteUnreviewedBlogpost")
    public String ownerDeleteUnreviewedBlogpost(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        blogDaoDB.deletePostByID(id);

        return "redirect:/ownerUnreviewed";
    }

    //deletes a blogpost from the scheduled page.
    @GetMapping("ownerDeleteScheduledBlogpost")
    public String ownerDeleteScheduledBlogpost(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        blogDaoDB.deletePostByID(id);

        return "redirect:/ownerScheduled";
    }

    //deletes a blogpost from the expired page.
    @GetMapping("ownerDeleteExpiredBlogpost")
    public String ownerDeleteExpiredBlogpost(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        blogDaoDB.deletePostByID(id);

        return "redirect:/ownerExpired";
    }

    //deletes all expired blogposts.
    @GetMapping("ownerExpiredDeleteAll")
    public String ownerExpiredDeleteAll(HttpServletRequest request) {
        blogDaoDB.deleteExpiredPosts();

        return "redirect:/ownerExpired";
    }

    //displays the ownerBlogpostsByHashtag.html page.
    //this page displays all posts tagged with a certain hashtag.
    @GetMapping("ownerBlogpostsByHashtag")
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
        List<Blogpost> blogposts = blogDaoDB.getAllPostsByHashtag(hashtag);
        Blogpost.setTagStrings(blogposts, hashtagDaoDB);
        model.addAttribute("blogposts", blogposts);
        model.addAttribute("hashtags", hashtags);
        model.addAttribute("inHashtag", hashtag);
        return "ownerBlogpostsByHashtag";
    }

    //displays the ownerHashtags.html page.
    //this page lists all the hashtags and allows the user to create more.
    @GetMapping("ownerHashtags")
    public String displayOwnerHashtags(Model model) {
        List<Blogpost> blogposts = blogDaoDB.getAllPosts();
        Blogpost.setTagStrings(blogposts, hashtagDaoDB);
        List<Hashtag> hashtags = hashtagDaoDB.getHashtags(true);
        Hashtag.setBlogposts(hashtags, blogDaoDB);
        model.addAttribute("blogposts", blogposts);
        model.addAttribute("hashtags", hashtags);
        return "ownerHashtags";
    }

    //adds a hashtag to the database.
    @PostMapping("addHashtag")
    public String addHashtag(HttpServletRequest request) {
        int blogpostID = Integer.parseInt(request.getParameter("tagPostId"));
        String tagName = request.getParameter("tagName");
        Hashtag hashtag = new Hashtag(-1, blogpostID, tagName);
        hashtagDaoDB.addHashtag(hashtag);
        return "redirect:/ownerHashtags";
    }

    //deletes a specific hashtag.
    @GetMapping("ownerDeleteHashtag")
    public String ownerDeleteHashtag(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        hashtagDaoDB.deleteHashtagByID(id);

        return "redirect:/ownerHashtags";
    }
}
