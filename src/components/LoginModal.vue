<template>
  <div class="fixed inset-0 bg-black/50 flex items-center justify-center px-4 z-50" @click.self="$emit('close')">
    <div class="bg-white dark:bg-card-dark w-full max-w-sm rounded-xl p-6 relative" @click.stop>
      <!-- 닫기 버튼 -->
      <button class="absolute top-3 right-3 text-gray-500 hover:text-primary" @click="$emit('close')">
        <span class="material-symbols-outlined text-3xl pr-4">x</span>
      </button>

      <h2 class="text-lg font-bold mb-4 text-gray-900 dark:text-white">로그인</h2>

      <form @submit.prevent="handleLogin">
        <div class="mb-4 text-left">
          <label class="block text-sm font-medium mb-1" for="email">이메일</label>
          <input
              id="email"
              v-model="email"
              type="email"
              required
              class="w-full rounded border-gray-300 dark:border-gray-600 dark:bg-gray-800"
          />
        </div>

        <div class="mb-6 text-left">
          <label class="block text-sm font-medium mb-1" for="password">비밀번호</label>
          <input
              id="password"
              v-model="password"
              type="password"
              required
              class="w-full rounded border-gray-300 dark:border-gray-600 dark:bg-gray-800"
          />
        </div>

        <button
            type="submit"
            class="w-full py-2 px-4 bg-primary text-white rounded-lg font-bold hover:opacity-90 transition-opacity"
        >
          로그인
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import { login } from '@/services/memberService';

const email = ref('');
const password = ref('');
const userStore = useUserStore();
const router = useRouter();

const handleLogin = async () => {
  try {
    const response = await login({
      email: email.value,
      password: password.value,
    });

    // 예: 응답에서 token과 user 정보가 함께 온다고 가정
    const { tokenType = 'Bearer', accessToken, member } = response.data;

    userStore.saveUser({ tokenType, accessToken, member });

    router.push('/dashboard');
    emitClose();
  } catch (err) {
    console.error(err);
    alert('로그인 실패: 이메일 또는 비밀번호를 확인하세요.');
  }
};

const emit = defineEmits(['close']);
const emitClose = () => emit('close');
</script>
