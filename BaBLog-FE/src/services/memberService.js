import apiClient from './apiClient';

export const signup = (payload) => apiClient.post('/members/signup', payload);

export const login = (payload) => apiClient.post('/members/login', payload);

// 비밀번호 확인: true/false 반환
export const passwordCheck = (payload) => apiClient.post('/members/password-check', payload);

// 비밀번호 변경
export const passwordChange = (payload) => apiClient.post('/members/password-change', payload);

// 회원 정보 수정 (프로필 업데이트)
export const updateMember = (payload) => apiClient.patch('/members/info', payload);

// 권장 섭취량 조회
export const getMemberNutrients = () => apiClient.get('/members/nutrients');

// 신장/체중 기반 권장 섭취량 재계산
export const recalculateMemberNutrients = () => apiClient.post('/members/nutrients/recalculate');

// 권장 섭취량 수동 수정
export const updateMemberNutrients = (payload) => apiClient.patch('/members/nutrients', payload);
