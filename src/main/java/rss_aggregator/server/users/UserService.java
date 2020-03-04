package rss_aggregator.server.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rss_aggregator.server.exceptions.EmailExistsException;
import rss_aggregator.server.exceptions.UserAlreadyExistsException;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Override
    public User getUser(String verificationToken) {
        return null;
    }

    @Override
    public void saveRegisteredUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void createVerificationTokenForUser(User user, String token) {

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
