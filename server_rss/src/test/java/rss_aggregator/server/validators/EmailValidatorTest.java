package rss_aggregator.server.validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    @Test
    void isValid() {
        EmailValidator validator = new EmailValidator();

        Assertions.assertTrue(validator.isValid("jak@jak.jak"));
        Assertions.assertTrue(validator.isValid("test@gmail.com"));
        Assertions.assertFalse(validator.isValid("bonjour"));
        Assertions.assertFalse(validator.isValid("test[ at ]gmail.com"));
        Assertions.assertFalse(validator.isValid("test@gmail.c"));
        Assertions.assertFalse(validator.isValid("t@t.t"));
    }
}