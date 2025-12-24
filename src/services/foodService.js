import apiClient from './apiClient';

// name 또는 vendor 중 최소 1개 필요
export const searchFoods = (params) => apiClient.get('/foods/search', { params });
