package rss_aggregator.server.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rss_aggregator.server.exceptions.EmailExistsException;
import rss_aggregator.server.users.model.User;
import rss_aggregator.server.verificationtoken.model.VerificationToken;
import rss_aggregator.server.verificationtoken.VerificationTokenRepository;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User registerNewUserAccount(String username, String password) throws EmailExistsException {
        if (emailExists(username)) {
            throw new EmailExistsException("There is an account with that email address: " + username);
        }
        final User user = new User();

        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(username);
        return userRepository.save(user);
    }

    public User getUserByVerificationToken(final String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);

        return token != null ? token.getUser() : null;
    }

    @Override
    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
        System.out.println("token created : " + token);
    }

    @Override
    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID()
                .toString());
        vToken = tokenRepository.save(vToken);
        System.out.println("token updated : " + vToken);
        return vToken;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByID(long id) {
        return userRepository.findById(id);
    }

    @Override
    public void changeUserPassword(User user, String password) {
        user.setPassword(password);
        saveUser(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String password) {
        return false;
    }

    @Override
    public String validateVerificationToken(String token) {

        VerificationToken verificationToken = getVerificationToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return TOKEN_EXPIRED;
        }
        user.setActivated(true);
        saveUser(user);
        return TOKEN_VALID;
    }
}
