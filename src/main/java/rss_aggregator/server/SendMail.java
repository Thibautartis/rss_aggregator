package rss_aggregator.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendMail {
    public SendMail(JavaMailSender mailSender, String to, String from, String subject, String text) {

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setFrom(from);
        email.setSubject(subject);
        email.setText(text);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            mailSender.send(email);
        });
    }
}
