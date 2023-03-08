package com.sg.blogwebsite.dao;

import com.sg.blogwebsite.entities.Blogpost;
import com.sg.blogwebsite.entities.Hashtag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class BlogDaoDB implements BlogDao {

    @Autowired
    JdbcTemplate jdbc;

    @Override
    public List<Blogpost> getReviewedPosts() {
        final String GET_REVIEWED_POSTS = "SELECT * FROM blogposts " +
                "WHERE needsreview = false AND postDate <= ? AND expiryDate >= ?";
        return jdbc.query(GET_REVIEWED_POSTS, new BlogpostMapper(), new Date(), new Date());
    }

    @Override
    public List<Blogpost> getUnreviewedPosts() {
        final String GET_UNREVIEWED_POSTS = "SELECT * FROM blogposts " +
                "WHERE needsreview = true AND postDate <= ? AND expiryDate >= ?";
        return jdbc.query(GET_UNREVIEWED_POSTS, new BlogpostMapper(), new Date(), new Date());
    }

    @Override
    public List<Blogpost> getScheduledPosts() {
        final String GET_SCHEDULED_POSTS = "SELECT * FROM blogposts WHERE postDate > ?";
        return jdbc.query(GET_SCHEDULED_POSTS, new BlogpostMapper(), new Date());
    }

    @Override
    public List<Blogpost> getExpiredPosts() {
        final String GET_EXPIRED_POSTS = "SELECT * FROM blogposts WHERE expiryDate < ?";
        return jdbc.query(GET_EXPIRED_POSTS, new BlogpostMapper(), new Date());
    }

    @Override
    public List<Blogpost> getAllPosts() {
        final String GET_ALL_POSTS = "SELECT * FROM blogposts";
        return jdbc.query(GET_ALL_POSTS, new BlogpostMapper());
    }

    @Override
    public List<Blogpost> getReviewedPostsByHashtag(Hashtag hashtag) {
        final String GET_REVIEWED_POSTS_BY_HASHTAG = "SELECT blogposts.* FROM blogposts " +
                "LEFT JOIN hashtags " +
                "ON blogposts.id = hashtags.blogpostid " +
                "WHERE needsreview = false AND hashtags.tagname = ? " +
                "AND postDate <= ? AND expiryDate >= ?";
        return jdbc.query(GET_REVIEWED_POSTS_BY_HASHTAG, new BlogpostMapper(), hashtag.getTagName(), new Date(), new Date());
    }

    @Override
    public List<Blogpost> getAllPostsByHashtag(Hashtag hashtag) {
        final String GET_ALL_POSTS_BY_HASHTAG = "SELECT blogposts.* FROM blogposts " +
                "LEFT JOIN hashtags " +
                "ON blogposts.id = hashtags.blogpostid " +
                "WHERE hashtags.tagname = ?";
        return jdbc.query(GET_ALL_POSTS_BY_HASHTAG, new BlogpostMapper(), hashtag.getTagName());
    }

    @Override
    public Blogpost getPostByID(int id) {
        try {
            final String GET_POST_BY_ID = "SELECT * FROM blogposts WHERE id = ?";
            return jdbc.queryForObject(GET_POST_BY_ID, new BlogpostMapper(), id);
        } catch(DataAccessException ex) {
            return null;
        }
    }

    @Override
    @Transactional
    public Blogpost addPost(Blogpost blogpost) {
        final String INSERT_POST = "INSERT INTO blogposts(title, contents, postdate, expirydate, needsreview) " +
                "VALUES(?,?,?,?,?)";
        jdbc.update(INSERT_POST,
                blogpost.getTitle(),
                blogpost.getContents(),
                blogpost.getPostDate(),
                blogpost.getExpiryDate(),
                blogpost.getNeedsReview());

        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        blogpost.setId(newId);
        return blogpost;
    }

    @Override
    public void reviewPostByID(int id) {
        final String REVIEW_POST = "UPDATE blogposts SET needsreview = false WHERE id = ?";
        jdbc.update(REVIEW_POST, id);
    }

    @Override
    @Transactional
    public void deletePostByID(int id) {
        final String DELETE_HASHTAGS = "DELETE FROM hashtags WHERE blogpostid = ?";
        jdbc.update(DELETE_HASHTAGS, id);

        final String DELETE_POST = "DELETE FROM blogposts WHERE id = ?";
        jdbc.update(DELETE_POST, id);
    }

    @Override
    public void deleteExpiredPosts() {
        List<Blogpost> posts = getExpiredPosts();

        for (Blogpost blogpost : posts) {
            deletePostByID(blogpost.getId());
        }
    }

    protected static final class BlogpostMapper implements RowMapper<Blogpost> {

        @Override
        public Blogpost mapRow(ResultSet rs, int rowNum) throws SQLException {
            Blogpost blogpost = new Blogpost(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("contents"),
                    rs.getDate("postdate"),
                    rs.getDate("expirydate"),
                    rs.getBoolean("needsreview")
            );
            return blogpost;
        }
    }
}
