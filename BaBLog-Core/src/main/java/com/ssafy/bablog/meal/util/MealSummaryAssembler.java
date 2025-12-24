package com.ssafy.bablog.meal.util;

import com.ssafy.bablog.meal.service.dto.MealSummary;
import com.ssafy.bablog.meal.service.dto.NutritionSummary;
import com.ssafy.bablog.meal.domain.Meal;
import com.ssafy.bablog.meal.domain.MealType;
import com.ssafy.bablog.meal.repository.mapper.MealFoodWithFood;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MealSummaryAssembler {

    private MealSummaryAssembler() {
    }

    public static NutritionSummary sumTotals(List<Meal> meals) {
        NutritionSummary totals = NutritionSummary.zero();
        for (Meal meal : meals) {
            totals.addFromMeal(meal);
        }
        return totals;
    }

    public static List<MealSummary> buildSummaries(
            LocalDate mealDate,
            Map<MealType, Meal> mealsByType,
            Map<Long, List<MealFoodWithFood>> foodsByMeal
    ) {
        List<MealSummary> summaries = new ArrayList<>();
        for (MealType mealType : MealType.values()) {
            Meal meal = mealsByType.get(mealType);
            if (meal == null) {
                summaries.add(new MealSummary(mealType, mealDate, 0, null, BigDecimal.ZERO));
                continue;
            }
            List<MealFoodWithFood> foods = foodsByMeal.getOrDefault(meal.getId(), List.of());
            int foodCount = foods.size();
            String representative = foodCount > 0 ? foods.get(0).getFood().getName() : null;
            summaries.add(new MealSummary(
                    mealType,
                    meal.getMealDate(),
                    foodCount,
                    representative,
                    orZero(meal.getKcal())
            ));
        }
        return summaries;
    }

    private static BigDecimal orZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
