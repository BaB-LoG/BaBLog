package com.ssafy.bablog.meal.service.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DashboardSummary {
    private final LocalDate mealDate;
    private final NutritionSummary totals;
    private final NutritionSummary targets;
    private final List<MealSummary> summaries;
}
