import apiClient from './apiClient';

export const getMealsByDate = (date) => apiClient.get('/meals', { params: { date } });

export const getDailyMealSummary = (date) => apiClient.get('/meals/summary', { params: { date } });

export const addMealFood = (payload) => apiClient.post('/meals/foods', payload);

export const deleteMealFood = (mealFoodId) => apiClient.delete(`/meals/foods/${mealFoodId}`);

export const updateMealFood = (mealFoodId, payload) => apiClient.patch(`/meals/foods/${mealFoodId}`, payload);
