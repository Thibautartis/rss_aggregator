package rss_aggregator.server.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import rss_aggregator.server.rss.RssFeedRepository;
import rss_aggregator.server.rss.model.RssFeed;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserFeedRepository {

    @Autowired
    UserFeedRepository rssFeedRepository;

    @Test
    public void addFeed() {

        RssFeed feed = new RssFeed();
        feed.setFeed("test");

        rssFeedRepository.save(feed);

        RssFeed found = rssFeedRepository.findByFeed(feed.getFeed());
        assertThat(found).isEqualTo(feed);

    }

    @Test
    public void addAndRmFeed() {

        RssFeed feed = new RssFeed();
        feed.setFeed("test");

        rssFeedRepository.save(feed);

        RssFeed found = rssFeedRepository.findByFeed(feed.getFeed());
        assertThat(found).isNotEqualTo(null);

        rssFeedRepository.delete(feed);

        found = rssFeedRepository.findByFeed(feed.getFeed());
        assertThat(found).isEqualTo(null);

    }

    @Test
    public void addAndUpdateFeed() {

        RssFeed feed = new RssFeed();
        feed.setFeed("test");

        rssFeedRepository.save(feed);

        RssFeed found = rssFeedRepository.findByFeed(feed.getFeed());
        assertThat(found).isEqualTo(feed);

        found.setFeed("tset");

        rssFeedRepository.save(found);
        assertThat(found).isEqualTo(feed);

        found = rssFeedRepository.findByFeed("test");
        assertThat(found).isEqualTo(null);
    }

}
