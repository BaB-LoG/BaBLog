<template>
  <div class="flex flex-col rounded-xl border border-border-light bg-card-light p-6 shadow-sm dark:border-border-dark dark:bg-card-dark">
    <h3 class="mb-4 text-xl font-bold text-text-light dark:text-text-dark">{{ title }}</h3>
    <div class="flex flex-col gap-4">
      <div class="relative">
        <span class="material-symbols-outlined pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary-light dark:text-text-secondary-dark">search</span>
        <input
          class="h-12 w-full rounded-lg border border-border-light bg-background-light pl-10 pr-4 text-text-light focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
          :placeholder="placeholder"
          type="text"
          readonly
          @click="emitOpenAdd"
        />
      </div>

      <div class="grid grid-cols-[1.5fr_1fr] gap-6">
        <div class="flex min-h-[220px] flex-col overflow-hidden rounded-lg border border-border-light bg-background-light dark:border-border-dark dark:bg-background-dark">
          <template v-if="items && items.length">
            <ul class="flex flex-col gap-3 overflow-y-auto px-3 py-3">
              <li
                v-for="item in items"
                :key="item.mealFoodId || item.name"
                class="flex items-center justify-between rounded-lg bg-card-light px-3 py-3 shadow-sm dark:bg-card-dark"
              >
                <div>
                  <p class="font-medium text-text-light dark:text-text-dark">{{ item.name }}</p>
                  <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark">{{ item.detail }}</p>
                </div>
                <div class="flex gap-2">
                  <button
                    class="text-text-secondary-light transition hover:text-primary dark:text-text-secondary-dark dark:hover:text-primary"
                    @click="emitOpenEdit(item)"
                  >
                    <span class="material-symbols-outlined">edit</span>
                  </button>
                  <button
                    class="text-text-secondary-light transition hover:text-red-500 dark:text-text-secondary-dark"
                    @click="emitDelete(item)"
                  >
                    <span class="material-symbols-outlined">delete</span>
                  </button>
                </div>
              </li>
            </ul>
          </template>
          <div v-else class="flex flex-1 items-center justify-center px-3 py-6 text-center text-text-secondary-light dark:text-text-secondary-dark">
            {{ emptyMessage }}
          </div>
        </div>

        <div class="min-h-[220px]">
          <MealNutritionDetail :title="`${title} 영양 상세`" :stats="stats" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import MealNutritionDetail from './MealNutritionDetail.vue';

const props = defineProps({
  title: { type: String, required: true },
  items: { type: Array, default: () => [] },
  stats: { type: Object, default: () => ({}) },
  placeholder: { type: String, default: '음식 이름을 검색하여 추가하세요' },
  emptyMessage: { type: String, default: '기록이 없습니다.' },
});

const emit = defineEmits(['open-add', 'open-edit', 'delete-item']);
const emitOpenAdd = () => emit('open-add', props.title);
const emitOpenEdit = (item) => emit('open-edit', { label: props.title, item });
const emitDelete = (item) => emit('delete-item', { label: props.title, item });
</script>
