package com.pc.kilojoules.controller;

import com.pc.kilojoules.entity.Journal;
import com.pc.kilojoules.entity.MealType;
import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.exception.RecordNotFoundException;
import com.pc.kilojoules.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class JournalController {

    private final JournalService journalService;
    private final StatisticService statisticService;
    private final UserService userService;
    private final UserProfileService userProfileService;

    private static final Logger log = LoggerFactory.getLogger(JournalController.class);

    @Autowired
    public JournalController(JournalService journalService, StatisticService statisticService, UserService userService, UserProfileService userProfileService) {
        this.journalService = journalService;
        this.statisticService = statisticService;
        this.userService = userService;
        this.userProfileService = userProfileService;
    }


    @GetMapping("/journal")
    public String showJournalForDate(Model model, @RequestParam(name = "date", defaultValue = "#{T(java.time.LocalDate).now()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        User user = userService.getCurrentUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", userProfileService.fetchUserProfileByUser(user));

        model.addAttribute("entriesB", journalService.findJournalEntriesByDateAndUserOrderByMealType(date, MealType.BREAKFAST, user));
        model.addAttribute("entriesM", journalService.findJournalEntriesByDateAndUserOrderByMealType(date, MealType.MID_MORNING_SNACK, user));
        model.addAttribute("entriesL", journalService.findJournalEntriesByDateAndUserOrderByMealType(date, MealType.LUNCH, user));
        model.addAttribute("entriesA", journalService.findJournalEntriesByDateAndUserOrderByMealType(date, MealType.AFTERNOON_SNACK, user));
        model.addAttribute("entriesD", journalService.findJournalEntriesByDateAndUserOrderByMealType(date, MealType.DINNER, user));
        model.addAttribute("date", date);
        model.addAttribute("previousDate", date.minusDays(1).toString());
        model.addAttribute("nextDate", date.plusDays(1).toString());
        model.addAttribute("dayTotals", statisticService.calculateJournalTotalsByDate(date, user));
        return "journal";
    }

    @GetMapping("/journal/statistics")
    public String showStatistics(Model model,
                                 @RequestParam(name = "startDate", defaultValue = "#{T(java.time.LocalDate).now().minusDays(2)}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam(name = "endDate", defaultValue = "#{T(java.time.LocalDate).now()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        User user = userService.getCurrentUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", userProfileService.fetchUserProfileByUser(user));

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("periodTotals", statisticService.calculateJournalTotalsByPeriod(startDate, endDate, user));
        model.addAttribute("topKiloJoules", statisticService.getTop10ByKiloJoules(user, startDate, endDate));
        model.addAttribute("topKiloJoulesCount", statisticService.getTop10ByKiloJoulesCount(user, startDate, endDate));
        model.addAttribute("topProteins", statisticService.getTop10ByProteins(user, startDate, endDate));
        model.addAttribute("topProteinsCount", statisticService.getTop10ByProteinsCount(user, startDate, endDate));
        model.addAttribute("topCarbs", statisticService.getTop10ByCarbohydrates(user, startDate, endDate));
        model.addAttribute("topCarbsCount", statisticService.getTop10ByCarbohydratesCount(user, startDate, endDate));
        model.addAttribute("topFiber", statisticService.getTop10ByFiber(user, startDate, endDate));
        model.addAttribute("topFiberCount", statisticService.getTop10ByFiberCount(user, startDate, endDate));
        model.addAttribute("topFat", statisticService.getTop10ByFat(user, startDate, endDate));
        model.addAttribute("topFatCount", statisticService.getTop10ByFatCount(user, startDate, endDate));

        return "journalStatistics";
    }

    @PostMapping("/journal/{id}/delete")
    public String deleteJournalEntry(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Journal journal = journalService.deleteJournal(id);
            redirectAttributes.addFlashAttribute("successMessage", "Journal entry " + (journal.getJournalFood() != null ? journal.getJournalFood().getName() : journal.getJournalMeal().getMealName()) + " deleted successfully");
            return "redirect:/journal";
        } catch (RecordNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error:", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Data access error");
        }
        return "redirect:/journal";
    }
}
