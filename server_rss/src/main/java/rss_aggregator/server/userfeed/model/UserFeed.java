package rss_aggregator.server.userfeed.model;

import javax.persistence.*;

@Entity
@Table(name = "user_feeds")
public class UserFeed {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user")
    private Long user;

    @Column(name = "feed")
    private Long feed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user_id) {
        this.user = user_id;
    }

    public Long getFeed() {
        return feed;
    }

    public void setFeed(Long feed_id) {
        this.feed = feed_id;
    }
}