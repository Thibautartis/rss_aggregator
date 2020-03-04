package rss_aggregator.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import rss_aggregator.server.exceptions.EmailExistsException;
import rss_aggregator.server.users.IUserService;
import rss_aggregator.server.users.User;
import rss_aggregator.server.users.UserDTO;

import javax.validation.Valid;

@Controller
public class SignupController {

    @Autowired
    private IUserService userService;

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
}
