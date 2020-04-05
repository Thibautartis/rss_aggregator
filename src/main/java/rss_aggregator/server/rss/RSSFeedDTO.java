package rss_aggregator.server.rss;


import com.sun.istack.NotNull;
import rss_aggregator.server.validators.ValidURL;

import javax.validation.constraints.NotEmpty;

public class RSSFeedDTO {
    @NotNull
    @NotEmpty
    @ValidURL
    private String feed;

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }
}
