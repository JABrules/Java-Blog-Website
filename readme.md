This folder is an IntelliJ project.

The application can be run from Main.
The ERD for the database is included (blog-erd.png)
The database can be created by running blogpost-db.sql
The database can also be populated with a few records by running populate-blog.sql
The web-page can be accessed from http://localhost:8080
Unfortunately I was not able to get the embedded html editor to work so the blog posts are in plaintext instead.
The rest of the functionality of the website has been added.
The homepage has buttons to go to pages to find blogposts. It also contains two links to access the admin and owner sections of the website respectively.
The admin section of the site allows the user to add blogposts, however they will not be displayed on the main page until marked as reviewed by the owner.
The owner section of the site allows the owner to add or delete blogposts, mark blogposts as reviewed, look at unreleased or expired blogposts, and also add or delete tags from blogposts.
Posts added can have a schedule date and expiry date - the post will only be visible on the main page if the current date is between these two.
The user can also search for all posts that are tagged with a specific hashtag.