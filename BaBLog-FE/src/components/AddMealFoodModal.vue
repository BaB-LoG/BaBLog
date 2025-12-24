<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4" @click.self="emitClose">
    <div class="flex w-full max-w-5xl flex-col overflow-hidden rounded-xl border border-border-light bg-card-light shadow-2xl dark:border-border-dark dark:bg-card-dark">
      <div class="flex items-center justify-between border-b border-border-light px-6 py-4 dark:border-border-dark">
        <h2 class="text-xl font-bold text-text-light dark:text-text-dark">
          음식 기록 <span class="text-sm font-semibold text-primary">({{ mealLabel }})</span>
        </h2>
        <button class="text-text-secondary-light transition hover:text-primary dark:text-text-secondary-dark" @click="emitClose">
          <span class="material-symbols-outlined text-2xl">close</span>
        </button>
      </div>

      <div class="grid max-h-[75vh] grid-cols-[1.1fr_0.9fr] divide-x divide-border-light overflow-hidden dark:divide-border-dark">
        <div class="space-y-5 overflow-y-auto px-6 py-5">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-2">
              <label class="text-sm font-medium text-text-light dark:text-text-dark" for="food-name">음식 이름</label>
              <div class="relative">
                <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary-light dark:text-text-secondary-dark">search</span>
                <input
                  id="food-name"
                  v-model="form.foodName"
                  type="text"
                  placeholder="예: 닭가슴살"
                  class="h-11 w-full rounded-lg border border-border-light bg-background-light pl-10 pr-4 text-text-light focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                />
              </div>
            </div>
            <div class="space-y-2">
              <label class="text-sm font-medium text-text-light dark:text-text-dark" for="food-vendor">업체명</label>
              <input
                id="food-vendor"
                v-model="form.vendor"
                type="text"
                placeholder="예: 브랜드명"
                class="h-11 w-full rounded-lg border border-border-light bg-background-light px-4 text-text-light focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
              />
            </div>
          </div>
          <div class="flex justify-end">
            <button
              type="button"
              class="h-11 rounded-lg bg-primary px-4 text-sm font-bold text-white transition hover:opacity-90 disabled:opacity-60"
              :disabled="!canSearch || loadingSearch"
              @click="handleSearch"
            >
              {{ loadingSearch ? '검색 중...' : '검색' }}
            </button>
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-text-light dark:text-text-dark" for="food-amount">섭취량 (g)</label>
            <div class="flex gap-3">
              <input
                id="food-amount"
                v-model.number="form.quantity"
                type="number"
                min="0"
                step="0.1"
                placeholder="예: 150 (그램)"
                class="h-11 w-full rounded-lg border border-border-light bg-background-light px-4 text-text-light focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
              />
              <div class="flex h-11 items-center rounded-lg border border-border-light bg-background-light px-4 text-sm font-semibold text-text-light dark:border-border-dark dark:bg-background-dark dark:text-text-dark">
                g
              </div>
            </div>
            <p v-if="selectedFood" class="text-xs text-text-secondary-light dark:text-text-secondary-dark">
              1회 제공량: {{ selectedFood.foodWeight || selectedFood.standard }} g (기준 {{ selectedFood.standard }} g 영양 정보)
            </p>
          </div>

        <div class="space-y-3">
          <button
            type="button"
            class="flex h-11 w-full items-center justify-center rounded-lg bg-background-light text-sm font-bold text-text-light transition hover:bg-border-light dark:bg-background-dark dark:text-text-dark dark:hover:bg-border-dark disabled:opacity-60"
            :disabled="!selectedFood || !form.quantity"
            @click="calculateNutrition"
          >
            칼로리 계산하기
          </button>

          <div class="rounded-lg bg-background-light px-4 py-3 text-sm text-text-secondary-light dark:bg-background-dark dark:text-text-secondary-dark">
            <div v-if="calculatedNutrition">
              <p class="mb-3 text-center font-semibold text-text-light dark:text-text-dark">예상 섭취 영양</p>
              <div class="grid grid-cols-3 gap-3 text-xs">
                <div v-for="nut in nutritionList" :key="nut.key" class="flex flex-col rounded-md bg-card-light px-3 py-2 text-center dark:bg-card-dark">
                  <span class="text-text-secondary-light dark:text-text-secondary-dark whitespace-nowrap">{{ nut.label }}</span>
                  <span class="text-sm font-bold text-text-light dark:text-text-dark whitespace-nowrap">
                    {{ nut.value }} {{ nut.unit }}
                  </span>
                </div>
              </div>
            </div>
            <p v-else class="text-center">계산된 영양 정보가 여기에 표시됩니다.</p>
          </div>
        </div>
        </div>

        <div class="flex h-full flex-col">
          <div class="flex items-center justify-between px-6 py-4">
            <h3 class="text-sm font-bold text-text-light dark:text-text-dark">검색 결과</h3>
          </div>
          <div class="flex-1 overflow-y-auto border-t border-border-light px-6 pb-4 dark:border-border-dark max-h-[50vh]">
            <div
              v-if="loadingSearch"
              class="mt-3 rounded-lg bg-background-light px-4 py-3 text-sm text-text-secondary-light dark:bg-background-dark dark:text-text-secondary-dark"
            >
              검색 중입니다...
            </div>
            <div
              v-else-if="!searchResults.length"
              class="mt-3 rounded-lg bg-background-light px-4 py-3 text-sm text-text-secondary-light dark:bg-background-dark dark:text-text-secondary-dark"
            >
              검색 결과가 없습니다.
            </div>
            <ul v-else class="mt-3 space-y-3">
              <li
                v-for="food in searchResults"
                :key="food.id"
                class="cursor-pointer rounded-lg border border-border-light bg-card-light px-4 py-3 transition hover:border-primary hover:shadow-sm dark:border-border-dark dark:bg-card-dark"
                @click="selectFood(food)"
              >
                <div class="flex items-center justify-between">
                  <p class="font-semibold text-text-light dark:text-text-dark">{{ food.name }}</p>
                  <span class="text-xs text-text-secondary-light dark:text-text-secondary-dark">{{ food.vendor }}</span>
                </div>
                <p class="text-xs text-text-secondary-light dark:text-text-secondary-dark">
                  1회 제공량 {{ food.foodWeight || food.standard }} g / 기준 {{ food.standard }} g
                </p>
                <p class="text-xs text-text-secondary-light dark:text-text-secondary-dark">칼로리 {{ food.kcal }} kcal</p>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div class="flex justify-end gap-3 border-t border-border-light px-6 py-4 dark:border-border-dark">
        <button
          type="button"
          class="h-11 rounded-lg border border-border-light px-6 text-sm font-bold text-text-light transition hover:bg-border-light/60 dark:border-border-dark dark:text-text-dark"
          @click="emitClose"
        >
          취소
        </button>
        <button
          type="button"
          class="h-11 rounded-lg bg-primary px-6 text-sm font-bold text-white transition hover:opacity-90 disabled:opacity-60"
          :disabled="!canSubmit"
          @click="handleSubmit"
        >
          {{ submitLabel }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue';
import { searchFoods } from '@/services/foodService';

const props = defineProps({
  mealLabel: {
    type: String,
    default: '식사',
  },
  mode: {
    type: String,
    default: 'add',
  },
  initialFood: {
    type: Object,
    default: null,
  },
  initialQuantity: {
    type: [Number, String],
    default: '',
  },
});

const emit = defineEmits(['close', 'submit']);

const form = reactive({
  foodName: '',
  vendor: '',
  quantity: '',
});

const searchResults = ref([]);
const selectedFood = ref(null);
const calculatedNutrition = ref(null);
const loadingSearch = ref(false);

const emitClose = () => emit('close');

const canSearch = computed(() => Boolean(form.foodName || form.vendor));
const canSubmit = computed(() => selectedFood.value && form.quantity);

const selectFood = (food) => {
  selectedFood.value = food;
  form.foodName = food.name || '';
  form.vendor = food.vendor || '';
  calculatedNutrition.value = null;
};

const handleSearch = async () => {
  if (!canSearch.value) {
    alert('음식 이름 또는 업체명을 입력해주세요.');
    return;
  }
  loadingSearch.value = true;
  try {
    const res = await searchFoods({
      name: form.foodName || undefined,
      vendor: form.vendor || undefined,
    });
    searchResults.value = res.data || [];
    if (searchResults.value.length === 0) {
      selectedFood.value = null;
      calculatedNutrition.value = null;
    }
  } catch (error) {
    console.error('음식 검색 실패', error);
    alert('음식 검색에 실패했습니다.');
  } finally {
    loadingSearch.value = false;
  }
};

const calculateNutrition = () => {
  if (!selectedFood.value || !form.quantity) return;
  const base = selectedFood.value;
  const ratio = (Number(form.quantity) || 0) / (base.standard || 100);
  const round1 = (val) => Math.round(val * 10) / 10;
  calculatedNutrition.value = {
    kcal: round1((base.kcal || 0) * ratio),
    protein: round1((base.protein || 0) * ratio),
    fat: round1((base.fat || 0) * ratio),
    saturatedFat: round1((base.saturatedFat || 0) * ratio),
    transFat: round1((base.transFat || 0) * ratio),
    carbohydrates: round1((base.carbohydrates || 0) * ratio),
    sugar: round1((base.sugar || 0) * ratio),
    natrium: round1((base.natrium || 0) * ratio),
    cholesterol: round1((base.cholesterol || 0) * ratio),
  };
};

const resetForm = () => {
  form.foodName = '';
  form.vendor = '';
  form.quantity = '';
  selectedFood.value = null;
  calculatedNutrition.value = null;
  searchResults.value = [];
};

const setInitialState = () => {
  if (!props.initialFood) {
    if (props.mode === 'add') {
      resetForm();
    }
    return;
  }
  selectedFood.value = props.initialFood;
  form.foodName = props.initialFood.name || '';
  form.vendor = props.initialFood.vendor || '';
  form.quantity = props.initialQuantity ?? '';
  if (form.quantity) {
    calculateNutrition();
  } else {
    calculatedNutrition.value = null;
  }
};

const handleSubmit = () => {
  if (!selectedFood.value || !form.quantity) {
    alert('음식과 섭취량을 입력해주세요.');
    return;
  }
  emit('submit', {
    foodId: selectedFood.value.id,
    foodName: selectedFood.value.name,
    vendor: selectedFood.value.vendor,
    intake: Number(form.quantity),
    unit: 'g',
    nutrition: calculatedNutrition.value,
  });
  emitClose();
  resetForm();
};

const submitLabel = computed(() => (props.mode === 'edit' ? '수정' : '추가'));

const nutritionList = computed(() => {
  if (!calculatedNutrition.value) return [];
  const n = calculatedNutrition.value;
  return [
    { key: 'kcal', label: '칼로리', value: n.kcal, unit: 'kcal' },
    { key: 'carbohydrates', label: '탄수화물', value: n.carbohydrates, unit: 'g' },
    { key: 'protein', label: '단백질', value: n.protein, unit: 'g' },
    { key: 'fat', label: '지방', value: n.fat, unit: 'g' },
    { key: 'saturatedFat', label: '포화지방', value: n.saturatedFat, unit: 'g' },
    { key: 'transFat', label: '트랜스지방', value: n.transFat, unit: 'g' },
    { key: 'sugar', label: '당류', value: n.sugar, unit: 'g' },
    { key: 'natrium', label: '나트륨', value: n.natrium, unit: 'mg' },
    { key: 'cholesterol', label: '콜레스테롤', value: n.cholesterol, unit: 'mg' },
  ];
});

setInitialState();

watch(
  () => [props.initialFood, props.initialQuantity, props.mode],
  () => {
    setInitialState();
  },
);
</script>
