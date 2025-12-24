package com.ssafy.bablog.member_nutrient.service.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberNutrientUpdateCommand {
    private final BigDecimal kcal;
    private final BigDecimal protein;
    private final BigDecimal fat;
    private final BigDecimal saturatedFat;
    private final BigDecimal transFat;
    private final BigDecimal carbohydrates;
    private final BigDecimal sugar;
    private final BigDecimal natrium;
    private final BigDecimal cholesterol;

    public boolean hasAnyValue() {
        return kcal != null
                || protein != null
                || fat != null
                || saturatedFat != null
                || transFat != null
                || carbohydrates != null
                || sugar != null
                || natrium != null
                || cholesterol != null;
    }
}
