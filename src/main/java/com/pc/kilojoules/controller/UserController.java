package com.pc.kilojoules.controller;

import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.entity.UserProfile;
import com.pc.kilojoules.exception.RecordNotFoundException;
import com.pc.kilojoules.model.UserRegistrationDto;
import com.pc.kilojoules.service.UserProfileService;
import com.pc.kilojoules.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;

@Controller
public class UserController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    private static final Logger log = LoggerFactory.getLogger(FoodController.class);

    public UserController(UserService userService, UserProfileService userProfileService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @GetMapping({"/","/login"})
    public String login(@RequestParam(value="error", required = false) String error, @ModelAttribute User user, Model model) {
        if(error!=null) {
            model.addAttribute("errorMessage", "Login failed. You can register ");
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }

    @GetMapping("/admin/list-users")
    public String fetchUsers(Model model, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", userProfileService.fetchUserProfileByUser(user));
        model.addAttribute("users", userService.fetchCombinedUserData());

        return "listUsers";
    }

    @GetMapping("/admin/list-admins")
    public String fetchAdmins(Model model, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", userProfileService.fetchUserProfileByUser(user));
        model.addAttribute("admins", userService.fetchCombinedAdminData());

        return "listAdmins";
    }

    @GetMapping("/user/profile")
    public String showUserProfile(Model model) {
        User user = userService.getCurrentUser();
        UserProfile userProfile = userProfileService.fetchUserProfileByUser(user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", userProfile);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String createdAt = (userProfile.getCreatedAt() != null ) ? formatter.format(userProfile.getCreatedAt()) : "Not yet created";
        String updatedAt = (userProfile.getUpdatedAt() != null ) ? formatter.format(userProfile.getUpdatedAt()) : "Not yet updated";
        model.addAttribute("createdAt", createdAt);
        model.addAttribute("updatedAt", updatedAt);

        return "userProfile";
    }

    @PostMapping("/user/profile")
    public String createUserProfile(@Valid @ModelAttribute("user") UserProfile userProfile, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = String.format("Error in field '%s': %s", fieldError.getField(), fieldError.getDefaultMessage());
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            redirectAttributes.addFlashAttribute("userProfile", userProfile);
            return "redirect:/user/profile";
        }
        User currentUser = userService.getCurrentUser();
        if(userProfileService.existsUserProfileByUser(currentUser)) {
            UserProfile currentUserProfile = userProfileService.fetchUserProfileByUser(currentUser);
            if(currentUserProfile.getEmail() == null && userProfileService.existsUserProfileByEmail(userProfile.getEmail()) ||
                    userProfile.getEmail() != null && currentUserProfile.getEmail() != null && !currentUserProfile.getEmail().equals(userProfile.getEmail()) && userProfileService.existsUserProfileByEmail(userProfile.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "This email already exists.");
                redirectAttributes.addFlashAttribute("userProfile", userProfile);
                return "redirect:/user/profile";
            }
            UserProfile updatedUserProfile = userProfileService.updateUserProfile(currentUserProfile, userProfile);

            redirectAttributes.addFlashAttribute("successMessage", "User profile updated successfully.");
            redirectAttributes.addFlashAttribute("userProfile", updatedUserProfile);

        } else {
            if (userProfileService.existsUserProfileByEmail(userProfile.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "This email already exists.");
                redirectAttributes.addFlashAttribute("userProfile", userProfile);
                return "redirect:/user/profile";
            }
            UserProfile newProfile = userProfileService.createUserProfile(currentUser, userProfile);
            redirectAttributes.addFlashAttribute("successMessage", "User profile created successfully.");
            redirectAttributes.addFlashAttribute("userProfile", newProfile);
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/register")
    public String registerUserGet(Model model, @ModelAttribute("user") UserRegistrationDto dto) {
        model.addAttribute("user", dto);
        return "register";
    }

    @PostMapping("/register")
    public String registerUserPost(@Valid @ModelAttribute UserRegistrationDto dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation failed!");
            return "redirect:/register";
        }
        if (userService.existsUserByUsername(dto.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username already exists.");
            redirectAttributes.addFlashAttribute("user", new UserRegistrationDto(dto.getUsername(), null, null));
            return "redirect:/register";
        }
        if (!dto.getPassword().equals(dto.getMatchingPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match!");
            redirectAttributes.addFlashAttribute("user", new UserRegistrationDto(dto.getUsername(), null, null));
            return "redirect:/register";
        }

        User registeredUser = userService.createUser(dto);
        redirectAttributes.addFlashAttribute("successMessage", "New user " + registeredUser.getUsername() + " was registered successfully.");

        return "redirect:/login";
    }


    @PostMapping("/user/{userId}/delete")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User record with id " + userId + " was deleted successfully.");
        } catch (RecordNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/list-users";
    }
}
