package rss_aggregator.server.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLValidator {
    private Pattern pattern;
    private Matcher matcher;
    private static final String URL_PATTERN = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)\n";

    private boolean isValid(final String url) {
        pattern = Pattern.compile(URL_PATTERN);
        matcher = pattern.matcher(url);
        return matcher.matches();
    }
}
