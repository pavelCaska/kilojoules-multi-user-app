package com.pc.kilojoules.entity;

import java.math.BigDecimal;

public interface JournalEntry {
    BigDecimal getQuantity();
    BigDecimal getKiloJoules();
    BigDecimal getProteins();
    BigDecimal getCarbohydrates();
    BigDecimal getFiber();
    BigDecimal getFat();


}
