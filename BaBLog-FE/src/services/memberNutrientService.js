import apiClient from './apiClient';

// 특정 날짜의 영양 섭취 목표 조회
export const getDailyTargets = (date) => apiClient.get('/members/nutrients/daily', { params: { date } });
