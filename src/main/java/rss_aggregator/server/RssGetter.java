package rss_aggregator.server;

import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RssGetter {
    public SyndFeed getRssSyndFeed(final String src) {
        SyndFeed feed = null;

        try {
            URL feedUrl = new URL(src);

            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(feedUrl));

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage());
        }

        return feed;
    }

    public List<SyndFeed> getMultipleRssSyndFeed(final List<String> src) {
        List<SyndFeed> feeds = new ArrayList<>();
        for (String str : src) {
            feeds.add(getRssSyndFeed(str));
        }
        return feeds;
    }

    public List<Item> getRssFeed(String src) {
        SyndFeed syndFeed = getRssSyndFeed(src);
        List<Item> feed = new ArrayList<>();

        for (SyndEntry entry: syndFeed.getEntries()) {
            Item item = new Item();

            item.setTitle(entry.getTitle());
            item.setPubDate(entry.getPublishedDate());
            item.setLink(entry.getLink());
            item.setAuthor(entry.getAuthor());
            feed.add(item);
        }
        return feed;
    }

    public List<Item> getMultipleRssFeed(List<String> src)
    {
        List<Item> feed = new ArrayList<>();
        for (String str : src) {
            feed.addAll(getRssFeed(str));
        }

        return feed;
    }
}
