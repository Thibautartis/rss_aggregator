package rss_aggregator.server.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rss_aggregator.server.exceptions.EmailExistsException;
import rss_aggregator.server.exceptions.UserAlreadyExistsException;
import rss_aggregator.server.security.VerificationToken;
import rss_aggregator.server.security.VerificationTokenRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User registerNewUserAccount(UserDTO accountDto) throws EmailExistsException {
        if (emailExists(accountDto.getEmail())) {
            throw new EmailExistsException("There is an account with that email adress: " + accountDto.getEmail());
        }
        final User user = new User();

        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        return userRepository.save(user);
    }

    public User getUser(final String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    @Override
    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
        System.out.println("token created : " + token);
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }

    @Override
    public Optional<User> getUserByID(long id) {
        return Optional.empty();
    }

    @Override
    public void changeUserPassword(User user, String password) {

    }

    @Override
    public boolean checkIfValidOldPassword(User user, String password) {
        return false;
    }

    @Override
    public String validateVerificationToken(String token) {
        return null;
    }
}
