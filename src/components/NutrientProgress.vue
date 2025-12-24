<template>
  <div class="flex flex-col gap-2">
    <div class="flex items-baseline justify-between gap-4">
      <p class="text-sm font-medium text-text-light dark:text-text-dark">{{ label }}</p>
      <p class="text-xs font-normal text-text-secondary-light dark:text-text-secondary-dark">
        {{ current }} / {{ target }} {{ unit }}
      </p>
    </div>
    <div class="h-2.5 rounded-full bg-border-light dark:bg-border-dark">
      <div
        class="h-2.5 rounded-full"
        :class="colorClass"
        :style="{ width: progressWidth }"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  label: { type: String, required: true },
  current: { type: [Number, String], required: true },
  target: { type: [Number, String], required: true },
  unit: { type: String, default: '' },
  colorClass: { type: String, default: 'bg-primary' },
});

// 진행률은 0~100% 사이로 제한합니다.
const progressWidth = computed(() => {
  const curr = Number(props.current) || 0;
  const tgt = Number(props.target) || 0;
  if (tgt <= 0) return '0%';
  const ratio = Math.min(100, Math.max(0, (curr / tgt) * 100));
  return `${ratio}%`;
});
</script>
