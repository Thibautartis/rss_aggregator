package rss_aggregator.server.rss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import rss_aggregator.server.rss.RssFeedRepository;
import rss_aggregator.server.rss.RssGetter;
import rss_aggregator.server.rss.model.RssFeed;
import rss_aggregator.server.rss.view.RssFeedView;
import rss_aggregator.server.userfeed.model.UserFeed;
import rss_aggregator.server.userfeed.UserFeedRepository;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.model.User;

import javax.servlet.http.HttpServletRequest;

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

    @RequestMapping(value = "/addFeed", method = RequestMethod.POST)
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

    @RequestMapping(value = "/rmFeed", method = RequestMethod.POST)
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

        if (userFeedRepository.findAllByFeed(rssFeed.getId()).size() == 0) {
            feedRepository.delete(rssFeed);
        }

        return "/rss";
    }

    @RequestMapping(value = "/getFeed", method = RequestMethod.GET)
    @ResponseBody
    public String getFeed(final HttpServletRequest request, @RequestParam("feed") final String feed) {

        System.out.println(feed);
        RssFeed rssFeed = feedRepository.findByFeed(feed);

        System.out.println(rssFeed);
        if (rssFeed == null) {
            return "/error";
        }

        RssGetter rssGetter = new RssGetter();

        return rssGetter.getRssFeed(rssFeed.getFeed()).toString();
    }

}
