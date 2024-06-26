package com.pc.kilojoules.controller;

import com.pc.kilojoules.entity.Food;
import com.pc.kilojoules.entity.Portion;
import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.exception.RecordNameExistsException;
import com.pc.kilojoules.exception.RecordNotDeletableException;
import com.pc.kilojoules.exception.RecordNotFoundException;
import com.pc.kilojoules.service.FoodService;
import com.pc.kilojoules.service.PortionService;
import com.pc.kilojoules.service.UserProfileService;
import com.pc.kilojoules.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PortionController {

    private final PortionService portionService;
    private final FoodService foodService;
    private final UserService userService;
    private final UserProfileService userProfileService;
    private static final Logger log = LoggerFactory.getLogger(PortionController.class);

    @Autowired
    public PortionController(PortionService portionService, FoodService foodService, UserService userService, UserProfileService userProfileService) {
        this.portionService = portionService;
        this.foodService = foodService;
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @GetMapping("/food/{id}/portion")
    public String createPortion(@PathVariable("id") Long id, Model model, @ModelAttribute("portion") Portion portion, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", userProfileService.fetchUserProfileByUser(user));

        try {
            Food food = foodService.getFoodById(id);
            model.addAttribute("food", food);
            model.addAttribute("portions", food.getPortions());
            return "editPortion";

        } catch (RecordNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error:", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Data access error");
        }
        return "redirect:/food";
    }

    @PostMapping("/food/{id}/portion")
    public String savePortion(@PathVariable("id") Long id, @Valid @ModelAttribute("portion") Portion portion, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation failed");
            return "redirect:/food/" + id + "/portion";
        }
        try {
            Food food = foodService.getFoodById(id);
            Portion savedPortion = portionService.createPortion(food, portion);
            redirectAttributes.addFlashAttribute("successMessage", "Portion " + savedPortion.getPortionName() + " saved successfully.");
            return "redirect:/food/" + id + "/portion";

        } catch (RecordNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/food";
        } catch (RecordNameExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/food/" + id + "/portion";
        } catch (DataAccessException e) {
            log.error("Database access error:", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Data access error");
            return "redirect:/food";
        }
    }

    @PostMapping("/food/{foodId}/portion/{id}/delete")
    public String deletePortion(@PathVariable Long foodId, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        if(!portionService.existsPortionByIdAndFoodId(id, foodId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Food record with id " + foodId + " is not associated with Portion record with id " + id);
        }
        try {
            Portion portion = portionService.deletePortionById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Portion " + portion.getPortionName() + " deleted.");
        } catch (RecordNotFoundException | RecordNotDeletableException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error:", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Data access error");
        }
        return "redirect:/food/" + foodId + "/portion";
    }
}
