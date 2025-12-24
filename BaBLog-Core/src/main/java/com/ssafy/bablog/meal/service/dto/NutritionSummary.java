package com.ssafy.bablog.meal.service.dto;

import com.ssafy.bablog.meal.domain.Meal;
import com.ssafy.bablog.meal_log.domain.MealLog;
import com.ssafy.bablog.member_nutrient.domain.MemberNutrientDaily;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NutritionSummary {
    private BigDecimal kcal;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal saturatedFat;
    private BigDecimal transFat;
    private BigDecimal carbohydrates;
    private BigDecimal sugar;
    private BigDecimal natrium;
    private BigDecimal cholesterol;

    public static NutritionSummary zero() {
        return new NutritionSummary(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    public static NutritionSummary from(MealLog mealLog) {
        if (mealLog == null) {
            return zero();
        }
        return new NutritionSummary(
                mealLog.getKcal(),
                mealLog.getProtein(),
                mealLog.getFat(),
                mealLog.getSaturatedFat(),
                mealLog.getTransFat(),
                mealLog.getCarbohydrates(),
                mealLog.getSugar(),
                mealLog.getNatrium(),
                mealLog.getCholesterol()
        );
    }

    public static NutritionSummary fromMeal(Meal meal) {
        if (meal == null) {
            return zero();
        }
        return new NutritionSummary(
                meal.getKcal(),
                meal.getProtein(),
                meal.getFat(),
                meal.getSaturatedFat(),
                meal.getTransFat(),
                meal.getCarbohydrates(),
                meal.getSugar(),
                meal.getNatrium(),
                meal.getCholesterol()
        );
    }

    public static NutritionSummary from(MemberNutrientDaily daily) {
        if (daily == null) {
            return zero();
        }
        return new NutritionSummary(
                daily.getKcal(),
                daily.getProtein(),
                daily.getFat(),
                daily.getSaturatedFat(),
                daily.getTransFat(),
                daily.getCarbohydrates(),
                daily.getSugar(),
                daily.getNatrium(),
                daily.getCholesterol()
        );
    }

    public void addFromMeal(Meal meal) {
        if (meal == null) {
            return;
        }
        this.kcal = add(this.kcal, meal.getKcal());
        this.protein = add(this.protein, meal.getProtein());
        this.fat = add(this.fat, meal.getFat());
        this.saturatedFat = add(this.saturatedFat, meal.getSaturatedFat());
        this.transFat = add(this.transFat, meal.getTransFat());
        this.carbohydrates = add(this.carbohydrates, meal.getCarbohydrates());
        this.sugar = add(this.sugar, meal.getSugar());
        this.natrium = add(this.natrium, meal.getNatrium());
        this.cholesterol = add(this.cholesterol, meal.getCholesterol());
    }

    private BigDecimal add(BigDecimal base, BigDecimal value) {
        BigDecimal safeBase = base == null ? BigDecimal.ZERO : base;
        BigDecimal safeValue = value == null ? BigDecimal.ZERO : value;
        return safeBase.add(safeValue);
    }
}
