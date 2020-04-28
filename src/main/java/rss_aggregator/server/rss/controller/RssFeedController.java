package rss_aggregator.server.rss.controller;

import com.rometools.rome.feed.synd.SyndFeed;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rss_aggregator.server.rss.RssFeedRepository;
import rss_aggregator.server.rss.RssGetter;
import rss_aggregator.server.rss.WebFeed;
import rss_aggregator.server.rss.model.RssFeed;
import rss_aggregator.server.userfeed.UserFeedRepository;
import rss_aggregator.server.userfeed.model.UserFeed;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.model.User;
import rss_aggregator.server.validators.URLValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class RssFeedController {

    @Autowired
    RssFeedRepository feedRepository;

    @Autowired
    UserFeedRepository userFeedRepository;

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/rss", method = RequestMethod.GET)
    public String rssFeedsGet(final HttpServletRequest request, Model model) {
        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        List<UserFeed> userFeeds = userFeedRepository.findAllByUser(user.get_id());
        ArrayList<String> feeds = new ArrayList<>();
        for (UserFeed userFeed : userFeeds) {
            Optional<RssFeed> rssFeed = feedRepository.findById(userFeed.getFeed());
            feeds.add(rssFeed.get().getFeed());
        }

        RssGetter rssGetter = new RssGetter();
        List<WebFeed> webFeeds = rssGetter.getMultipleRssFeedAsWebFeed(feeds);

        model.addAttribute("feeds", webFeeds);

        return "rss";
    }

    @RequestMapping(value = "/rss", method = RequestMethod.POST)
    @ResponseBody
    public String rssFeeds(final HttpServletRequest request) {
        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        List<UserFeed> userFeeds = userFeedRepository.findAllByUser(user.get_id());
        ArrayList<String> feeds = new ArrayList<>();
        for (UserFeed userFeed : userFeeds) {
            Optional<RssFeed> rssFeed = feedRepository.findById(userFeed.getFeed());
            feeds.add(rssFeed.get().getFeed());
        }

        RssGetter rssGetter = new RssGetter();

        JSONObject jsonFeeds = rssGetter.getMultipleRssFeedAsJson(feeds);

        JSONObject response = new JSONObject()
                .put("status", "ok");

        response.put("feeds", jsonFeeds);

        return response.toString();
    }

    @RequestMapping(value = "/addFeed", method = RequestMethod.POST)
    @ResponseBody
    public String addFeed(final HttpServletRequest request, HttpServletResponse response) {

        String feed = request.getParameter("feed");

        URLValidator validator = new URLValidator();
        if (!validator.isValid(feed)) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("error", "invalid url").toString();
        }

        RssFeed rssFeed = feedRepository.findByFeed(feed);

        if (rssFeed == null) {
            rssFeed = new RssFeed();
            rssFeed.setFeed(feed);
            feedRepository.save(rssFeed);
        }

        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        UserFeed userFeed = userFeedRepository.findByUserAndFeed(user.get_id(), rssFeed.getId());

        if (userFeed != null) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("errno", "feed already bound to user").toString();
        }

        userFeed = new UserFeed();
        userFeed.setFeed(rssFeed.getId());
        userFeed.setUser(user.get_id());

        userFeedRepository.save(userFeed);

        return new JSONObject().put("status", "ok").toString();
    }

    @RequestMapping(value = "/rmFeed", method = RequestMethod.POST)
    @ResponseBody
    public String removeFeed(final HttpServletRequest request, HttpServletResponse response) {

        String feed = request.getParameter("feed");

        RssFeed rssFeed = feedRepository.findByFeed(feed);

        if (rssFeed == null) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("errno", "feed not found").toString();
        }

        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        UserFeed userFeed = userFeedRepository.findByUserAndFeed(user.get_id(), rssFeed.getId());

        if (userFeed == null) {
            response.setStatus(400);
            return new JSONObject()
                    .put("status", "error")
                    .put("errno", "user not subscribed to feed")
                    .toString();
        }

        userFeedRepository.delete(userFeed);

        if (userFeedRepository.findAllByFeed(rssFeed.getId()).size() == 0) {
            feedRepository.delete(rssFeed);
        }

        return new JSONObject().put("status", "ok").toString();
    }

    @RequestMapping(value = "/getFeed", method = RequestMethod.POST)
    @ResponseBody
    public String getFeed(final HttpServletRequest request, HttpServletResponse response) {

        String feed = request.getParameter("feed");

        RssFeed rssFeed = feedRepository.findByFeed(feed);

        System.out.println(rssFeed);
        if (rssFeed == null) {
            response.setStatus(400);
            return new JSONObject()
                    .put("status", "error")
                    .put("errno", "could not find feed")
                    .toString();
        }

        RssGetter rssGetter = new RssGetter();

        JSONObject feedJson = rssGetter.getRssFeedAsJson(rssFeed.getFeed());

        JSONObject responseJson = new JSONObject()
                .put("status", "ok")
                .put("feed", feedJson);

        return responseJson
                .toString();
    }

}
