package rss_aggregator.server.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import rss_aggregator.server.passwordlosttoken.PasswordLostTokenRepository;
import rss_aggregator.server.passwordlosttoken.model.PasswordLostToken;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PasswordLostTokenRepositoryTest {

    @Autowired
    PasswordLostTokenRepository passwordLostTokenRepository;

    @Test
    public void addToken() {

        PasswordLostToken token = new PasswordLostToken();
        token.setToken("test");

        passwordLostTokenRepository.save(token);

        PasswordLostToken found = passwordLostTokenRepository.findByToken(token.getToken());
        assertThat(found).isEqualTo(token);

    }

    @Test
    public void addAndRmToken() {

        PasswordLostToken token = new PasswordLostToken();
        token.setToken("test");

        passwordLostTokenRepository.save(token);

        PasswordLostToken found = passwordLostTokenRepository.findByToken(token.getToken());
        assertThat(found).isNotEqualTo(null);

        passwordLostTokenRepository.delete(token);

        found = passwordLostTokenRepository.findByToken(token.getToken());
        assertThat(found).isEqualTo(null);

    }

    @Test
    public void addAndUpdateToken() {

        PasswordLostToken token = new PasswordLostToken();
        token.setToken("test");

        passwordLostTokenRepository.save(token);

        PasswordLostToken found = passwordLostTokenRepository.findByToken(token.getToken());
        assertThat(found).isEqualTo(token);

        found.setToken("tset");

        passwordLostTokenRepository.save(found);
        assertThat(found).isEqualTo(token);

        found = passwordLostTokenRepository.findByToken("test");
        assertThat(found).isEqualTo(null);
    }

}
