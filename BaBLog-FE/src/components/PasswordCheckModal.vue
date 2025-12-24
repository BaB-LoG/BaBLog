<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4" @click.self="emitClose">
    <div class="relative w-full max-w-sm rounded-xl bg-card-light p-6 shadow-xl dark:bg-card-dark">
      <button class="absolute right-3 top-3 text-text-secondary-light hover:text-primary dark:text-text-secondary-dark" @click="emitClose">
        <span class="material-symbols-outlined text-2xl">close</span>
      </button>
      <h2 class="mb-4 text-lg font-bold text-text-light dark:text-text-dark">비밀번호 확인</h2>
      <form class="space-y-4" @submit.prevent="handleSubmit">
        <div class="text-left">
          <label class="mb-1 block text-sm font-medium text-text-light dark:text-text-dark" for="password">비밀번호</label>
          <input
            id="password"
            v-model="password"
            type="password"
            required
            class="w-full rounded-lg border border-border-light bg-background-light px-3 py-2 text-text-light focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
            placeholder="비밀번호를 입력하세요"
          />
        </div>
        <button
          type="submit"
          class="flex w-full items-center justify-center rounded-lg bg-primary px-4 py-2 font-bold text-white transition hover:opacity-90 disabled:opacity-60"
          :disabled="loading"
        >
          {{ loading ? '확인 중...' : '확인' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { passwordCheck } from '@/services/memberService';

const emit = defineEmits(['success', 'close']);
const password = ref('');
const loading = ref(false);

const emitClose = () => emit('close');

const handleSubmit = async () => {
  if (loading.value) return;
  loading.value = true;
  try {
    const res = await passwordCheck({ password: password.value });
    if (res.data === true) {
      emit('success');
    } else {
      alert('비밀번호가 일치하지 않습니다.');
    }
  } catch (error) {
    console.error('비밀번호 확인 실패', error);
    alert('비밀번호 확인 중 오류가 발생했습니다.');
  } finally {
    loading.value = false;
    password.value = '';
  }
};
</script>
