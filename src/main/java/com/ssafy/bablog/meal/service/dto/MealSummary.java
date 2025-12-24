package com.ssafy.bablog.meal.service.dto;

import com.ssafy.bablog.meal.domain.MealType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MealSummary {
    private final MealType mealType;
    private final LocalDate mealDate;
    private final int foodCount;
    private final String representative;
    private final BigDecimal kcal;
}
