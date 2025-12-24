# BaBLog 통합 컨텍스트

이 문서는 BaBLog-FE(프론트엔드)와 BaBLog-BE(백엔드)를 함께 이해하기 위한 통합 요약입니다.
구체적인 구현 질문에 답할 수 있도록 주요 구조, 흐름, 파일 위치를 정리했습니다.

## 전체 구조
- 루트: `BaBLog-FE/`(Vue 3) + `BaBLog-Core/`(공통 도메인/서비스) + `BaBLog-BE/`(Spring Boot) + `BaBLog-Batch/`(Spring Batch).
- 공통 참고 자료: FE의 `BaBLog-FE/docs/*.json`, BE의 `BaBLog-BE/src/main/resources/static/*-api.json`, 스키마는 `BaBLog-BE/src/main/resources/static/DDL.sql`.
- AI 평가 기준(설계안): `AI_EVALUATION_GUIDE.md`.

## 프론트엔드 (BaBLog-FE)

### 기술 스택
- Vue 3 + Vite, vue-router, Pinia, Tailwind CSS, axios, date-fns, jwt-decode.
- 모듈 타입: `package.json`에 `type: module`.

### 엔트리/라우팅
- 엔트리: `BaBLog-FE/src/main.js`
  - `createApp(App)` 후 `pinia` 등록 → `useUserStore(pinia).hydrate()`로 로컬 스토리지 인증 복원 → `router` 등록.
- 라우터: `BaBLog-FE/src/router/index.js`
  - 레이아웃 분리: `PublicLayout`(비인증), `DashboardLayout`(인증)
  - 경로:
    - `/` → `HomePage`(guestOnly)
    - `/dashboard` → `DashboardPage`
    - `/meal-log` → `MealLogPage`
    - `/reports` → `ReportsPage`
    - `/goals` → `GoalsPage`(현재 안내 메시지)
    - `/mypage` → `MyPage`
  - 가드: 인증 필요 페이지는 비로그인 시 `alert` 후 `/`로 이동, 게스트 전용 페이지는 로그인 시 `/dashboard`로 이동.

### 상태 관리
- `BaBLog-FE/src/stores/userStore.js`
  - `tokenType`, `accessToken`, `member`, `isAuthenticated` 저장.
  - `authHeader` getter는 `Bearer <token>` 구성.
  - `saveUser` 시 `localStorage`의 `bablog_auth`에 저장, `hydrate`로 복원, `logout`은 삭제.

### API 클라이언트 및 엔드포인트
- 공통: `BaBLog-FE/src/services/apiClient.js`
  - `baseURL: http://localhost:8080`.
  - 요청 인터셉터에서 `Authorization` 헤더 주입.
- 회원: `BaBLog-FE/src/services/memberService.js`
  - `POST /members/signup`, `POST /members/login`
  - `POST /members/password-check`, `POST /members/password-change`
  - `PATCH /members/info`
  - `GET /members/nutrients`, `POST /members/nutrients/recalculate`, `PATCH /members/nutrients`
- 식단: `BaBLog-FE/src/services/mealService.js`
  - `GET /meals?date=yyyy-MM-dd`, `GET /meals/summary?date=yyyy-MM-dd`
  - `POST /meals/foods`, `DELETE /meals/foods/{id}`, `PATCH /meals/foods/{id}`
- 식품 검색: `BaBLog-FE/src/services/foodService.js`
  - `GET /foods/search?name=&vendor=`
- 일일 목표: `BaBLog-FE/src/services/memberNutrientService.js`
  - `GET /members/nutrients/daily?date=yyyy-MM-dd`
- 리포트: `BaBLog-FE/src/services/reportService.js`
  - `GET /reports/daily?date=yyyy-MM-dd`, `POST /reports/daily?date=yyyy-MM-dd`
  - `GET /reports/weekly?date=yyyy-MM-dd`

### 주요 화면/컴포넌트 동작
- `DashboardPage.vue`
  - `GET /meals/summary`로 당일 요약(식사별 대표 음식/총량/목표).
  - `GET /reports/daily`로 어제 일간 리포트 조회.
  - 주간 요약 일부는 현재 정적 데이터.
