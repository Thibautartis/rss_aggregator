package rss_aggregator.server.users;

import rss_aggregator.server.exceptions.UserAlreadyExistsException;

import java.util.Optional;

public interface IUserService {

    User registerNewUserAccount(UserDTO accountDto) throws UserAlreadyExistsException;

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void deleteUser(User user);

    void createVerificationTokenForUser(User user, String token);

    User findUserByEmail(String email);

    Optional<User> getUserByID(long id);

    void changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String password);

    String validateVerificationToken(String token);

}