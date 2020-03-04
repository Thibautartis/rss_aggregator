package rss_aggregator.server.signup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.User;
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
        String message = messages.getMessage("message.regSucc", null, event.getLocale());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setFrom("super");
        email.setSubject("Signup Confirmation");
        email.setText(message + "\r\n" + "http://localhost:8080" + confirmationUrl);
        mailSender.send(email);
    }
}
