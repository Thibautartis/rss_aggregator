package rss_aggregator.server.users;

import rss_aggregator.server.exceptions.UserAlreadyExistsException;
import rss_aggregator.server.passwordlosttoken.model.PasswordLostToken;
import rss_aggregator.server.users.model.User;
import rss_aggregator.server.verificationtoken.model.VerificationToken;

import java.util.Optional;

public interface IUserService {

    User registerNewUserAccount(String username, String password) throws UserAlreadyExistsException;

    User getUserByVerificationToken(String verificationToken);

    VerificationToken getVerificationToken(String VerificationToken);

    void saveUser(User user);

    void deleteUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken generateNewVerificationToken(String existingVerificationToken);

    User getUserByPasswordLostToken(String passwordLostToken);

    PasswordLostToken getPasswordLostToken(String passwordLostToken);

    void createPasswordLostToken(User user, String passwordLostToken);

    User findUserByEmail(String email);

    Optional<User> getUserByID(long id);

    void changeUserPassword(User user, String password);

    String validateVerificationToken(String token);

    String validatePasswordLostToken(String token);

}