// src/stores/userStore.js
import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    tokenType: 'Bearer',
    accessToken: null,
    member: null,
    isAuthenticated: false,
  }),
  getters: {
    authHeader: (state) =>
      state.accessToken ? `${state.tokenType || 'Bearer'} ${state.accessToken}` : null,
  },
  actions: {
    saveUser({ tokenType = 'Bearer', accessToken, member }) {
      this.tokenType = tokenType || 'Bearer';
      this.accessToken = accessToken;
      this.member = member;
      this.isAuthenticated = Boolean(accessToken);
      // 새로고침 후에도 인증 상태를 유지하기 위해 로컬 스토리지에 저장
      localStorage.setItem(
        'bablog_auth',
        JSON.stringify({
          tokenType: this.tokenType,
          accessToken: this.accessToken,
          member: this.member,
        }),
      );
    },
    hydrate() {
      const raw = localStorage.getItem('bablog_auth');
      if (!raw) return;
      try {
        const parsed = JSON.parse(raw);
        this.tokenType = parsed.tokenType || 'Bearer';
        this.accessToken = parsed.accessToken || null;
        this.member = parsed.member || null;
        this.isAuthenticated = Boolean(this.accessToken);
      } catch (error) {
        console.error('Failed to hydrate auth state', error);
        localStorage.removeItem('bablog_auth');
      }
    },
    logout() {
      this.tokenType = 'Bearer';
      this.accessToken = null;
      this.member = null;
      this.isAuthenticated = false;
      localStorage.removeItem('bablog_auth');
    },
  },
});
