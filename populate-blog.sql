use blog;

delete from hashtags where id > 0;
delete from blogposts where id > 0;

insert into blogposts (id, title, contents, postdate, expirydate, needsreview) values 
	(1, 'cool post', 'yeah this is cool', '2023-01-03', '2023-02-03', false),
    (2, 'cooler post', 'yeah this is cooler', '2023-01-03', '2023-02-02', false),
    (3, 'coolerer post', 'yeah this is coolerer', '2023-01-03', '2023-02-02', false),
    (4, 'unreviewed post', 'secrets', '2023-01-03', '2023-02-03', true),
    (5, 'unreviewed post... TWO!', 'more secrets', '2023-01-03', '2023-02-02', true),
    (6, 'scheduled post', 'the future is now!', '2023-01-06', '2023-02-06', false),
    (7, 'expired post', 'rip', '2023-01-01', '2023-01-02', false);

insert into hashtags (id, blogpostid, tagname) values
	(1, 1, 'cool'),
    (2, 1, 'epic'),
    (3, 1, 'awesome'),
    (4, 2, 'cooler'),
    (5, 3, 'cool'),
    (6, 3, 'cooler'),
    (7, 3, 'coolerer'),
    (8, 4, 'unreviewed'),
    (9, 5, 'unreviewed'),
    (10, 5, 'two'),
    (11, 6, 'new'),
    (12, 7, 'old');

select blogposts.* from blogposts 
left join hashtags
on blogposts.id = hashtags.blogpostid
where blogposts.needsreview = false;

select * from blogposts;

select * from hashtags
order by tagname;