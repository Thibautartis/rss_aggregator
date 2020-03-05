package rss_aggregator.server.rss.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item;
import rss_aggregator.server.rss.RssGetter;

@Component
public class RssFeedView extends AbstractRssFeedView {

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

        RssGetter getter = new RssGetter();
        List<String> rssSrc = new ArrayList<>();
        rssSrc.add("https://www.reddit.com/r/trees.rss");
        rssSrc.add("https://www.reddit.com/r/gtaonline.rss");

        return getter.getMultipleRssFeed(rssSrc);
    }
}
