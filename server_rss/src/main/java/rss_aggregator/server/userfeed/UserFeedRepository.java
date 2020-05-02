package rss_aggregator.server.userfeed;

import org.springframework.data.jpa.repository.JpaRepository;
import rss_aggregator.server.userfeed.model.UserFeed;

import java.util.List;

public interface UserFeedRepository extends JpaRepository<UserFeed, Long> {
    List<UserFeed> findAllByFeed(Long feed);

    List<UserFeed> findAllByUser(Long user);

    UserFeed findByUserAndFeed(Long user, Long feed);

    @Override
    void delete(UserFeed userFeed);
}