- `MealLogPage.vue`
  - 날짜 선택/이동 기능(`date-fns`).
  - `getMealsByDate`로 식단 조회, `getDailyTargets`로 목표 영양 조회.
  - `meals` 상태를 `BREAKFAST/LUNCH/DINNER/SNACK`으로 분기하여 합산.
  - 영양 요약은 `NutrientProgress` 컴포넌트로 표시, 점수는 `kcal` 달성률 기반.
  - `AddMealFoodModal`로 음식 추가 → `addMealFood` 호출.
  - `POST /reports/daily`로 일간 리포트 생성 요청 가능.
- `ReportsPage.vue`
  - `GET /reports/weekly`로 주간 리포트 조회.
  - `GET /reports/daily`로 선택 날짜 일간 리포트 조회.
- `MyPage.vue`
  - 비밀번호 재확인 후 프로필/비밀번호 변경 기능 활성화.
  - 프로필 수정: `PATCH /members/info` 결과를 Pinia에 반영.
  - 권장 섭취량: `GET /members/nutrients`, `POST /members/nutrients/recalculate`, `PATCH /members/nutrients`.
- `AddMealFoodModal.vue`
  - `foods/search`로 검색, 선택된 음식 + 섭취량으로 영양 계산(기준 g 대비 비율 적용).
  - 계산 결과를 부모에 전달 후 모달 종료.
- 레이아웃
  - `PublicLayout.vue`: 로그인/회원가입 모달 포함.
  - `DashboardLayout.vue`: 사이드바 라우팅 + 프로필 메뉴(마이 페이지/로그아웃).

### 스타일/디자인 시스템
- Tailwind 설정: `BaBLog-FE/tailwind.config.js`
  - `colors` 토큰(예: `primary`, `background-light`, `card-dark`) 정의.
  - `darkMode: 'class'` 사용.
- 전역 스타일: `BaBLog-FE/src/style.css`
  - 최소 너비 `1280px`, Material Symbols 아이콘 폰트 사용.

### 기타
- `BaBLog-FE/wireframe/*.html`: 초기 와이어프레임 스냅샷.
- 테스트 러너 없음(수동 검증 위주).

## 백엔드 (BaBLog-BE)

### 기술 스택
- Spring Boot 3.5.8, Java 17, MyBatis, MySQL, Spring Security, JWT(jjwt), Spring AI(OpenAI).
- 빌드/실행: `./mvnw spring-boot:run`, 테스트: `./mvnw test`.

### 설정 및 보안
- `BaBLog-BE/src/main/resources/application.yml`
  - DB 연결 정보, JWT 시크릿, Spring AI(OpenAI) 설정이 하드코딩되어 있음(실환경에서는 환경 변수로 덮어쓰기 권장).
- `SecurityConfig`
  - stateless 세션, JWT 필터 적용.
  - `/members/signup`, `/members/login`, Swagger 경로는 공개.
  - CORS 허용: `http://localhost:5173`, `http://127.0.0.1:5173`, `http://localhost:8080`.
- JWT
  - `JwtTokenProvider`: HS256, `security.jwt.expiration-millis` 사용.
  - `JwtAuthenticationFilter`: 토큰 유효성 및 블랙리스트 체크.
  - `TokenBlacklistService`: 메모리 기반 블랙리스트 + 5분 주기 정리.

### 애플리케이션 엔트리
- `BaBLogBeApplication`
  - `@MapperScan`으로 MyBatis 매퍼 스캔.
  - `@EnableScheduling` 활성화(현재 실제 스케줄 실행은 배치 모듈에서 담당).

### 주요 컨트롤러/엔드포인트
- 회원: `MemberController`
  - `POST /members/signup`, `POST /members/login`, `POST /members/logout`
  - `GET /members/info`, `PATCH /members/info`
  - `DELETE /members/sign-out`(비밀번호 필요)
  - `POST /members/password-check`, `POST /members/password-change`
- 식품: `FoodController`
  - `GET /foods/search`(name/vendor 부분 일치 검색)
- 식단: `MealController`
  - `GET /meals?date=yyyy-MM-dd`(하루 전체 식단)
  - `GET /meals/summary?date=yyyy-MM-dd`(대시보드 요약)
  - `GET /meals/{mealId}`(단일 식단)
  - `POST /meals/foods`(음식 추가)
  - `PATCH /meals/foods/{mealFoodId}`(음식 수정)
  - `DELETE /meals/foods/{mealFoodId}`(음식 삭제)
  - `POST /meals/test-api`(테스트용 식단 생성)
