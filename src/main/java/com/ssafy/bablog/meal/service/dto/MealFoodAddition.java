package com.ssafy.bablog.meal.service.dto;

import com.ssafy.bablog.food.domain.Food;
import com.ssafy.bablog.meal.domain.Meal;
import com.ssafy.bablog.meal.domain.MealFood;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MealFoodAddition {
    private final Meal meal;
    private final MealFood mealFood;
    private final Food food;
}
