import axios from 'axios';
import { useUserStore } from '@/stores/userStore';
import pinia from '@/plugins/pinia';

// 공통 axios 인스턴스: 베이스 URL 및 인증 헤더 자동 주입
const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
});

apiClient.interceptors.request.use((config) => {
  const userStore = useUserStore(pinia);
  if (userStore.isAuthenticated && userStore.authHeader) {
    config.headers.Authorization = userStore.authHeader;
  }
  return config;
});

export default apiClient;
