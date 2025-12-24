import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import pinia from '@/plugins/pinia';
import PublicLayout from '@/layouts/PublicLayout.vue';
import DashboardLayout from '@/layouts/DashboardLayout.vue';
import HomePage from '@/views/HomePage.vue';
import DashboardPage from '@/views/DashboardPage.vue';
import MealLogPage from '@/views/MealLogPage.vue';
import ReportsPage from '@/views/ReportsPage.vue';
import GoalsPage from '@/views/GoalsPage.vue';
import MyPage from '@/views/MyPage.vue';

const routes = [
  {
    path: '/',
    component: PublicLayout,
    children: [
      {
        path: '',
        name: 'home',
        component: HomePage,
        meta: { guestOnly: true },
      },
    ],
  },
  {
    path: '/dashboard',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'dashboard',
        component: DashboardPage,
      },
    ],
  },
  {
    path: '/meal-log',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'meal-log',
        component: MealLogPage,
      },
    ],
  },
  {
    path: '/reports',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'reports',
        component: ReportsPage,
      },
    ],
  },
  {
    path: '/goals',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'goals',
        component: GoalsPage,
      },
    ],
  },
  {
    path: '/mypage',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'mypage',
        component: MyPage,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 인증되지 않은 사용자는 보호된 페이지 접근 시 홈으로, 인증된 사용자는 게스트 페이지 진입 시 대시보드로 이동
router.beforeEach((to, from, next) => {
  const userStore = useUserStore(pinia);
  const isGuestOnly = to.matched.some((record) => record.meta.guestOnly);
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);

  if (isGuestOnly && userStore.isAuthenticated) {
    next('/dashboard');
    return;
  }

  if (requiresAuth && !userStore.isAuthenticated) {
    window.alert('로그인이 필요합니다.');
    next('/');
    return;
  }

  next();
});

export default router;
