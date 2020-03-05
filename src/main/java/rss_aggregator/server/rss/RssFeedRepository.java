package rss_aggregator.server.rss;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rss_aggregator.server.rss.models.RssFeed;

import java.util.List;

public interface RssFeedRepository extends JpaRepository<RssFeed, Long> {
    RssFeed findByFeed(String feed);

    @Query(value = "select feeds.id, feeds.feed from feeds left join user_feeds on user_feeds.feed = feeds.id where user_feeds.user = :user_id", nativeQuery = true)
    List<RssFeed> findByUser(@Param("user_id") Long id);

    @Override
    void delete(RssFeed rssFeed);
}
