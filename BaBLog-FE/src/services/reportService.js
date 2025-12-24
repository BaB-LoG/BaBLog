import apiClient from './apiClient';

export const getDailyReport = (date) => apiClient.get('/reports/daily', { params: { date } });

export const createDailyReport = (date) => apiClient.post('/reports/daily', null, { params: { date } });

export const getWeeklyReport = (date) => apiClient.get('/reports/weekly', { params: { date } });
