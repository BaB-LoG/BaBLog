# BaBLog Architecture

이 문서는 BaBLog 멀티 모듈 구조와 코드 배치 규칙을 처음 보는 사람이 이해할 수 있도록 설명합니다.

## 모듈 구성
- `BaBLog-Core`
  - 도메인/서비스/리포지토리(MyBatis) 및 공통 유틸을 포함합니다.
  - 웹/API, 보안, 스케줄링은 포함하지 않습니다.
- `BaBLog-BE`
  - REST 컨트롤러, 보안(Spring Security/JWT), Swagger 등 웹 전용 구성만 포함합니다.
  - Core의 서비스/도메인을 주입받아 API 응답을 조립합니다.
- `BaBLog-Batch`
  - 배치 잡/스케줄러만 포함합니다.
  - Core의 서비스/도메인을 재사용하여 배치 로직을 수행합니다.

## 의존 방향
- `BaBLog-BE` → `BaBLog-Core`
- `BaBLog-Batch` → `BaBLog-Core`
- `BaBLog-Core`는 `BE/Batch`를 참조하지 않습니다.

## 패키지 배치 규칙
### Core
- `com.ssafy.bablog.<domain>.domain`
  - 순수 도메인 모델
- `com.ssafy.bablog.<domain>.repository` + `repository.mapper`
  - MyBatis Repository/Mapper
- `com.ssafy.bablog.<domain>.service`
  - 비즈니스 로직
- `com.ssafy.bablog.<domain>.service.dto`
  - 서비스 전용 커맨드/뷰 모델
  - API 요청/응답 DTO와 분리됨
- `com.ssafy.bablog.config`
  - Core에서 공통으로 필요한 설정(예: AI 클라이언트 설정)

### BE
- `com.ssafy.bablog.<domain>.controller`
  - API 엔드포인트
- `com.ssafy.bablog.<domain>.dto`
  - API 요청/응답 DTO
- `com.ssafy.bablog.security`, `com.ssafy.bablog.config`
  - 인증/인가 및 Swagger 등 웹 전용 구성

### Batch
- `com.ssafy.bablog.batch.*`
  - 배치 잡/스텝/스케줄러/실패 로그

## DTO 분리 기준
- **API DTO**: `BaBLog-BE`에만 위치
- **서비스 DTO**: `BaBLog-Core`의 `service.dto`에 위치
- Core는 API DTO를 참조하지 않음

## 주요 흐름
### API 요청 흐름
1) Controller에서 API DTO 수신
2) API DTO → Core 서비스 DTO로 변환
3) Core 서비스 실행
4) Core 결과 → API 응답 DTO로 매핑 후 반환

### 배치 흐름
1) Batch 스케줄러가 Job 실행
2) Job/Tasklet에서 Core 서비스 호출
3) 실패는 `batch_failure_log`에 기록

## 마이그레이션 대상 스케줄
- 일일 기본 식단 생성
- 일간 리포트 생성
- 주간 리포트 생성

스케줄 시간은 Batch 내부에서 관리합니다.

## 코드 추가 가이드
- 비즈니스 로직 추가: Core의 `service`에 작성
- API 스펙 변경/추가: BE의 `controller` + `dto`에 작성
- 배치 작업 추가: Batch의 `batch` 패키지에 작성
- DB 쿼리 추가: Core의 `repository`/`mapper`에 작성

## 참고
- MyBatis 매퍼 XML: `BaBLog-Core/src/main/resources/mapper`
- 배치 실패 로그 테이블: `batch_failure_log`
