package com.ssafy.bablog.member_nutrient.domain;

import com.ssafy.bablog.member_nutrient.service.dto.MemberNutrientUpdateCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberNutrient {
    private Long id;
    private Long memberId;
    private BigDecimal kcal;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal saturatedFat;
    private BigDecimal transFat;
    private BigDecimal carbohydrates;
    private BigDecimal sugar;
    private BigDecimal natrium;
    private BigDecimal cholesterol;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void apply(MemberNutrientUpdateCommand command) {
        if (command == null) {
            return;
        }
        this.kcal = firstNonNull(command.getKcal(), this.kcal);
        this.protein = firstNonNull(command.getProtein(), this.protein);
        this.fat = firstNonNull(command.getFat(), this.fat);
        this.saturatedFat = firstNonNull(command.getSaturatedFat(), this.saturatedFat);
        this.transFat = firstNonNull(command.getTransFat(), this.transFat);
        this.carbohydrates = firstNonNull(command.getCarbohydrates(), this.carbohydrates);
        this.sugar = firstNonNull(command.getSugar(), this.sugar);
        this.natrium = firstNonNull(command.getNatrium(), this.natrium);
        this.cholesterol = firstNonNull(command.getCholesterol(), this.cholesterol);
    }

    private <T> T firstNonNull(T first, T second) {
        return Objects.nonNull(first) ? first : second;
    }
}
