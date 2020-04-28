package rss_aggregator.server.rss;

import java.util.List;

public class WebFeed {
    public static class WebFeedEntry {
        String title;
        String pubDate;
        String link;
        String author;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPubDate() {
            return pubDate;
        }

        public void setPubDate(String pubDate) {
            this.pubDate = pubDate;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

    }

    List<WebFeedEntry> entries;

    String author;
    String description;
    String title;

    public List<WebFeedEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<WebFeedEntry> entries) {
        this.entries = entries;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
