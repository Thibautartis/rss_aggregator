package rss_aggregator.server.signup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import rss_aggregator.server.exceptions.EmailExistsException;
import rss_aggregator.server.security.VerificationToken;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.User;
import rss_aggregator.server.users.UserDTO;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;
import org.springframework.context.MessageSource;


@Controller
public class SignupController {

    @Autowired
    private IUserService userService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Qualifier("messageSource")
    @Autowired
    MessageSource messages;

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

        Locale locale = request.getLocale();

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/signupConfirmFail.html?lang=" + locale.getLanguage();
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = messages.getMessage("auth.message.expired", null, locale);
            model.addAttribute("message", messageValue);
            return "redirect:/signupConfirmFail.html?lang=" + locale.getLanguage();
        }

        user.setActivated(true);
        userService.saveRegisteredUser(user);
        return "redirect:/login.html?lang=" + request.getLocale().getLanguage();
    }

}
