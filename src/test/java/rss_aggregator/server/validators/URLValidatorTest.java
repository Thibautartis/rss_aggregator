package rss_aggregator.server.validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class URLValidatorTest {

    @Test
    void isValid() {
        URLValidator validator = new URLValidator();

        Assertions.assertTrue(validator.isValid("https://reddit.com/r/kde.rss"));
        Assertions.assertTrue(validator.isValid("http://reddit.com/r/kde.rss"));
        Assertions.assertFalse(validator.isValid("reddit.com/r/kde.rss"));
        Assertions.assertFalse(validator.isValid("http://reddit/r/kde.rss"));
        Assertions.assertFalse(validator.isValid("httpreddit/r/kde.rss"));
        Assertions.assertFalse(validator.isValid("francis"));
    }
}