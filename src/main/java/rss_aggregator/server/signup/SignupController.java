package rss_aggregator.server.signup;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import rss_aggregator.server.SendMail;
import rss_aggregator.server.exceptions.EmailExistsException;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.UserService;
import rss_aggregator.server.users.model.User;
import rss_aggregator.server.validators.EmailValidator;
import rss_aggregator.server.validators.PasswordValidator;
import rss_aggregator.server.verificationtoken.model.VerificationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Controller
public class SignupController {

    @Autowired
    private IUserService userService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Qualifier("messageSource")
    @Autowired
    MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    @ResponseBody
    public String registerUser(final HttpServletRequest request, HttpServletResponse response) {
        String user = request.getParameter("username");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        EmailValidator emailValidator = new EmailValidator();
        if (!emailValidator.isValid(user)) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("error", "invalid email").toString();
        }

        PasswordValidator passwordValidator = new PasswordValidator();
        if (password == null || !passwordValidator.isValid(password, confirm)) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("error", "invalid password").toString();
        }

        User registered = createUserAccount(user, password);
        if (registered == null) {
            response.setStatus(400);
            return new JSONObject()
                    .put("status", "error")
                    .put("errno", "user already exists")
                    .toString();
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String token = UUID.randomUUID().toString();
            userService.createVerificationToken(registered, token);

            String confirmationUrl
                    = request.getContextPath() + "/signupConfirm.html?token=" + token;
            String message = "Registration success, niquel, super";

            new SendMail(mailSender, user, "super", "Signup confirmation",
                    message + "\r\n" + "http://localhost:8080" + confirmationUrl);
        });

        return new JSONObject()
                .put("status", "ok")
                .toString();
    }

    private User createUserAccount(String username, String password) {
        User registered = null;
        try {
            registered = userService.registerNewUserAccount(username, password);
        } catch (EmailExistsException e) {
            System.out.println(e);
        }
        return registered;
    }

    @RequestMapping(value = "/signupConfirm", method=RequestMethod.GET)
    @ResponseBody
    public String confirmSignup(HttpServletResponse response, @RequestParam("token") String token) {

        String result = userService.validateVerificationToken(token);
        JSONObject responseJson = new JSONObject();
        switch (result) {
            case UserService.TOKEN_VALID:
                responseJson.put("status", "ok");
                break;
            case UserService.TOKEN_EXPIRED:
                response.setStatus(400);
                responseJson.put("status", "error").put("errno", "token expired");
                break;
            case UserService.TOKEN_INVALID:
                response.setStatus(400);
                responseJson.put("status", "error").put("errno", "token invalid");
                break;
        }

        return responseJson.toString();
    }

    @RequestMapping(value = "/resendSignupToken", method = RequestMethod.GET)
    @ResponseBody
    public String resendRegistrationToken(final HttpServletRequest request, HttpServletResponse response, @RequestParam("token") final String existingToken) {

        if (userService.getVerificationToken(existingToken) == null) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("error", "token does not exist").toString();
        }

        // newToken is the old token after update
        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUserByVerificationToken(newToken.getToken());

        String confirmationUrl = "/signupConfirm.html?token=" + newToken.getToken();
        String message = "Registration success, niquel, super";

        new SendMail(mailSender, user.getEmail(), "super", "resend Signup confirmation",
                message + "\r\n" + "http://localhost:8080" + confirmationUrl);
        return new JSONObject().put("status", "ok").toString();
    }

}
