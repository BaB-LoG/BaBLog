<template>
  <div class="flex flex-col gap-4 rounded-xl border border-border-light bg-card-light p-6 shadow-sm dark:border-border-dark dark:bg-card-dark">
    <div class="flex items-center justify-between">
      <h3 class="text-xl font-bold text-text-light dark:text-text-dark whitespace-nowrap">{{ title }}</h3>
    </div>
    <div class="relative">
      <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary-light dark:text-text-secondary-dark">
        search
      </span>
      <input
        class="h-12 w-full rounded-lg border border-border-light bg-background-light pl-10 pr-4 text-text-light shadow-inner focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
        :placeholder="placeholder"
        type="text"
      />
    </div>

    <template v-if="items && items.length">
      <ul class="flex flex-col gap-3">
        <li
          v-for="item in items"
          :key="item.name"
          class="flex items-center justify-between rounded-lg bg-background-light px-3 py-3 dark:bg-background-dark"
        >
          <div>
            <p class="font-medium text-text-light dark:text-text-dark whitespace-nowrap">{{ item.name }}</p>
            <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark">{{ item.detail }}</p>
          </div>
          <div class="flex gap-2">
            <button class="text-text-secondary-light transition-colors hover:text-primary dark:text-text-secondary-dark dark:hover:text-primary">
              <span class="material-symbols-outlined">edit</span>
            </button>
            <button class="text-text-secondary-light transition-colors hover:text-red-500 dark:text-text-secondary-dark">
              <span class="material-symbols-outlined">delete</span>
            </button>
          </div>
        </li>
      </ul>
      <div class="mt-3 border-t border-border-light pt-3 text-right text-text-light dark:border-border-dark dark:text-text-dark">
        <p class="font-bold">
          합계: <span class="text-primary">{{ totalKcal }}</span> kcal
        </p>
      </div>
    </template>
    <template v-else>
      <div class="py-6 text-center text-text-secondary-light dark:text-text-secondary-dark">
        {{ emptyMessage }}
      </div>
    </template>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  items: { type: Array, default: () => [] },
  totalKcal: { type: Number, default: 0 },
  emptyMessage: { type: String, default: '기록이 없습니다.' },
  placeholder: { type: String, default: '음식 이름을 검색하여 추가하세요' },
});
</script>
