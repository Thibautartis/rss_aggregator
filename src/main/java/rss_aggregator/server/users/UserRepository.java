package rss_aggregator.server.users;

import org.springframework.data.jpa.repository.JpaRepository;
import rss_aggregator.server.users.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Override
    void delete(User user);
}
