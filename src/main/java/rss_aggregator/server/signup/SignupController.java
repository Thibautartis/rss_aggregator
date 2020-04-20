package rss_aggregator.server.signup;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import rss_aggregator.server.SendMail;
import rss_aggregator.server.exceptions.EmailExistsException;
import rss_aggregator.server.verificationtoken.model.VerificationToken;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.model.User;
import rss_aggregator.server.users.UserDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.context.MessageSource;
import rss_aggregator.server.users.UserService;

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

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    @ResponseBody
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDTO userDto = new UserDTO();
        model.addAttribute("user", userDto);
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    @ResponseBody
    public String registerUser(final HttpServletRequest request) {
        String user = request.getParameter("username");
        String password = request.getParameter("password");

        User registered = createUserAccount(user, password);
        if (registered == null) {
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
    public String confirmSignup(WebRequest request, Model model, @RequestParam("token") String token) {

        String result = userService.validateVerificationToken(token);
        JSONObject response = new JSONObject();
        switch (result) {
            case UserService.TOKEN_VALID:
                response.put("status", "ok");
                break;
            case UserService.TOKEN_EXPIRED:
                response.put("status", "error").put("errno", "token expired");
                break;
            case UserService.TOKEN_INVALID:
                response.put("status", "error").put("errno", "token invalid");
                break;
        }

        return response.toString();
    }

    @RequestMapping(value = "/resendSignupToken", method = RequestMethod.GET)
    @ResponseBody
    public String resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
        // newToken is the old token after update
        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUserByVerificationToken(newToken.getToken());

        String confirmationUrl
                = "/signupConfirm.html?token=" + newToken.getToken();
        String message = "Registration success, niquel, super";

        new SendMail(mailSender, user.getEmail(), "super", "resend Signup confirmation",
                message + "\r\n" + "http://localhost:8080" + confirmationUrl);
        return new JSONObject().put("status", "ok").toString();
    }

}
