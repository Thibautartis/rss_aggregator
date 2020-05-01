package rss_aggregator.server.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import rss_aggregator.server.userfeed.UserFeedRepository;
import rss_aggregator.server.userfeed.model.UserFeed;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserFeedRepositoryTest {

    @Autowired
    UserFeedRepository userFeedRepository;

    @Test
    public void addUserFeed() {

        UserFeed userFeed = new UserFeed();
        userFeed.setFeed(42L);
        userFeed.setUser(24L);

        userFeedRepository.save(userFeed);

        List<UserFeed> found = userFeedRepository.findAllByFeed(userFeed.getFeed());
        assertThat(found.get(0)).isEqualTo(userFeed);

    }

    @Test
    public void addAndRmUserFeed() {

        UserFeed userFeed = new UserFeed();
        userFeed.setFeed(42L);
        userFeed.setUser(24L);

        userFeedRepository.save(userFeed);

        List<UserFeed> found = userFeedRepository.findAllByFeed(userFeed.getFeed());
        assertThat(found.isEmpty()).isFalse();

        userFeedRepository.delete(userFeed);

        found = userFeedRepository.findAllByFeed(userFeed.getFeed());
        assertThat(found.isEmpty()).isTrue();

    }

    @Test
    public void addAndUpdateUserFeed() {

        UserFeed userFeed = new UserFeed();
        userFeed.setFeed(42L);
        userFeed.setUser(24L);

        userFeedRepository.save(userFeed);

        List<UserFeed> foundList = userFeedRepository.findAllByFeed(userFeed.getFeed());
        assertThat(foundList.isEmpty()).isFalse();

        UserFeed found = foundList.get(0);
        found.setFeed(84L);

        userFeedRepository.save(found);
        assertThat(found).isEqualTo(userFeed);

        foundList = userFeedRepository.findAllByFeed(42L);
        assertThat(foundList.isEmpty()).isTrue();
    }

}