- 권장 섭취량: `MemberNutrientController`
  - `GET /members/nutrients`, `POST /members/nutrients/recalculate`, `PATCH /members/nutrients`
  - `GET /members/nutrients/daily?date=yyyy-MM-dd`
- 리포트: `ReportController`
  - `POST /reports/daily?date=yyyy-MM-dd`(일간 리포트 생성)
  - `POST /reports/weekly?date=yyyy-MM-dd`(주간 리포트 생성)
  - `GET /reports/daily?date=yyyy-MM-dd`(일간 리포트 조회, 없으면 204)
  - `GET /reports/weekly?date=yyyy-MM-dd`(주간 리포트 조회, 없으면 204)

### 서비스 핵심 로직
- `MemberService`
  - 이메일을 소문자/트림 정규화 후 가입 처리.
  - 로그인은 `AuthenticationManager` → JWT 발급.
  - 로그아웃은 토큰 블랙리스트 등록.
  - 프로필 수정, 비밀번호 확인/변경, 회원 탈퇴 지원.
- `MemberNutrientService`
  - 키/몸무게 기반 권장 섭취량 계산/저장.
  - `MemberNutrientDaily` 스냅샷을 일자 기준으로 보관(과거 기록 보존용).
  - 값이 없으면 0으로 채운 스냅샷 반환.
- `MemberNutrientCalculator`
  - Mifflin-St Jeor 식, 나이 30세, 활동계수 1.2 가정.
  - 탄수/지방/단백질/당류 비율 기반 계산.
- `MealService`
  - 일자/식사타입 별 `Meal` 생성 후 `MealFood` 추가.
  - 추가/수정/삭제 시 `Meal` 및 `MealLog` 영양 합계 동기화.
  - 조회 시 `Meal` + `MealFood` + `MealLog`를 합쳐 응답.
- `ReportService`
  - 일간/주간 리포트 생성 및 조회, 결과는 DB upsert.
  - 일간: 실제 섭취/목표/식사 요약을 묶어 AI 요청.
  - 주간: 주간 날짜 범위의 일간 지표 모아 AI 요청.
- `ReportAiService`
  - Spring AI `ChatClient`로 JSON 스키마 기반 리포트 생성.
  - 응답 JSON 파싱 실패 시 예외 처리.

### 데이터 계층
- MyBatis 매퍼 XML: `BaBLog-Core/src/main/resources/mapper/*.xml`.
- Repository 인터페이스 + Impl 패턴으로 Mapper 호출(Core 모듈).
- 리포트 도메인: `daily_report`, `weekly_report` 테이블에 AI 평가 결과 저장.

## FE-BE 연동 포인트
- FE는 `Authorization: Bearer <token>` 사용. 토큰은 로컬 스토리지에 저장됨.
- `isAuthenticated`는 토큰 유무 기준이며 서버 검증과 별개이므로, 서버 응답 오류 처리가 필요할 수 있음.
- 영양 목표/식단/리포트 조회는 날짜 파라미터 `yyyy-MM-dd` 규격을 전제로 함.
- 리포트 조회 API는 데이터가 없을 경우 204(No Content)를 반환함.

## 배치 (BaBLog-Batch)

### 기술 스택
- Spring Boot 3.5.8, Spring Batch, MySQL, Java 17 (Core 모듈의 MyBatis/서비스 재사용).

### 현 상태
- 엔트리: `BaBLog-Batch/src/main/java/com/ssafy/bablog/BaBLogBatchApplication.java`.
- 잡/스텝 구성: `dailyMealInitJob`, `dailyReportJob`, `weeklyReportJob` (각각 Tasklet 기반).
- 스케줄러: `BatchJobScheduler`
  - 일일 식단 생성: 00:00 (당일 기준)
  - 일간 리포트 생성: 00:05 (전날 기준)
  - 주간 리포트 생성: 월요일 00:15 (전주 월~일 기준)
- 대상 사용자: `MemberIdProvider`가 전체 회원 ID를 조회.
- 실패 처리: `batch_failure_log` 테이블에 실패 내역 기록.
- 설정 파일: `BaBLog-Batch/src/main/resources/application.yml` (gitignore에 포함될 수 있음).

