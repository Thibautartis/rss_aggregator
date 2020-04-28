package rss_aggregator.server.rss;

import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.json.JSONObject;

import javax.validation.constraints.NotNull;
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

    public List<SyndFeed> getMultipleRssSyndFeed(@NotNull final List<String> src) {
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

    public List<Item> getMultipleRssFeed(@NotNull final List<String> src)
    {
        List<Item> feed = new ArrayList<>();
        for (String str : src) {
            feed.addAll(getRssFeed(str));
        }

        return feed;
    }

    public JSONObject getRssFeedAsJson(final String feed) {
        RssGetter rssGetter = new RssGetter();

        SyndFeed syndFeed = rssGetter.getRssSyndFeed(feed);

        List<Item> items = new ArrayList<>();
        for (SyndEntry entry : syndFeed.getEntries()) {
            Item item = new Item();

            item.setTitle(entry.getTitle());
            item.setPubDate(entry.getPublishedDate());
            item.setLink(entry.getLink());
            item.setAuthor(entry.getAuthor());
            items.add(item);
        }

        JSONObject feedJson = new JSONObject();

        SyndImage image = syndFeed.getImage();
        if (image != null) {
            JSONObject imageJson = new JSONObject();
            imageJson.put("description", image.getDescription());
            imageJson.put("link", image.getLink());
            imageJson.put("title", image.getTitle());
            imageJson.put("url", image.getUrl());
            feedJson.put("image", imageJson);
        }

        feedJson.put("author", syndFeed.getAuthor());
        feedJson.put("description", syndFeed.getDescription());
        feedJson.put("title", syndFeed.getTitle());
        feedJson.put("items", items);

        return feedJson;
    }

    public JSONObject getMultipleRssFeedAsJson(@NotNull final List<String> feeds) {
        JSONObject feedsJson = new JSONObject();

        int i = 0;
        for (String feed : feeds) {
            JSONObject feedAsJson = getRssFeedAsJson(feed);
            feedsJson.put("feed" + i++, feedAsJson);
        }
        return feedsJson;
    }

    public WebFeed getRssFeedAsWebFeed(final String feed) {
        RssGetter rssGetter = new RssGetter();

        SyndFeed syndFeed = rssGetter.getRssSyndFeed(feed);

        List<WebFeed.WebFeedEntry> items = new ArrayList<>();
        for (SyndEntry entry : syndFeed.getEntries()) {
            WebFeed.WebFeedEntry item = new WebFeed.WebFeedEntry();

            item.setTitle(entry.getTitle());
            item.setPubDate(entry.getPublishedDate().toString());
            item.setLink(entry.getLink());
            item.setAuthor(entry.getAuthor());
            items.add(item);
        }

        WebFeed webFeed = new WebFeed();

        webFeed.setAuthor(syndFeed.getAuthor());
        webFeed.setDescription(syndFeed.getDescription());
        webFeed.setTitle(syndFeed.getTitle());
        webFeed.setEntries(items);

        return webFeed;
    }

    public List<WebFeed> getMultipleRssFeedAsWebFeed(@NotNull final List<String> feeds) {
        List<WebFeed> webFeeds = new ArrayList<>();

        for (String feed: feeds) {
            WebFeed webFeed = getRssFeedAsWebFeed(feed);
            webFeeds.add(webFeed);
        }
        return webFeeds;
    }
}
