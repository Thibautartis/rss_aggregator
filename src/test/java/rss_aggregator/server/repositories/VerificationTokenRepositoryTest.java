package rss_aggregator.server.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import rss_aggregator.server.verificationtoken.VerificationTokenRepository;
import rss_aggregator.server.verificationtoken.model.VerificationToken;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class VerificationTokenRepositoryTest {

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Test
    public void addToken() {

        VerificationToken token = new VerificationToken();
        token.setToken("test");

        verificationTokenRepository.save(token);

        VerificationToken found = verificationTokenRepository.findByToken(token.getToken());
        assertThat(found).isEqualTo(token);

    }

    @Test
    public void addAndRmToken() {

        VerificationToken token = new VerificationToken();
        token.setToken("test");

        verificationTokenRepository.save(token);

        VerificationToken found = verificationTokenRepository.findByToken(token.getToken());
        assertThat(found).isNotEqualTo(null);

        verificationTokenRepository.delete(token);

        found = verificationTokenRepository.findByToken(token.getToken());
        assertThat(found).isEqualTo(null);

    }

    @Test
    public void addAndUpdateToken() {

        VerificationToken token = new VerificationToken();
        token.setToken("test");

        verificationTokenRepository.save(token);

        VerificationToken found = verificationTokenRepository.findByToken(token.getToken());
        assertThat(found).isEqualTo(token);

        found.setToken("tset");

        verificationTokenRepository.save(found);
        assertThat(found).isEqualTo(token);

        found = verificationTokenRepository.findByToken("test");
        assertThat(found).isEqualTo(null);
    }
    
}
