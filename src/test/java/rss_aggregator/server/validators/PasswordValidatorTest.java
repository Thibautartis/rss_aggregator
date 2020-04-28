package rss_aggregator.server.validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void isValid() {
        PasswordValidator validator = new PasswordValidator();

        Assertions.assertTrue(validator.isValid("Ng1FCddgV", "Ng1FCddgV"));
        Assertions.assertTrue(validator.isValid("cvV9CkTpQz2", "cvV9CkTpQz2"));
        Assertions.assertFalse(validator.isValid("Ng1FCddgV", "cvV9CkTpQz2"));
        Assertions.assertFalse(validator.isValid("RIT3b", "RIT3b"));
        Assertions.assertFalse(validator.isValid("password", "password"));
        Assertions.assertFalse(validator.isValid("password", "password"));
        Assertions.assertFalse(validator.isValid("password1234", "password1234"));
        Assertions.assertFalse(validator.isValid("password1234", "password1234"));
        Assertions.assertTrue(validator.isValid("Password1234", "Password1234"));
    }
}