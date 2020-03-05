package rss_aggregator.server.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import rss_aggregator.server.rss.RssFeedRepository;
import rss_aggregator.server.rss.models.RssFeed;
import rss_aggregator.server.users.IUserService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private RssFeedRepository feedRepository;

    @RequestMapping(value = "/addFeed", method = RequestMethod.GET)
    @ResponseBody
    public String addFeed(final HttpServletRequest request, @RequestParam("feed") final String feed) {
        RssFeed rssFeed = new RssFeed();

        rssFeed.setFeed(feed);
        feedRepository.save(rssFeed);
        return "/rss";
    }
}
