package com.sg.blogwebsite.dao;

import com.sg.blogwebsite.entities.Blogpost;
import com.sg.blogwebsite.entities.Hashtag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class HashtagDaoDB implements HashtagDao {

    @Autowired
    JdbcTemplate jdbc;

    @Override
    public List<Hashtag> getHashtags(boolean orderByPost) {
        final String GET_HASHTAGS = orderByPost ? "SELECT * FROM hashtags ORDER BY blogpostid"
                : "SELECT * FROM hashtags ORDER BY tagname";
        return jdbc.query(GET_HASHTAGS, new HashtagMapper());
    }

    @Override
    public List<Hashtag> getHashtagsByPostID(int id) {
        final String GET_HASHTAGS_BY_POST_ID = "SELECT * FROM hashtags WHERE blogpostid = ?";
        return jdbc.query(GET_HASHTAGS_BY_POST_ID, new HashtagMapper(), id);
    }

    @Override
    public Hashtag getHashtagByID(int id) {
        try {
            final String GET_HASHTAG_BY_ID = "SELECT * FROM hashtags WHERE id = ?";
            return jdbc.queryForObject(GET_HASHTAG_BY_ID, new HashtagMapper(), id);
        } catch(DataAccessException ex) {
            return null;
        }
    }

    @Override
    public Hashtag addHashtag(Hashtag hashtag) {
        final String INSERT_HASHTAG = "INSERT INTO hashtags(blogpostid, tagname) " +
                "VALUES(?,?)";
        jdbc.update(INSERT_HASHTAG,
                hashtag.getBlogpostID(),
                hashtag.getTagName());

        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        hashtag.setId(newId);
        return hashtag;
    }

    @Override
    public void deleteHashtagByID(int id) {
        final String DELETE_HASHTAGS = "DELETE FROM hashtags WHERE id = ?";
        jdbc.update(DELETE_HASHTAGS, id);
    }

    protected static final class HashtagMapper implements RowMapper<Hashtag> {

        @Override
        public Hashtag mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hashtag hashtag = new Hashtag(
                    rs.getInt("id"),
                    rs.getInt("blogpostid"),
                    rs.getString("tagname")
            );
            return hashtag;
        }
    }
}
