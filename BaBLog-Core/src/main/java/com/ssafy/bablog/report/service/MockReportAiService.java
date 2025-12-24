package com.ssafy.bablog.report.service;

import com.ssafy.bablog.report.service.dto.AiDailyReportResult;
import com.ssafy.bablog.report.service.dto.AiWeeklyReportResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Profile("test")
public class MockReportAiService implements ReportAiClient {

    @Override
    public <T> T generateDailyInsight(Map<String, Object> payload, Class<T> resultType) {
        simulateLatency();
        AiDailyReportResult result = new AiDailyReportResult();
        result.setScore(75);
        result.setGrade("보통");
        result.setSummary("테스트용 일간 리포트입니다. 실제 AI 호출 없이 생성되었습니다.");
        result.setHighlights(List.of("탄단지 균형이 비교적 안정적입니다."));
        result.setImprovements(List.of("나트륨 섭취를 조금 줄이면 더 좋습니다."));
        result.setRecommendations(List.of("내일 점심에 채소 2종 추가하기"));
        result.setRiskFlags(List.of("테스트 데이터"));
        result.setNutrientScores(Map.of(
                "kcal", 18,
                "macroBalance", 15,
                "protein", 8,
                "sugar", 6,
                "natrium", 6
        ));
        return resultType.cast(result);
    }

    @Override
    public <T> T generateWeeklyInsight(Map<String, Object> payload, Class<T> resultType) {
        simulateLatency();
        AiWeeklyReportResult result = new AiWeeklyReportResult();
        result.setScore(78);
        result.setGrade("보통");
        result.setConsistencyScore(70);
        result.setSummary("테스트용 주간 리포트입니다. 실제 AI 호출 없이 생성되었습니다.");
        result.setPatternSummary("주중 규칙성이 유지되는 패턴입니다.");
        LocalDate bestDay = resolveBestDay(payload);
        LocalDate worstDay = bestDay != null ? bestDay.plusDays(2) : null;
        result.setBestDay(bestDay != null ? bestDay.toString() : null);
        result.setBestReason("균형 잡힌 식사를 유지했습니다.");
        result.setWorstDay(worstDay != null ? worstDay.toString() : null);
        result.setWorstReason("간식 비중이 높았습니다.");
        result.setNextWeekFocus("단백질 섭취를 하루 1회 이상 유지하세요.");
        result.setHighlights(List.of("기록 빈도가 꾸준합니다."));
        result.setImprovements(List.of("당류 섭취를 줄여보세요."));
        result.setRecommendations(List.of("다음 주 아침에 단백질 1회 추가하기"));
        result.setRiskFlags(List.of("테스트 데이터"));
        result.setTrend(Map.of("note", "mock"));
        return resultType.cast(result);
    }

    private LocalDate resolveBestDay(Map<String, Object> payload) {
        if (payload == null) {
            return null;
        }
        Object periodObj = payload.get("period");
        if (!(periodObj instanceof Map<?, ?> period)) {
            return null;
        }
        Object startDate = period.get("startDate");
        if (!(startDate instanceof String start)) {
            return null;
        }
        try {
            return LocalDate.parse(start);
        } catch (Exception e) {
            return null;
        }
    }

    private void simulateLatency() {
        int delayMs = 6000 + (int) (Math.random() * 2001);
        delayMs = 200;
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