## 코어 (BaBLog-Core)

### 역할
- 공통 도메인/서비스/리포지토리(MyBatis)와 리포트 AI 로직을 BE/Batch가 공유.
- BE는 컨트롤러/보안, Batch는 스케줄/잡만 보유.

## DB 스키마 요약 (DDL)
- `member`: `email`, `password`, `name`, `gender`, `birth_date`, `height_cm`, `weight_kg` (email 유니크).
- `member_nutrient`: 회원별 권장 섭취량(영양 성분 9종). `member_id` FK.
- `member_nutrient_daily`: 날짜별 스냅샷(`target_date` 유니크 키: `member_id`+`target_date`).
- `meal`: 회원/날짜/식사 타입(`BREAKFAST|LUNCH|DINNER|SNACK`)별 집계 영양.
- `meal_food`: 식단-음식 매핑(`meal_id`+`food_id` 유니크), 섭취량 `intake`/`unit`.
- `meal_log`: 식단 단위 영양 누적 로그(`meal_id` 유니크, `member_id` FK).
- `food`: 기준량(`standard`), 영양 정보, `food_weight`, `vendor`.
- `daily_report`, `weekly_report`: AI 평가 리포트(점수, 설명, 추천).
- `batch_failure_log`: 배치 실패 이력.
- `goal`: 목표(일/주)와 진행값 저장(현재 FE는 준비 단계).

## API 요청/응답 요약 (샘플 기준)
- 회원
  - `POST /members/signup`: `email`, `password`, `name`, `gender` 필수. 선택: `birthDate`, `heightCm`, `weightKg`. 응답 없음.
  - `POST /members/login`: 응답 `tokenType`, `accessToken`, `member{ id, email, name, gender, birthDate, heightCm, weightKg }`.
  - `GET /members/info`: `member` 상세 반환.
  - `PATCH /members/info`: `name`, `gender`, `birthDate`, `heightCm`, `weightKg` 부분 수정 가능.
  - `POST /members/password-check`: `{ password }` → `true/false`.
  - `POST /members/password-change`: `{ currentPassword, newPassword }` → 응답 없음.
  - `DELETE /members/sign-out`: `{ password }` 필요.
- 식단
  - `POST /meals/foods`: `{ mealType, mealDate, foodId, intake, unit }` → `meal` + `mealFood` 응답.
  - `GET /meals?date=yyyy-MM-dd`: `meal` 배열(각 `foods` 포함).
  - `GET /meals/summary?date=yyyy-MM-dd`: 대시보드 요약(총합/목표/식사별 요약).
  - `GET /meals/{mealId}`: 단일 `meal` + `foods`.
  - `PATCH /meals/foods/{mealFoodId}`: `{ mealId, foodId?, intake?, unit? }` → 수정된 `meal` 반환.
  - `DELETE /meals/foods/{mealFoodId}`: 응답 없음.
- 음식
  - `GET /foods/search?name=&vendor=`: `food` 리스트 반환(부분 일치).
- 권장 섭취량
  - `GET /members/nutrients`: `memberId` + 영양 성분 9종.
  - `POST /members/nutrients/recalculate`: 키/체중 기반 재계산 후 반환.
  - `PATCH /members/nutrients`: 영양 성분 부분 수정.
  - `GET /members/nutrients/daily?date=yyyy-MM-dd`: 일별 스냅샷 반환.
- 리포트
  - `POST /reports/daily?date=yyyy-MM-dd`: 일간 리포트 생성 후 반환.
  - `POST /reports/weekly?date=yyyy-MM-dd`: 주간 리포트 생성 후 반환.
  - `GET /reports/daily?date=yyyy-MM-dd`: 일간 리포트 반환(없으면 204).
  - `GET /reports/weekly?date=yyyy-MM-dd`: 주간 리포트 반환(없으면 204).

## 작업 시 주의 사항
- 민감 정보: `application.yml`(BE/Batch)에 DB 비밀번호, JWT 시크릿, AI API 키가 포함되어 있을 수 있음. 커밋/공유 시 주의.
- 테스트/문서: FE 테스트는 미구성, BE는 기본 부트스트랩 테스트만 존재.
