import { createApp } from 'vue';
import router from './router';
import App from './App.vue';
import './style.css';
import pinia from './plugins/pinia';

const app = createApp(App);
app.use(pinia);
// 새로고침 후에도 인증 정보를 복원합니다.
import { useUserStore } from '@/stores/userStore';
useUserStore(pinia).hydrate();
app.use(router);
app.mount('#app');
