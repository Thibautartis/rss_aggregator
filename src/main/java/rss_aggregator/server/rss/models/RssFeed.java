package rss_aggregator.server.rss.models;

import rss_aggregator.server.users.models.User;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "feeds")
public class RssFeed {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String feed;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RssFeed rssFeed = (RssFeed) o;
        return id.equals(rssFeed.id) &&
                feed.equals(rssFeed.feed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, feed);
    }

    @Override
    public String toString() {
        return "RssFeed{" +
                "id=" + id +
                ", feed='" + feed +
                '}';
    }
}
