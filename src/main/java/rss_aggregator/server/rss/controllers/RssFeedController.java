package rss_aggregator.server.rss.controllers;

import jdk.nashorn.internal.ir.RuntimeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import rss_aggregator.server.rss.RssFeedRepository;
import rss_aggregator.server.rss.models.RssFeed;
import rss_aggregator.server.rss.views.RssFeedView;
import rss_aggregator.server.userfeed.model.UserFeed;
import rss_aggregator.server.userfeed.UserFeedRepository;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.UserRepository;
import rss_aggregator.server.users.models.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

@RestController
public class RssFeedController {

    @Autowired
    RssFeedRepository feedRepository;

    @Autowired
    UserFeedRepository userFeedRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private RssFeedView view;

    @GetMapping("/rss")
    public View GetFeed() {
        return view;
    }

    @RequestMapping(value = "/addFeed", method = RequestMethod.GET)
    @ResponseBody
    public String addFeed(final HttpServletRequest request, @RequestParam("feed") final String feed) {

        RssFeed rssFeed = feedRepository.findByFeed(feed);

        if (rssFeed == null) {
            rssFeed = new RssFeed();
            rssFeed.setFeed(feed);
            feedRepository.save(rssFeed);
        }

        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        UserFeed userFeed = new UserFeed();
        userFeed.setFeed(rssFeed.getId());
        userFeed.setUser(user.get_id());

        userFeedRepository.save(userFeed);

        return "/rss";
    }

    @RequestMapping(value = "/rmFeed", method = RequestMethod.GET)
    @ResponseBody
    public String removeFeed(final HttpServletRequest request, @RequestParam("feed") final String feed) {

        RssFeed rssFeed = feedRepository.findByFeed(feed);

        if (rssFeed == null) {
            return "/error";
        }

        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        UserFeed userFeed = userFeedRepository.findByUserAndFeed(user.get_id(), rssFeed.getId());

        if (userFeed == null) {
            return "/error";
        }

        userFeedRepository.delete(userFeed);

        return "/rss";
    }

}
