# Meal 패키지 책임/역할 정리

이 문서는 `com.ssafy.bablog.meal` 패키지의 클래스 책임과 역할을 요약합니다.

## controller
- `MealController`: 식단 관련 REST 엔드포인트를 제공하고, 인증 사용자 기준으로 `MealService`를 호출합니다.

## controller/dto
- `AddMealFoodRequest`: 식단에 음식 추가 요청 DTO(식사 타입, 날짜, foodId, 섭취량, 단위).
- `AddMealFoodResponse`: 식단 추가 결과(식단 요약 + 추가된 음식 응답).
- `UpdateMealFoodRequest`: 식단 음식 수정 요청 DTO(mealId 필수, foodId/intake/unit 선택).
- `MealResponse`: 식단 기본 정보 + 영양 정보 응답(foods는 필요 시 null).
- `MealWithFoodsResponse`: 식단 단건/목록 응답(foods 포함, mealLog 기반 영양 포함).
- `MealFoodResponse`: 식단에 포함된 음식 + 섭취량 기반 영양 정보 응답.
- `NutritionResponse`: 영양 성분 응답(식단/로그/목표 값을 매핑).
- `MealSummaryResponse`: 대시보드용 식사별 요약(식사 타입, 대표 음식, kcal 등).
- `DashboardSummaryResponse`: 대시보드용 일자 요약(총합/목표/식사별 요약).

## domain
- `Meal`: 식단 엔티티. 영양 델타 생성 및 델타 적용(누적) 책임을 보유합니다.
- `MealFood`: 식단-음식 매핑 엔티티. 섭취량/단위 갱신 책임을 보유합니다.
- `MealType`: 식사 타입 열거형(BREAKFAST/LUNCH/DINNER/SNACK).

## service
- `MealService`: 식단 생성/조회/수정/삭제의 핵심 유스케이스를 오케스트레이션합니다.
- `MealReadSupport`: 식단 조회 시 필요한 음식/로그 데이터를 묶어 로드하는 읽기 지원 컴포넌트입니다.
- `MealScheduler`: 매일 00시에 모든 사용자에 대해 기본 식단 레코드를 생성합니다.

## util
- `NutritionCalculator`: 기준량 대비 섭취량 스케일링을 수행하는 영양 계산 유틸입니다.
- `MealSummaryAssembler`: 대시보드 요약 계산(총합/식사별 요약)을 담당합니다.

## repository
- `MealRepository`/`MealRepositoryImpl`: 식단 CRUD 및 영양 누적 업데이트 접근 계층입니다.
- `MealFoodRepository`/`MealFoodRepositoryImpl`: 식단-음식 매핑 CRUD 접근 계층입니다.

## repository/mapper
- `MealMapper`: `meal` 테이블 관련 MyBatis 매퍼입니다.
- `MealFoodMapper`: `meal_food` 테이블 관련 MyBatis 매퍼입니다.
- `MealFoodWithFood`: 식단-음식-식품 조인 조회 결과 매핑 DTO입니다.
