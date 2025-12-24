package com.ssafy.bablog.meal.service.dto;

import com.ssafy.bablog.meal.domain.Meal;
import com.ssafy.bablog.meal.repository.mapper.MealFoodWithFood;
import com.ssafy.bablog.meal_log.domain.MealLog;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MealAggregate {
    private final Meal meal;
    private final List<MealFoodWithFood> foods;
    private final MealLog mealLog;
}
