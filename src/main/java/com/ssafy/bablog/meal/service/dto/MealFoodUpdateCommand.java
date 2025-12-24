package com.ssafy.bablog.meal.service.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MealFoodUpdateCommand {
    private final Long mealId;
    private final Long foodId;
    private final BigDecimal intake;
    private final String unit;
}
