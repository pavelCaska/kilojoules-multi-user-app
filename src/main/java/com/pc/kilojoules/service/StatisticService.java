package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.model.JournalTotalsDTO;
import com.pc.kilojoules.model.TopTenDTO;

import java.time.LocalDate;
import java.util.List;

public interface StatisticService {

    JournalTotalsDTO calculateJournalTotalsByDate(LocalDate date, User user);

    JournalTotalsDTO calculateJournalTotalsByPeriod(LocalDate startDate, LocalDate endDate, User user);

    List<TopTenDTO> getTop10ByKiloJoules(User user, LocalDate startDate, LocalDate endDate);

    List<TopTenDTO> getTop10ByKiloJoulesCount(User user, LocalDate startDate, LocalDate endDate);

    List<TopTenDTO> getTop10ByProteins(User user, LocalDate startDate, LocalDate endDate);

    List<TopTenDTO> getTop10ByProteinsCount(User user, LocalDate startDate, LocalDate endDate);

    List<TopTenDTO> getTop10ByCarbohydrates(User user, LocalDate startDate, LocalDate endDate);

    List<TopTenDTO> getTop10ByCarbohydratesCount(User user, LocalDate startDate, LocalDate endDate);

    List<TopTenDTO> getTop10ByFiber(User user, LocalDate startDate, LocalDate endDate);

    List<TopTenDTO> getTop10ByFiberCount(User user, LocalDate startDate, LocalDate endDate);

    List<TopTenDTO> getTop10ByFat(User user, LocalDate startDate, LocalDate endDate);

    List<TopTenDTO> getTop10ByFatCount(User user, LocalDate startDate, LocalDate endDate);
}
