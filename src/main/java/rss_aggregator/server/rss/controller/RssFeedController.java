package rss_aggregator.server.rss.controller;

import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndImage;
import org.json.JSONObject;
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
import java.util.ArrayList;
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

        return new JSONObject().put("status", "ok").toString();
    }

    @RequestMapping(value = "/rmFeed", method = RequestMethod.POST)
    @ResponseBody
    public String removeFeed(final HttpServletRequest request, @RequestParam("feed") final String feed) {

        RssFeed rssFeed = feedRepository.findByFeed(feed);

        if (rssFeed == null) {
            return new JSONObject().put("status", "error").put("errno", "feed not found").toString();
        }

        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        UserFeed userFeed = userFeedRepository.findByUserAndFeed(user.get_id(), rssFeed.getId());

        if (userFeed == null) {
            return new JSONObject().put("status", "error").put("errno", "user not subscribed to feed").toString();
        }

        userFeedRepository.delete(userFeed);

        if (userFeedRepository.findAllByFeed(rssFeed.getId()).size() == 0) {
            feedRepository.delete(rssFeed);
        }

        return new JSONObject().put("status", "ok").toString();
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

        SyndFeed syndFeed = rssGetter.getRssSyndFeed(rssFeed.getFeed());

        List<Item> items = new ArrayList<>();
        for (SyndEntry entry: syndFeed.getEntries()) {
            Item item = new Item();

            item.setTitle(entry.getTitle());
            item.setPubDate(entry.getPublishedDate());
            item.setLink(entry.getLink());
            item.setAuthor(entry.getAuthor());
            items.add(item);
        }

        JSONObject feedJson = new JSONObject();

        SyndImage image = syndFeed.getImage();
        JSONObject imageJson = new JSONObject();
        imageJson.put("description", image.getDescription());
        imageJson.put("link", image.getLink());
        imageJson.put("title", image.getTitle());
        imageJson.put("url", image.getUrl());

        feedJson.put("author", syndFeed.getAuthor());
        feedJson.put("description", syndFeed.getDescription());
        feedJson.put("title", syndFeed.getTitle());
        feedJson.put("image", imageJson);
        feedJson.put("items", items);

        return feedJson.toString();
    }

}
