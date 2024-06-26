package com.pc.kilojoules.controller;

import com.pc.kilojoules.entity.Food;
import com.pc.kilojoules.entity.Journal;
import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.exception.RecordNotFoundException;
import com.pc.kilojoules.model.JournalFoodFormDTO;
import com.pc.kilojoules.service.*;
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

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class JournalFoodController {

    private final FoodService foodService;
    private final JournalService journalService;
    private final UserService userService;
    private final UserProfileService userProfileService;
    private static final Logger log = LoggerFactory.getLogger(FoodController.class);

    @Autowired
    public JournalFoodController(FoodService foodService, JournalService journalService, UserService userService, UserProfileService userProfileService) {
        this.foodService = foodService;
        this.journalService = journalService;
        this.userService = userService;
        this.userProfileService = userProfileService;
    }


    @GetMapping("/journal/food/{foodId}/add")
    public String addFoodToJournalGet(@PathVariable("foodId") Long id, Model model, @ModelAttribute JournalFoodFormDTO formDTO, RedirectAttributes redirectAttributes) {
            User user = userService.getCurrentUser();
            model.addAttribute("username", user.getUsername());
            model.addAttribute("user", userProfileService.fetchUserProfileByUser(user));

        try {
            Food food = foodService.getFoodById(id);
            model.addAttribute("food", food);
            model.addAttribute("portions", food.getPortions());
            formDTO.setDate(LocalDate.now());
            formDTO.setFoodName(food.getName());
            model.addAttribute("formDTO", formDTO);
            return "addFoodToJournal";

        } catch (RecordNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error:", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Data access error");
        }
        return "redirect:/journal/search";
    }

    @PostMapping("/journal/food/{foodId}/add")
    public String addFoodToJournalPost(@PathVariable("foodId") Long id,
                                       @Valid @ModelAttribute("formDTO") JournalFoodFormDTO formDTO, BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation failed.");
            return "redirect:/journal/food/" + id + "/add";
        }
            User user = userService.getCurrentUser();
            BigDecimal savedQuantity = formDTO.getQuantity().multiply(formDTO.getPortionSize());
        try {
            Food food = foodService.getFoodById(id);
            Journal journal = journalService.addFoodToJournal(user, food, savedQuantity, formDTO.getDate(), formDTO.getMealType(), formDTO.getFoodName());
            redirectAttributes.addFlashAttribute("successMessage",  journal.getJournalFood().getName() + " added to journal successfully");
            return "redirect:/journal";
        } catch (RecordNotFoundException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error:", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Data access error");
        }
        return "redirect:/journal/search";
    }

    @GetMapping("/journal/{JournalId}/food/{journalFoodId}/edit")
    public String editJournalFoodGet(@PathVariable("JournalId") Long journalId, @PathVariable("journalFoodId") Long id, Model model,
                                     @ModelAttribute("formDTO") JournalFoodFormDTO formDTO,
                                     RedirectAttributes redirectAttributes) {
        if(!journalService.existsJournalByIdAndJournalFoodId(journalId, id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Food record with id " + id + " is not associated with Journal record with id " + journalId);
            return "redirect:/journal";
        }
            User user = userService.getCurrentUser();
            model.addAttribute("username", user.getUsername());
            model.addAttribute("user", userProfileService.fetchUserProfileByUser(user));

        try {
            Journal entry = journalService.getJournalEntryById(journalId);
            model.addAttribute("entry", entry);
            formDTO.setDate(entry.getConsumedAt());
            formDTO.setMealType(entry.getMealType().toString());
            formDTO.setFoodName(entry.getJournalFood().getName());
            model.addAttribute("formDTO", formDTO);

            return "editJournalFood";

        } catch (RecordNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error:", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Data access error");
        }
        return "redirect:/journal";
    }

    @PostMapping("/journal/{JournalId}/food/{journalFoodId}/edit")
    public String editJournalFoodPost(@PathVariable("JournalId") Long journalId, @PathVariable("journalFoodId") Long id,
                                      @Valid @ModelAttribute("formDTO") JournalFoodFormDTO formDTO, BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        if(!journalService.existsJournalByIdAndJournalFoodId(journalId, id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Food record with id " + id + " is not associated with Journal record with id " + journalId);
            return "redirect:/journal";
        }
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation failed.");
            return "redirect:/journal/food/" + id + "/edit";
        }
        try {
            BigDecimal savedQuantity = formDTO.getQuantity().multiply(formDTO.getPortionSize());
            Journal journal = journalService.updateJournalFood(journalId, savedQuantity, formDTO.getDate(), formDTO.getMealType(), formDTO.getFoodName());
            redirectAttributes.addFlashAttribute("successMessage", journal.getJournalFood().getName() + " updated successfully.");

        } catch (RecordNotFoundException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error:", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Data access error");
        }
        return "redirect:/journal";
    }
}
