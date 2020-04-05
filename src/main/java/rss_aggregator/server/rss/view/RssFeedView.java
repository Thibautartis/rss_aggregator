package rss_aggregator.server.rss.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item;
import rss_aggregator.server.rss.RssFeedRepository;
import rss_aggregator.server.rss.RssGetter;
import rss_aggregator.server.rss.model.RssFeed;
import rss_aggregator.server.userfeed.UserFeedRepository;
import rss_aggregator.server.userfeed.model.UserFeed;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.model.User;

@Component
public class RssFeedView extends AbstractRssFeedView {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserFeedRepository userFeedRepository;

    @Autowired
    private RssFeedRepository rssFeedRepository;

    @Override
    protected Channel newFeed() {
        Channel channel = new Channel("rss_2.0");
        channel.setLink("la.battoire.feed");
        channel.setTitle("La battoire");
        channel.setDescription("FRANCIS");
        return channel;
    }

    @Override
    protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {

        User user = userService.findUserByEmail(request.getUserPrincipal().getName());

        List<UserFeed> userFeeds = userFeedRepository.findAllByUser(user.get_id());
        ArrayList<String> feeds = new ArrayList<>();

        for (UserFeed userFeed: userFeeds) {
            Optional<RssFeed> feed = rssFeedRepository.findById(userFeed.getFeed());
            feeds.add(feed.get().getFeed());
        }

        RssGetter getter = new RssGetter();

        return getter.getMultipleRssFeed(feeds);
    }
}
