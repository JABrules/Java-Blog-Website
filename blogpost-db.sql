drop database if exists blog;
create database blog;
use blog;

create table blogposts (
	id int not null auto_increment,
    title varchar(100) not null,
    contents mediumtext not null,
    postdate date not null,
    expirydate date not null,
    needsreview boolean not null,
    constraint PK_id primary key (id)
);

create table hashtags (
	id int not null auto_increment,
    blogpostid int not null,
    tagname varchar(30) not null,
    constraint PK_tagid primary key (id),
    constraint FK_blogpost_id foreign key (blogpostid)
		references blogposts (id)
);