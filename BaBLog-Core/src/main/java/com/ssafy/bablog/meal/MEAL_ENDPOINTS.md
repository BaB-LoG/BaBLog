# MealController 엔드포인트 정리

기준 경로: `/meals`  
인증: 모두 `Authorization: Bearer <token>` 필요

## 공통 개요
- 주요 테이블: `meal`, `meal_food`, `meal_log`, `food`, `member_nutrient_daily`
- 핵심 FK 관계(DDL 기준):
  - `meal.member_id -> member.id`
  - `meal_food.meal_id -> meal.id`
  - `meal_food.food_id -> food.id`
  - `meal_log.meal_id -> meal.id`
  - `meal_log.member_id -> member.id`
  - `member_nutrient_daily.member_id -> member.id`
- 영양 누적 원칙:
  - `meal_food` 변경은 항상 `meal`과 `meal_log`에 영양 델타를 반영합니다.
  - 섭취량(intake)은 `food.standard` 대비 비율로 영양을 스케일링합니다.
  - `meal_log`는 식사 단위 누적 로그로 `meal_id` 기준 upsert 됩니다.
- 트랜잭션 범위:
  - `MealService`는 클래스 레벨 `@Transactional`이며, 조회 전용 메서드는 `readOnly=true`입니다.
  - `meal_food` 변경(추가/수정/삭제)은 `meal`과 `meal_log` 누적이 같은 트랜잭션 내에서 처리됩니다.
- 락/동시성 관점:
  - 동일 식단에 대한 동시 업데이트가 들어오면 `meal`/`meal_log` 영양 누적이 경쟁 상태가 될 수 있습니다.
  - 현재는 낙관적 락/버전 칼럼이 없어, 동시 요청이 많다면 누적 값이 마지막 요청 기준으로 덮일 수 있습니다.
- 인덱스 관점:
  - DDL에 이미 정의된 인덱스:
    - `meal`: `idx_meal_member(member_id)`, `idx_meal_member_date_type(member_id, meal_date, meal_type)`, `idx_meal_member_type(member_id, meal_type)`
    - `meal_food`: `idx_meal_food_meal_food(meal_id, food_id)`(UNIQUE), `idx_meal_food_food_id(food_id)`
    - `meal_log`: `idx_meal_log_meal(meal_id)`(UNIQUE), `idx_meal_log_member_logged_at(member_id, logged_at)`, `idx_meal_log_logged_at(logged_at)`
    - `member_nutrient_daily`: `idx_member_nutrient_daily_member_date(member_id, target_date)`(UNIQUE)
- 예외/응답 규칙(요약):
  - 403: 식단 소유자 불일치(본인 식단만 수정/삭제 가능)
  - 404: 식단/식품/meal_food 미존재
  - 400: `mealId` 불일치 등 요청 파라미터 오류

## 1) 기본 식단 수동 생성 (테스트용)
- `POST /meals/test-api`
- 설명: 로그인 사용자 기준으로 오늘 날짜의 기본 식단(BREAKFAST/LUNCH/DINNER/SNACK)을 생성합니다.
- 요청 바디: 없음
- 응답: 200 OK, body 없음
- 영향 테이블: `meal`, `member_nutrient_daily`
- 비즈니스 로직:
  - 사용자의 오늘 날짜 스냅샷(`member_nutrient_daily`)이 없으면 생성합니다.
  - `meal`에서 (memberId, mealType, date) 조합이 없는 경우만 생성합니다.
- DB 관점:
  - `meal`은 `idx_meal_member_date_type(member_id, meal_date, meal_type)` 유니크 인덱스로 중복이 방지됩니다.
  - 영양 칼럼(`kcal` 등)은 `decimal(6,2)`로 저장되며 최초 생성 시 null 상태입니다.
- 예외/에러:
  - 인증 실패 시 401
  - 서비스 계층 내부에서 별도 예외는 발생하지 않으나, DB 에러 시 500

## 2) 식단에 음식 추가
- `POST /meals/foods`
- 설명: 지정한 식사 타입/날짜의 식단에 음식을 추가하고 영양을 누적합니다.
- 요청 바디: `AddMealFoodRequest`
- 응답: 200 OK, `AddMealFoodResponse`
- 영향 테이블: `meal`, `meal_food`, `meal_log`, `food`
- 비즈니스 로직:
  - (memberId, mealType, mealDate) 식단이 없으면 생성 후 추가합니다.
  - `meal_food`에 신규 추가 후, 해당 음식의 영양 델타를 `meal`과 `meal_log`에 누적합니다.
  - 응답은 추가된 음식과 해당 식단의 최신 영양 상태를 포함합니다.
- DB 관점:
  - `meal_food`는 `idx_meal_food_meal_food(meal_id, food_id)` 유니크 인덱스가 있어 동일 식품 중복 추가 시 충돌합니다.
  - `meal_log`는 `idx_meal_log_meal(meal_id)` 유니크 인덱스 기반 upsert 대상이며, `logged_at`은 식단 날짜의 00:00으로 저장됩니다.
- 예외/에러:
  - 404: `foodId` 없음
  - 403: 식단 소유자 불일치
  - 409 또는 500: `meal_food` 유니크 제약 충돌(동일 식품 중복 추가)

