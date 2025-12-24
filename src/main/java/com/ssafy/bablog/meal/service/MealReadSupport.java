package com.ssafy.bablog.meal.service;

import com.ssafy.bablog.meal.repository.MealFoodRepository;
import com.ssafy.bablog.meal.repository.mapper.MealFoodWithFood;
import com.ssafy.bablog.meal_log.domain.MealLog;
import com.ssafy.bablog.meal_log.repository.MealLogRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MealReadSupport {
    // Read-only aggregation for foods/logs by meal id.

    private final MealFoodRepository mealFoodRepository;
    private final MealLogRepository mealLogRepository;

    public Map<Long, List<MealFoodWithFood>> mealFoodsByMealIds(List<Long> mealIds) {
        if (mealIds.isEmpty()) {
            return Map.of();
        }

        List<MealFoodWithFood> rows = mealFoodRepository.findByMealIdsWithFood(mealIds);
        Map<Long, List<MealFoodWithFood>> map = new HashMap<>();
        for (MealFoodWithFood row : rows) {
            map.computeIfAbsent(row.getMealFood().getMealId(), k -> new ArrayList<>()).add(row);
        }
        return map;
    }

    public Map<Long, MealLog> mealLogsByMealIds(List<Long> mealIds) {
        if (mealIds.isEmpty()) {
            return Map.of();
        }
        List<MealLog> logs = mealLogRepository.findByMealIds(mealIds);
        Map<Long, MealLog> map = new HashMap<>();
        for (MealLog log : logs) {
            map.put(log.getMealId(), log);
        }
        return map;
    }
}
