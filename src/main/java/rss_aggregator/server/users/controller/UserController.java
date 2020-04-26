package rss_aggregator.server.users.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rss_aggregator.server.rss.RssFeedRepository;
import rss_aggregator.server.rss.model.RssFeed;
import rss_aggregator.server.userfeed.UserFeedRepository;
import rss_aggregator.server.userfeed.model.UserFeed;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.model.User;
import rss_aggregator.server.validators.PasswordValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private UserFeedRepository userFeedRepository;

    @Autowired
    private RssFeedRepository rssFeedRepository;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String showUser(final HttpServletRequest request, final Model model) {
        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        List<UserFeed> userFeeds = userFeedRepository.findAllByUser(user.get_id());
        ArrayList<String> feeds = new ArrayList<>();

        for (UserFeed userFeed: userFeeds) {
            Optional<RssFeed> feed = rssFeedRepository.findById(userFeed.getFeed());
            feeds.add(feed.get().getFeed());
        }

        model.addAttribute("feeds", feeds);

        JSONObject userJson = new JSONObject();

        userJson.put("email", user.getEmail());
        userJson.put("feeds", feeds);

        return userJson.toString();
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(final HttpServletRequest request, HttpServletResponse response) {
        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");
        PasswordValidator validator = new PasswordValidator();
        if (password == null || !validator.isValid(password, confirm)) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("error", "invalid password").toString();
        }

        user.setPassword(password);
        userService.saveUser(user);

        return new JSONObject().put("status", "ok").toString();
    }
}
