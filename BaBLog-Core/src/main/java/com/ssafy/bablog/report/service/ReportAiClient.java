package com.ssafy.bablog.report.service;

import java.util.Map;

public interface ReportAiClient {
    <T> T generateDailyInsight(Map<String, Object> payload, Class<T> resultType);

    <T> T generateWeeklyInsight(Map<String, Object> payload, Class<T> resultType);
}
