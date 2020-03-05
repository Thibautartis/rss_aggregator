package rss_aggregator.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import rss_aggregator.server.users.models.User;
import rss_aggregator.server.users.UserRepository;

import java.util.ArrayList;

public class RssUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with email : " + email);
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                true, true, true, true, new ArrayList<>());
    }
}
