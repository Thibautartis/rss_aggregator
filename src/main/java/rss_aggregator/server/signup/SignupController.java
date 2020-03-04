package rss_aggregator.server.signup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
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
import rss_aggregator.server.security.VerificationToken;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.User;
import rss_aggregator.server.users.UserDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.MessageSource;
import rss_aggregator.server.users.UserService;


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
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDTO userDto = new UserDTO();
        model.addAttribute("user", userDto);
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid final UserDTO userDto, BindingResult result, WebRequest request, Errors errors) {

        User registered = null;
        if (!result.hasErrors()) {
            registered = createUserAccount(userDto, result);
        }
        if (registered == null) {
            result.rejectValue("email", "message.regError");
        }

        try {
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnSignupCompleteEvent(registered, request.getLocale(), appUrl));
        } catch (Exception e) {
            System.out.println(e);
        }

        return new ModelAndView(result.hasErrors() ? "signup" : "successRegister", "user", userDto);
    }

    private User createUserAccount(UserDTO accountDto, BindingResult result) {
        User registered = null;
        try {
            registered = userService.registerNewUserAccount(accountDto);
        } catch (EmailExistsException e) {
            System.out.println(e);
        }
        return registered;
    }

    @RequestMapping(value = "/signupConfirm", method=RequestMethod.GET)
    public String confirmSignup(WebRequest request, Model model, @RequestParam("token") String token) {

        String result = userService.validateVerificationToken(token);
        switch (result) {
            case UserService.TOKEN_VALID:
                result = "redirect:/login.html";
                break;
            case UserService.TOKEN_EXPIRED:
                result = "redirect:/error.html?error=token_expired";
                break;
            case UserService.TOKEN_INVALID:
                result = "redirect:/error.html?error=token_invalid";
                break;
        }

        return result;
    }

    @RequestMapping(value = "/resendSignupToken", method = RequestMethod.GET)
    @ResponseBody
    public String resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUser(newToken.getToken());

        String confirmationUrl
                = "/signupConfirm.html?token=" + newToken.getToken();
        String message = "Registration success, niquel, super";

        new SendMail(mailSender, user.getEmail(), "super", "resend Signup confirmation",
                message + "\r\n" + "http://localhost:8080" + confirmationUrl);
        return "redirect:/login.html";
    }

}
