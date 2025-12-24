<template>
  <div class="flex flex-col gap-4 rounded-lg bg-background-light p-4 dark:bg-background-dark">
    <h4 class="text-sm font-semibold text-text-light dark:text-text-dark">{{ title }}</h4>
    <div class="grid grid-cols-2 gap-2 text-xs">
      <div
        v-for="item in items"
        :key="item.label"
        class="flex flex-col items-center rounded-md bg-card-light p-2 dark:bg-card-dark"
      >
        <div class="flex flex-col items-center gap-1">
          <span class="text-text-secondary-light dark:text-text-secondary-dark text-xs whitespace-nowrap">{{ item.label }}</span>
          <span
            class="font-bold"
            :class="item.colorClass || 'text-text-light dark:text-text-dark'"
          >
            {{ formatValue(item.value) }} {{ item.unit }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  title: {
    type: String,
    default: '영양 상세',
  },
  stats: {
    type: Object,
    default: () => ({}),
  },
});

// 기본 표시 순서와 레이블 정의
const items = computed(() => {
  const s = props.stats || {};
  return [
    { label: '칼로리', value: s.kcal, unit: 'kcal' },
    { label: '탄수화물', value: s.carbohydrates, unit: 'g', colorClass: 'text-amber-500' },
    { label: '단백질', value: s.protein, unit: 'g', colorClass: 'text-red-500' },
    { label: '지방', value: s.fat, unit: 'g', colorClass: 'text-blue-500' },
    { label: '포화지방', value: s.saturatedFat, unit: 'g' },
    { label: '트랜스지방', value: s.transFat, unit: 'g' },
    { label: '당류', value: s.sugar, unit: 'g' },
    { label: '나트륨', value: s.natrium, unit: 'mg' },
    { label: '콜레스테롤', value: s.cholesterol, unit: 'mg' },
  ];
});

const formatValue = (val) => {
  if (val === null || val === undefined) return '0';
  return typeof val === 'number' ? val : val;
};
</script>
