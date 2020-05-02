package rss_aggregator.server.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import rss_aggregator.server.users.UserRepository;
import rss_aggregator.server.users.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void addUser() throws Exception {

        User user = new User();
        user.setEmail("test");
        user.setPassword("test");

        userRepository.save(user);

        User found = userRepository.findByEmail(user.getEmail());
        assertThat(found).isEqualTo(user);

    }

    @Test
    public void addAndRmUser() throws Exception {

        User user = new User();
        user.setEmail("test");
        user.setPassword("test");

        userRepository.save(user);

        User found = userRepository.findByEmail(user.getEmail());
        assertThat(found).isNotEqualTo(null);

        userRepository.delete(user);

        found = userRepository.findByEmail(user.getEmail());
        assertThat(found).isEqualTo(null);

    }

    @Test
    public void addAndUpdateUser() throws Exception {

        User user = new User();
        user.setEmail("test");
        user.setPassword("test");

        userRepository.save(user);

        User found = userRepository.findByEmail(user.getEmail());
        assertThat(found).isEqualTo(user);

        found.setEmail("tset");
        found.setPassword("tset");

        userRepository.save(found);
        assertThat(found).isEqualTo(user);

        found = userRepository.findByEmail("test");
        assertThat(found).isEqualTo(null);
    }
}
