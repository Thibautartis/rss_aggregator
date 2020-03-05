package rss_aggregator.server.rss.models;

import rss_aggregator.server.users.models.User;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "feeds")
public class RssFeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String feed;

    @ManyToMany(mappedBy = "rssFeeds")
    private Collection<User> users;

    public RssFeed() {
        super();
    }

    public RssFeed(final String feed) {
        this.feed = feed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RssFeed rssFeed = (RssFeed) o;
        return id.equals(rssFeed.id) &&
                feed.equals(rssFeed.feed) &&
                Objects.equals(users, rssFeed.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, feed, users);
    }

    @Override
    public String toString() {
        return "RssFeed{" +
                "id=" + id +
                ", feed='" + feed + '\'' +
                ", users=" + users +
                '}';
    }
}
