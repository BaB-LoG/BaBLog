<template>
  <header class="flex justify-center border-b border-solid border-border-light bg-card-light py-3 dark:border-border-dark dark:bg-card-dark">
    <div class="flex w-[1200px] items-center justify-between whitespace-nowrap px-4">
      <div class="flex items-center gap-4 text-text-light dark:text-text-dark shrink-0">
        <img :src="logo" alt="BaBLog 로고" width="120" class="shrink-0" />
      </div>
      <div class="flex gap-2 shrink-0">
        <button
          class="flex h-10 min-w-[100px] items-center justify-center rounded-lg bg-primary px-4 text-sm font-bold text-white transition-opacity hover:opacity-90"
          @click="$emit('open-signup')"
        >
          <span class="truncate">회원가입</span>
        </button>
        <button
          class="flex h-10 min-w-[100px] items-center justify-center rounded-lg bg-gray-200 px-4 text-sm font-bold text-text-light transition-colors hover:bg-gray-300 dark:bg-gray-700 dark:text-text-dark dark:hover:bg-gray-600"
          @click="$emit('open-login')"
        >
          <span class="truncate">로그인</span>
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import logo from '@/assets/bablog_logo.png';

defineEmits(['open-login', 'open-signup']);
</script>
