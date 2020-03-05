package rss_aggregator.server.signup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import rss_aggregator.server.SendMail;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.model.User;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

@Component
public class SignupListener implements ApplicationListener<OnSignupCompleteEvent> {

    @Autowired
    private IUserService service;

    @Qualifier("messageSource")
    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnSignupCompleteEvent event) {
        System.out.println("mail envoyé zupeeeer");
        this.confirmSignup(event);
    }

    private void confirmSignup(OnSignupCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);

        String confirmationUrl
                = event.getAppUrl() + "/signupConfirm.html?token=" + token;
        String message = "Registration success, niquel, super";

        new SendMail(mailSender, user.getEmail(), "super", "Signup confirmation",
                message + "\r\n" + "http://localhost:8080" + confirmationUrl);
    }
}
