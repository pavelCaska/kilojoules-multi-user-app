package com.pc.kilojoules.service;

import com.pc.kilojoules.model.FoodCSVRecord;

import java.io.File;
import java.util.List;

public interface FoodCsvService {
        List<FoodCSVRecord> convertCSV(File csvFile);

    }