## 3) 날짜별 식단 목록 조회
- `GET /meals?date=yyyy-MM-dd`
- 설명: 해당 날짜의 식단 목록(BREAKFAST/LUNCH/DINNER/SNACK)과 음식 목록/영양을 반환합니다.
- 요청 파라미터: `date`(필수)
- 응답: 200 OK, `List<MealWithFoodsResponse>`
- 영향 테이블: `meal`, `meal_food`, `food`, `meal_log`
- 비즈니스 로직:
  - 날짜 기준으로 해당 사용자의 `meal`을 조회합니다.
  - `meal_food`와 `food`를 조인해 음식 목록을 구성합니다.
  - 영양은 `meal_log`가 있으면 `meal_log` 기준, 없으면 `meal`의 누적 값을 사용합니다.
- DB 관점:
  - 실제 조회는 `meal`에서 `(member_id, meal_date, meal_type)` 인덱스를 활용할 수 있습니다.
  - 음식 목록은 `meal_food`의 `meal_id` 인덱스와 `food_id` 인덱스를 통해 조인됩니다.
- 예외/에러:
  - 400: `date` 형식 오류
  - 401: 인증 실패

## 4) 대시보드 요약 조회
- `GET /meals/summary?date=yyyy-MM-dd`
- 설명: 해당 날짜의 총합 영양, 목표, 식사별 요약을 반환합니다.
- 요청 파라미터: `date`(필수)
- 응답: 200 OK, `DashboardSummaryResponse`
- 영향 테이블: `meal`, `meal_food`, `food`, `member_nutrient_daily`
- 비즈니스 로직:
  - 해당 날짜의 모든 `meal` 영양 합계를 계산합니다.
  - `member_nutrient_daily`를 조회해 목표값을 응답에 포함합니다.
  - 식사별 요약은 음식 개수, 대표 음식(첫 음식), 식사 kcal을 포함합니다.
- DB 관점:
  - 목표값은 `member_nutrient_daily(member_id, target_date)` 유니크 인덱스로 일자 단위 1건을 보장합니다.
  - `member_nutrient_daily`가 없으면 서비스 레이어에서 0으로 채운 스냅샷을 구성합니다.
- 예외/에러:
  - 400: `date` 형식 오류
  - 401: 인증 실패

## 5) 식단 단건 조회
- `GET /meals/{mealId}`
- 설명: 특정 식단 1건의 음식 목록/영양을 반환합니다.
- 경로 파라미터: `mealId`(필수)
- 응답: 200 OK, `MealWithFoodsResponse`
- 영향 테이블: `meal`, `meal_food`, `food`, `meal_log`
- 비즈니스 로직:
  - `mealId`가 로그인 사용자 소유인지 확인합니다.
  - 단일 식단의 음식 목록과 영양을 조합해 응답합니다.
- DB 관점:
  - `meal`은 `member_id` FK가 있어 사용자 삭제 시 FK 정책에 따라 영향을 받을 수 있습니다(현재 DDL에는 ON DELETE 규칙 명시 없음).
- 예외/에러:
  - 404: `mealId` 없음
  - 403: 식단 소유자 불일치

## 6) 식단에 추가된 음식 삭제
- `DELETE /meals/foods/{mealFoodId}`
- 설명: 식단에 추가된 음식을 삭제하고 영양을 차감합니다.
- 경로 파라미터: `mealFoodId`(필수)
- 응답: 204 No Content
- 영향 테이블: `meal_food`, `meal`, `meal_log`, `food`
- 비즈니스 로직:
  - `meal_food`와 연관된 `meal` 소유자를 검증합니다.
  - 기존 음식의 영양 델타를 `meal`/`meal_log`에서 차감합니다.
  - 이후 `meal_food`를 삭제합니다.
- DB 관점:
  - 삭제는 `meal_food`의 물리 삭제이며, FK로 `meal`과 `food`에 종속됩니다.
  - `meal`/`meal_log`의 영양 합계는 업데이트 쿼리로 직접 감소되며 null 방지를 위해 서비스에서 보정합니다.
- 예외/에러:
  - 404: `mealFoodId` 없음
  - 403: 식단 소유자 불일치

## 7) 식단에 추가된 음식 수정
- `PATCH /meals/foods/{mealFoodId}`
- 설명: 식단에 추가된 음식의 식품/섭취량/단위를 수정하고 영양을 재계산합니다.
- 요청 바디: `UpdateMealFoodRequest`
- 응답: 200 OK, `MealWithFoodsResponse`
- 영향 테이블: `meal_food`, `meal`, `meal_log`, `food`
- 비즈니스 로직:
  - `mealFoodId`로 기존 레코드를 조회하고 소유자를 검증합니다.
  - 기존 영양을 `meal`/`meal_log`에서 차감 후, 신규 값으로 다시 누적합니다.
  - 응답은 수정 후 최신 식단 상태를 반환합니다.
- DB 관점:
  - `mealId`가 기존과 다르면 다른 식단으로 이동할 수 없도록 400 처리합니다(관계 일관성 유지).
  - `foodId` 변경은 `meal_food(meal_id, food_id)` 유니크 인덱스에 의해 충돌 가능성이 있습니다.
- 예외/에러:
  - 400: `mealId` 불일치
  - 404: `mealFoodId` 또는 `foodId` 없음
  - 403: 식단 소유자 불일치
  - 409 또는 500: `meal_food` 유니크 제약 충돌
