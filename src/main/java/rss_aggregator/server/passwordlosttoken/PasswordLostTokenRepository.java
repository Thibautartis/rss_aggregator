package rss_aggregator.server.passwordlosttoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import rss_aggregator.server.passwordlosttoken.model.PasswordLostToken;

import java.util.Date;

public interface PasswordLostTokenRepository extends JpaRepository<PasswordLostToken, Long> {

    PasswordLostToken findByToken(String token);

    @Modifying
    @Query("delete from PasswordLostToken t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);
}
