package rss_aggregator.server.signup;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    @Value("${rss_aggregator.ip}")
    private String ip;

    @Autowired
    private IUserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(final HttpServletRequest request) {
        return "signup";
    }

    @RequestMapping(value = "/signupWeb", method = RequestMethod.POST)
    public String registerUserWeb(final HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String user = request.getParameter("username");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        String result = processRegisterUser(user, password, confirm, request.getContextPath());

        if (!result.equals("ok")) {
            redirectAttributes.addAttribute("error", result);
            return "redirect:/error";
        }

        redirectAttributes.addAttribute("msg", "A confirmation link has been sent to you email to validate your registration");
        return "redirect:/login";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    @ResponseBody
    public String registerUser(final HttpServletRequest request, HttpServletResponse response) {
        String user = request.getParameter("username");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        String result = processRegisterUser(user, password, confirm, request.getContextPath());

        if (!result.equals("ok")) {
            response.setStatus(400);
            return new JSONObject().put("status", "error").put("error", result).toString();
        }

        return new JSONObject()
                .put("status", "ok")
                .toString();
    }

    private String processRegisterUser(final String user, final String password, final String confirm, final String contextPath) {
        EmailValidator emailValidator = new EmailValidator();
        if (!emailValidator.isValid(user)) {
            return "Invalid email";
        }

        PasswordValidator passwordValidator = new PasswordValidator();
        if (password == null || !passwordValidator.isValid(password, confirm)) {
            return "Invalid password";
        }

        User registered = createUserAccount(user, password);
        if (registered == null) {
            return "User already exists";
        }

        sendRegistrationConfirmationMail(contextPath, registered);

        return "ok";
    }

    private void sendRegistrationConfirmationMail(final String contextPath, final User user) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String token = UUID.randomUUID().toString();
            userService.createVerificationToken(user, token);

            String confirmationUrl
                    = contextPath + "/signupConfirm.html?token=" + token;
            String message = "Registration success, niquel, super";

            new SendMail(mailSender, user.getEmail(), "super", "Signup confirmation",
                    message + "\r\n" + "http://" + ip + ":8080" + confirmationUrl);
        });
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
    public String confirmSignup(HttpServletResponse response, @RequestParam("token") final String token, RedirectAttributes redirectAttributes) {

        String page, key, value;

        String result = userService.validateVerificationToken(token);
        switch (result) {
            case UserService.TOKEN_VALID:
                page = "login";
                key = "msg";
                value = "Signup confirmed";
                break;
            case UserService.TOKEN_EXPIRED:
                response.setStatus(400);
                page = "error";
                key = "error";
                value = "Token expired";
                break;
            case UserService.TOKEN_INVALID:
                response.setStatus(400);
                page = "error";
                key = "error";
                value = "Invalid token";
                break;
            default:
                response.setStatus(500);
                page = "error";
                key = "error";
                value = "Unknown error";
        }

        redirectAttributes.addAttribute(key, value);
        return "redirect:/" + page;
    }

    @RequestMapping(value = "/resendSignupToken", method = RequestMethod.GET)
    public String resendRegistrationToken(HttpServletResponse response, @RequestParam("token") final String existingToken, RedirectAttributes redirectAttributes) {

        System.out.println("resendsignuptoken");
        if (userService.getVerificationToken(existingToken) == null) {
            response.setStatus(400);
            redirectAttributes.addAttribute("error", "invalid token");
            return "redirect:/error";
        }

        // newToken is the old token after update
        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUserByVerificationToken(newToken.getToken());

        String confirmationUrl = "/signupConfirm.html?token=" + newToken.getToken();
        String message = "Registration success, niquel, super";

        new SendMail(mailSender, user.getEmail(), "super", "resend Signup confirmation",
                message + "\r\n" + "http://" + ip + ":8080" + confirmationUrl);

        redirectAttributes.addAttribute("msg", "Registration token re-sent");
        return "redirect:/login";
    }

}
