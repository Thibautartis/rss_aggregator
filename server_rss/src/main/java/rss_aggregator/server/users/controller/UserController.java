package rss_aggregator.server.users.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import rss_aggregator.server.SendMail;
import rss_aggregator.server.passwordlosttoken.model.PasswordLostToken;
import rss_aggregator.server.rss.RssFeedRepository;
import rss_aggregator.server.rss.model.RssFeed;
import rss_aggregator.server.userfeed.UserFeedRepository;
import rss_aggregator.server.userfeed.model.UserFeed;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.model.User;
import rss_aggregator.server.validators.PasswordValidator;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private UserFeedRepository userFeedRepository;

    @Autowired
    private RssFeedRepository rssFeedRepository;

    @Autowired
    private JavaMailSender mailSender;

    static final String INVALID_PASSWORD = "Password should have a length of 6 characters and have 1 number and 1 capital letter";

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String showUser(final HttpServletRequest request, final Model model) {

        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        List<String> feeds = getFeeds(user);

        model.addAttribute("username", user.getEmail());
        model.addAttribute("feeds", feeds);

        return "user";
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    public String sendUser(final HttpServletRequest request) {
        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        List<String> feeds = getFeeds(user);

        JSONObject userJson = new JSONObject();

        userJson.put("email", user.getEmail());
        userJson.put("feeds", feeds);

        return new JSONObject().put("user", userJson).put("status", "ok").toString();
    }

    private List<String> getFeeds(final User user) {
        List<UserFeed> userFeeds = userFeedRepository.findAllByUser(user.get_id());
        ArrayList<String> feeds = new ArrayList<>();

        for (UserFeed userFeed: userFeeds) {
            Optional<RssFeed> feed = rssFeedRepository.findById(userFeed.getFeed());
            feeds.add(feed.get().getFeed());
        }

        return feeds;
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseBody
    public String changePassword(final HttpServletRequest request, HttpServletResponse response) {
        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        if (password == null || confirm == null) {
            response.setStatus(400);
            String missingParam = password == null ? "password" : "confirm";
            return new JSONObject().put("status", "error")
                    .put("error", "parameter \"" + missingParam + "\" is missing").toString();
        }

        PasswordValidator validator = new PasswordValidator();
        if (password == null || !validator.isValid(password, confirm)) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("error", INVALID_PASSWORD).toString();
        }

        userService.changeUserPassword(user, password);

        return new JSONObject().put("status", "ok").toString();
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePasswordWeb(final HttpServletRequest request, HttpServletResponse response) {

        return "changePassword";
    }


    @RequestMapping(value = "/passwordLost", method = RequestMethod.POST)
    @ResponseBody
    public String passwordLost(final HttpServletRequest request) {

        String email = request.getParameter("username");

        if (email == null) {
            return new JSONObject().put("status", "error").put("error", "parameter \"username\" is missing").toString();
        }

        User user = userService.findUserByEmail(email);

        if (user == null) {
            return new JSONObject().put("status", "ok").toString();
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
           String token = UUID.randomUUID().toString();
           userService.createPasswordLostToken(user, token);

           String msg = "Here is your password reset token " + token;

           new SendMail(mailSender, email, "super", "Password reset token", msg);
        });

        return new JSONObject().put("status", "ok").toString();
    }

    @RequestMapping(value = "/changePasswordLost", method = RequestMethod.POST)
    @ResponseBody
    public String changePasswordList(final HttpServletRequest request, HttpServletResponse response) {

        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        String result = checkTokenPasswordConfirm(token, password, confirm);
        if (!result.equals("ok")) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("error", result).toString();
        }

        PasswordLostToken lostToken = userService.getPasswordLostToken(token);
        if (lostToken == null) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("error", "could not find token").toString();
        }

        User user = userService.getUserByPasswordLostToken(token);

        if (user == null) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("error", "no user associated to this token").toString();
        }

        PasswordValidator validator = new PasswordValidator();
        if (password == null || !validator.isValid(password, confirm)) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("error", INVALID_PASSWORD).toString();
        }

        userService.changeUserPassword(user, password);

        return new JSONObject().put("status", "ok").toString();
    }

    private String checkTokenPasswordConfirm(String token, String password, String confirm) {

        String prefix = "parameter \"";
        String suffix = "\" is missing";
        if (token == null) {
            return prefix + "token" + suffix;
        }
        if (password == null) {
            return prefix + "password" + suffix;
        }
        if (confirm == null) {
            return prefix + "confirm" + suffix;
        }
        return "ok";
    }

    @RequestMapping(value = "/rmUser", method = RequestMethod.POST)
    @ResponseBody
    public String rmUser(final HttpServletRequest request) {
        User user = userService.findUserByEmail(request.getUserPrincipal().getName());
        userService.deleteUser(user);

        return new JSONObject().put("status", "ok").toString();
    }
}
