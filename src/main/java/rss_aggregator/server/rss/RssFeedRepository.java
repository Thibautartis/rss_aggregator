package rss_aggregator.server.rss;

import org.springframework.data.jpa.repository.JpaRepository;
import rss_aggregator.server.rss.models.RssFeed;

public interface RssFeedRepository extends JpaRepository<RssFeed, Long> {
    RssFeed findByFeed(String feed);

    @Override
    void delete(RssFeed rssFeed);
}
