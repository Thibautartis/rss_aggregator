package rss_aggregator.server.repositories;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import rss_aggregator.server.passwordlosttoken.PasswordLostTokenRepository;
import rss_aggregator.server.rss.RssFeedRepository;
import rss_aggregator.server.userfeed.UserFeedRepository;
import rss_aggregator.server.users.UserRepository;
import rss_aggregator.server.users.model.User;
import rss_aggregator.server.verificationtoken.VerificationTokenRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoriesTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

/*
    @Autowired
    private RssFeedRepository rssFeedRepository;

    @Autowired
    private UserFeedRepository userFeedRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordLostTokenRepository passwordLostTokenRepository;
*/

    @Test
    public void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(userRepository).isNotNull();
/*
        assertThat(rssFeedRepository).isNotNull();
        assertThat(userFeedRepository).isNotNull();
        assertThat(verificationTokenRepository).isNotNull();
        assertThat(passwordLostTokenRepository).isNotNull();
*/
    }

    @Test
    public void testAddUser() {
        User user = new User();
        user.setEmail("jak@jak.jak");
        user.setPassword("##Password1234");

        userRepository.save(user);

        User getUser = userRepository.findByEmail(user.getEmail());

        assertThat(user).isEqualTo(getUser);
    }
}
