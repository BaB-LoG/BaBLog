<template>
  <div class="fixed inset-0 bg-black/50 flex items-center justify-center px-4 z-50" @click.self="$emit('close')">
    <div class="bg-white dark:bg-card-dark w-full max-w-md rounded-xl p-6 relative" @click.stop>
      <!-- 닫기 버튼 -->
      <button class="absolute top-3 right-3 text-gray-500 hover:text-primary" @click="$emit('close')">
        <span class="material-symbols-outlined text-3xl pr-4">x</span>
      </button>

      <h2 class="text-lg font-bold mb-4 text-gray-900 dark:text-white">회원가입</h2>

      <form @submit.prevent="handleSignUp" class="space-y-4">
        <div>
          <label class="block text-sm font-medium mb-1">이메일</label>
          <input type="email" v-model="form.email" required
                 class="w-full rounded border-gray-300 dark:border-gray-600 dark:bg-gray-800" />
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">비밀번호 (8자 이상)</label>
          <input type="password" v-model="form.password" required minlength="8"
                 class="w-full rounded border-gray-300 dark:border-gray-600 dark:bg-gray-800" />
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">이름</label>
          <input type="text" v-model="form.name" required
                 class="w-full rounded border-gray-300 dark:border-gray-600 dark:bg-gray-800" />
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">성별</label>
          <select v-model="form.gender" required
                  class="w-full rounded border-gray-300 dark:border-gray-600 dark:bg-gray-800">
            <option value="" disabled selected>선택...</option>
            <option value="MALE">남성</option>
            <option value="FEMALE">여성</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">생년월일</label>
          <input type="date" v-model="form.birthDate"
                 class="w-full rounded border-gray-300 dark:border-gray-600 dark:bg-gray-800" />
        </div>

        <div class="flex gap-4">
          <div class="w-1/2">
            <label class="block text-sm font-medium mb-1">신장(cm)</label>
            <input type="number" step="0.1" v-model.number="form.heightCm"
                   class="w-full rounded border-gray-300 dark:border-gray-600 dark:bg-gray-800" />
          </div>
          <div class="w-1/2">
            <label class="block text-sm font-medium mb-1">체중(kg)</label>
            <input type="number" step="0.1" v-model.number="form.weightKg"
                   class="w-full rounded border-gray-300 dark:border-gray-600 dark:bg-gray-800" />
          </div>
        </div>

        <button type="submit" class="w-full py-2 px-4 bg-primary text-white rounded-lg font-bold hover:opacity-90">
          가입하기
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import { signup, login } from '@/services/memberService';

const emit = defineEmits(['close']);
const router = useRouter();
const userStore = useUserStore();

const form = reactive({
  email: '',
  password: '',
  name: '',
  gender: '',
  birthDate: '',
  heightCm: null,
  weightKg: null,
});

const handleSignUp = async () => {
  try {
    // 1. 회원가입 요청 (필수: email, password, name, gender)
    await signup(form);

    // 2. 자동 로그인 (회원가입 후 바로 로그인)
    const loginRes = await login({
      email: form.email,
      password: form.password,
    });

    const { tokenType = 'Bearer', accessToken, member } = loginRes.data;
    userStore.saveUser({ tokenType, accessToken, member });

    router.push('/dashboard');
    emit('close');
  } catch (error) {
    console.error('회원가입 오류:', error);
    alert('회원가입에 실패했습니다. 입력 내용을 확인해주세요.');
  }
};
</script>
