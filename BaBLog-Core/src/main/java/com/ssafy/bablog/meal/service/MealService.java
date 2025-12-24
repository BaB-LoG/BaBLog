package com.ssafy.bablog.meal.service;

import com.ssafy.bablog.food.domain.Food;
import com.ssafy.bablog.food.repository.FoodRepository;
import com.ssafy.bablog.meal.domain.Meal;
import com.ssafy.bablog.meal.domain.MealFood;
import com.ssafy.bablog.meal.domain.MealType;
import com.ssafy.bablog.meal.repository.MealFoodRepository;
import com.ssafy.bablog.meal.repository.MealRepository;
import com.ssafy.bablog.meal.repository.mapper.MealFoodWithFood;
import com.ssafy.bablog.meal.service.dto.DashboardSummary;
import com.ssafy.bablog.meal.service.dto.MealAggregate;
import com.ssafy.bablog.meal.service.dto.MealFoodAddCommand;
import com.ssafy.bablog.meal.service.dto.MealFoodAddition;
import com.ssafy.bablog.meal.service.dto.MealFoodUpdateCommand;
import com.ssafy.bablog.meal.service.dto.MealSummary;
import com.ssafy.bablog.meal.service.dto.NutritionSummary;
import com.ssafy.bablog.meal.util.MealSummaryAssembler;
import com.ssafy.bablog.meal_log.domain.MealLog;
import com.ssafy.bablog.meal_log.repository.MealLogRepository;
import com.ssafy.bablog.member_nutrient.domain.MemberNutrientDaily;
import com.ssafy.bablog.member_nutrient.service.MemberNutrientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final MealFoodRepository mealFoodRepository;
    private final FoodRepository foodRepository;
    private final MealLogRepository mealLogRepository;
    private final MemberNutrientService memberNutrientService;
    private final MealReadSupport mealReadSupport;

    /**
     * Member 1명의 하루치 기본 Meal 레코드를 생성 (존재하면 건너뜀)
     */
    public void createDailyMeals(Long memberId, LocalDate mealDate) {
        memberNutrientService.ensureTodaySnapshot(memberId);
        for (MealType mealType : MealType.values()) {
            getOrCreateMeal(memberId, mealType, mealDate);
        }
    }

    /**
     * 식단 추가
     * 식단에 Food 추가 및 영양소 누적
     */
    public MealFoodAddition addFoodToMeal(Long memberId, MealFoodAddCommand command) {
        // 1. 어느 사용자의 아침 / 점심 / 저녁 / 간식 중 어떤 형태의 며칠날 식단인지 찾기
        Meal meal = getOrCreateMeal(memberId, command.getMealType(), command.getMealDate());
        // 2. 요청을 보낸 사용자가 이 식단의 소유주가 맞는지 검증
        ensureMealOwner(meal, memberId);

        // 3. foodId를 통해 food를 찾기
        Food food = findFoodOrThrow(command.getFoodId());

        // 4. meal과 food의 중간 테이블
        MealFood mealFood = MealFood.create(meal.getId(), food.getId(), command.getIntake(), command.getUnit());
        // 5. 저장하기
        mealFoodRepository.save(mealFood);

        // 6. 추가한 음식의 영양 정보를 해당 날짜의 mealLog/meal에 누적하기
        applyNutritionDelta(meal, food, command.getIntake());

        return new MealFoodAddition(meal, mealFood, food);
    }

    /**
     * 날짜별 식단 조회
     * member의 mealDate 일자의 BREAK_FAST, LUNCH, DINNER, SNACK을 모두 조회해 오는 메서드
     */
    @Transactional(readOnly = true)
    public List<MealAggregate> getMeals(Long memberId, LocalDate mealDate) {
        List<Meal> meals = mealRepository.findByMemberAndDate(memberId, mealDate);
        List<Long> mealIds = meals.stream().map(Meal::getId).toList();
        Map<Long, List<MealFoodWithFood>> foodsByMeal = mealReadSupport.mealFoodsByMealIds(mealIds);
        Map<Long, MealLog> logsByMeal = mealReadSupport.mealLogsByMealIds(mealIds);

        return meals.stream()
                .map(meal -> new MealAggregate(
                        meal,
                        foodsByMeal.getOrDefault(meal.getId(), List.of()),
                        logsByMeal.get(meal.getId())
                ))
                .toList();
    }

    /**
     * 식단 단일 조회
     * BREAK_FAST 단일 조회, LUNCH 단일 조회...
     * 클라이언트에 mealId가 있고, mealId가 있으면 날짜, 아침 점심 저녁 간식을 구분하지 않아도 되기 때문에
     * mealId를 받아오는 형태로 구현
     */
    @Transactional(readOnly = true)
    public MealAggregate getMeal(Long memberId, Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "식단 정보를 찾을 수 없습니다."));
        ensureMealOwner(meal, memberId);
        return buildMealAggregate(meal);
    }

    /**
     * mealFood 삭제
     * 삭제 시 해당 일자에 누적된 meal, meal_log의 영양 정보에도 반영(adjustNutrition 메서드)
     */
    public void deleteMealFood(Long memberId, Long mealFoodId) {
        MealFoodContext context = loadMealFoodContext(mealFoodId, memberId);

        applyNutritionReverseDelta(context.meal(), context.food(), context.mealFood().getIntake());

        mealFoodRepository.deleteById(mealFoodId);
    }

    /**
     * mealfood 수정
     * 식단 음식 수정 시 기존값만큼 차감 이후 새값만큼 증가
     */
    public MealAggregate updateMealFood(Long memberId, Long mealFoodId, MealFoodUpdateCommand command) {
        MealFoodContext context = loadMealFoodContext(mealFoodId, memberId);

        if (!context.mealFood().getMealId().equals(command.getMealId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mealId가 기존 식단와 일치하지 않습니다.");
        }

        // 기존 값
        // 요청값이 없으면 기존 값 유지
        MealFood existing = context.mealFood();
        Food oldFood = context.food();
        BigDecimal oldIntake = existing.getIntake();

        Food newFood = command.getFoodId() != null
                ? findFoodOrThrow(command.getFoodId())
                : oldFood;
        BigDecimal newIntake = command.getIntake() != null ? command.getIntake() : oldIntake;
        String newUnit = command.getUnit() != null ? command.getUnit() : existing.getUnit();

        // 기존 영양 차감
        applyNutritionReverseDelta(context.meal(), oldFood, oldIntake);

        // 신규 값 적용
        existing.update(newFood.getId(), newIntake, newUnit);
        mealFoodRepository.update(existing);
        applyNutritionDelta(context.meal(), newFood, newIntake);

        return buildMealAggregate(context.meal());
    }

    /**
     * 대시보드용 식단 요약
     */
    @Transactional(readOnly = true)
    public DashboardSummary getDailySummary(Long memberId, LocalDate mealDate) {
        List<Meal> meals = mealRepository.findByMemberAndDate(memberId, mealDate);
        Map<MealType, Meal> mealsByType = indexMealsByType(meals);

        List<Long> mealIds = meals.stream().map(Meal::getId).toList();
        Map<Long, List<MealFoodWithFood>> foodsByMeal = mealReadSupport.mealFoodsByMealIds(mealIds);
        NutritionSummary totals = MealSummaryAssembler.sumTotals(meals);

        MemberNutrientDaily dailyTarget = memberNutrientService.getDaily(memberId, mealDate);
        NutritionSummary targets = NutritionSummary.from(dailyTarget);
        List<MealSummary> summaries = MealSummaryAssembler.buildSummaries(mealDate, mealsByType, foodsByMeal);

        return new DashboardSummary(mealDate, totals, targets, summaries);
    }

    // -------------------------------------  이하 private  ---------------------------------------

    private void ensureMealOwner(Meal meal, Long memberId) {
        if (!meal.getMemberId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 식단만 수정할 수 있습니다.");
        }
    }

    private Meal getOrCreateMeal(Long memberId, MealType mealType, LocalDate mealDate) {
        return mealRepository.findByMemberAndTypeAndDate(memberId, mealType, mealDate)
                .orElseGet(() -> mealRepository.save(Meal.create(memberId, mealType, mealDate)));
    }

    private Food findFoodOrThrow(Long foodId) {
        return foodRepository.findById(foodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "음식 정보를 찾을 수 없습니다."));
    }

    private void adjustNutrition(Meal meal, Meal delta) {
        mealRepository.adjustNutrition(meal.getId(), delta);
        meal.applyNutritionDelta(delta);
    }

    private void applyNutritionDelta(Meal meal, Food food, BigDecimal intake) {
        Meal mealDelta = Meal.nutritionDelta(food, intake);
        adjustNutrition(meal, mealDelta);
        mealLogRepository.upsertNutrition(MealLog.from(meal, food, intake));
    }

    private void applyNutritionReverseDelta(Meal meal, Food food, BigDecimal intake) {
        Meal reversedDelta = Meal.reverseDelta(Meal.nutritionDelta(food, intake));
        adjustNutrition(meal, reversedDelta);
        mealLogRepository.upsertNutrition(MealLog.from(meal, food, intake).reverseDelta());
    }

    private MealFoodContext loadMealFoodContext(Long mealFoodId, Long memberId) {
        MealFood mealFood = mealFoodRepository.findById(mealFoodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "식단에 추가된 음식을 찾을 수 없습니다."));

        Meal meal = mealRepository.findById(mealFood.getMealId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "식단 정보를 찾을 수 없습니다."));
        ensureMealOwner(meal, memberId);

        Food food = foodRepository.findById(mealFood.getFoodId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "음식 정보를 찾을 수 없습니다."));

        return new MealFoodContext(meal, mealFood, food);
    }

    private MealAggregate buildMealAggregate(Meal meal) {
        Long mealId = meal.getId();
        Map<Long, List<MealFoodWithFood>> foodsByMeal = mealReadSupport.mealFoodsByMealIds(List.of(mealId));
        Map<Long, MealLog> logsByMeal = mealReadSupport.mealLogsByMealIds(List.of(mealId));
        return new MealAggregate(
                meal,
                foodsByMeal.getOrDefault(mealId, List.of()),
                logsByMeal.get(mealId)
        );
    }

    private Map<MealType, Meal> indexMealsByType(List<Meal> meals) {
        Map<MealType, Meal> map = new HashMap<>();
        for (Meal meal : meals) {
            map.put(meal.getMealType(), meal);
        }
        return map;
    }

    /**
     * meal_food 수정/삭제 시 사용하는 조회 컨텍스트
     */
    private record MealFoodContext(Meal meal, MealFood mealFood, Food food) {
    }

}
