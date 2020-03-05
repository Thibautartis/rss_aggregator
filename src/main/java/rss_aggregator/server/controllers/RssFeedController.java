package rss_aggregator.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;
import rss_aggregator.server.rss.views.RssFeedView;

@RestController
public class RssFeedController {

    @Autowired
    private RssFeedView view;

    @GetMapping("/rss")
    public View GetFeed() {
        return view;
    }
}
