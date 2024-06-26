package com.pc.kilojoules.controller;

import com.pc.kilojoules.entity.*;
import com.pc.kilojoules.exception.RecordNotFoundException;
import com.pc.kilojoules.model.MealDTO;
import com.pc.kilojoules.model.UnsavedMealFormDTO;
import com.pc.kilojoules.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SearchController {
    private final SearchService searchService;
    private final MealService mealService;
    private final JournalMealService journalMealService;
    private final JournalService journalService;
    private final UserService userService;
    private final UserProfileService userProfileService;
    private static final Logger log = LoggerFactory.getLogger(SearchController.class);
    @Autowired
    public SearchController(SearchService searchService, MealService mealService, JournalMealService journalMealService, JournalService journalService, UserService userService, UserProfileService userProfileService) {
        this.searchService = searchService;
        this.mealService = mealService;
        this.journalMealService = journalMealService;
        this.journalService = journalService;
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @GetMapping("/journal/search")
    public String searchResults(@RequestParam(value = "query", required = false) String query, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", userProfileService.fetchUserProfileByUser(user));
        if (query != null) {
            List<Food> foodResults = searchService.searchFoodsByName(query);
            List<Meal> meals = searchService.searchMealsByMealName(query);
            if(foodResults.isEmpty() && meals.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No such food or meal found");
                return "redirect:/journal/search";
            }

            List<MealDTO> mealsDTO = mealService.calculateAndReturnMealDtoList(meals);
            model.addAttribute("foodResults", foodResults);
            model.addAttribute("mealResults", mealsDTO);
        }
        return "searchItemsForJournal";
    }

    @GetMapping("/journal/meal/{id}/food/search")
    public String searchFoodForNewMeal(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes,
                                       @ModelAttribute("formDTO") UnsavedMealFormDTO formDTO,
                                       @RequestParam(name = "query") String query) {
        User user = userService.getCurrentUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", userProfileService.fetchUserProfileByUser(user));

        try {
            JournalMeal journalMeal = journalMealService.getJournalMealById(id);
            model.addAttribute("jm", journalMeal);
            model.addAttribute("foods", searchService.searchFoodsByName(query));

            return "addJmfToNewMealToJournal";

        } catch (RecordNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error:", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Data access error");

        }
        return "redirect:/journal/search";
    }

    @GetMapping("/journal/{journalId}/meal/{journalMealId}/food/search")
    public String searchFoodForJournalMeal(@PathVariable("journalId") Long journalId, @PathVariable("journalMealId") Long id,
                                           Model model, RedirectAttributes redirectAttributes,
                                           @ModelAttribute("formDTO") UnsavedMealFormDTO formDTO,
                                           @RequestParam(name = "query") String query) {
        if(!journalService.existsJournalByIdAndJournalMealId(journalId, id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Meal record with id " + id + " is not associated with Journal record with id " + journalId);
            return "redirect:/journal";
        }
        User user = userService.getCurrentUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", userProfileService.fetchUserProfileByUser(user));

        try {
            Journal entry = journalService.getJournalEntryById(journalId);
            model.addAttribute("entry", entry);
            model.addAttribute("foods", searchService.searchFoodsByName(query));

            return "addJournalMealFood";

        } catch (RecordNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error:", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Data access error");
        }
        return "redirect:/journal";
    }
}
