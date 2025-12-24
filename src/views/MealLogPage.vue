<template>
  <div class="px-10 py-6">
    <div class="mx-auto flex w-[1280px] flex-col gap-6">
      <header class="flex flex-row items-center justify-between gap-4">
        <div class="space-y-2">
          <p class="text-4xl font-black leading-tight tracking-[-0.033em] text-slate-900 dark:text-slate-100">
            오늘의 식단 기록
          </p>
          <p class="text-sm text-slate-500 dark:text-slate-400">하루 식사를 기록하고 영양 밸런스를 확인하세요.</p>
        </div>
        <div ref="dateControl" class="relative w-auto">
          <div class="flex h-10 items-center justify-center rounded-lg border border-slate-200 bg-white p-1 text-sm shadow-sm dark:border-slate-700 dark:bg-slate-800">
            <button
              class="flex h-full items-center justify-center px-3 text-slate-500 transition hover:text-slate-800 dark:text-slate-400 dark:hover:text-slate-200"
              :disabled="loadingMeals"
              @click="goPrevDate"
            >
              <span class="material-symbols-outlined">chevron_left</span>
            </button>
            <span class="px-3 font-medium text-slate-800 dark:text-slate-200">{{ displayDate }}</span>
            <button
              class="flex h-full items-center justify-center px-3 text-slate-500 transition hover:text-slate-800 dark:text-slate-400 dark:hover:text-slate-200"
              :disabled="loadingMeals"
              @click="goNextDate"
            >
              <span class="material-symbols-outlined">chevron_right</span>
            </button>
            <button
              class="flex h-full items-center justify-center border-l border-slate-200 px-3 text-slate-500 transition hover:text-slate-800 dark:border-slate-700 dark:text-slate-400 dark:hover:text-slate-200"
              :disabled="loadingMeals"
              @click="toggleDatePicker"
            >
              <span class="material-symbols-outlined">calendar_month</span>
            </button>
          </div>

          <div
            v-if="showDatePicker"
            class="absolute right-0 top-full z-20 mt-2 w-64 rounded-lg border border-slate-200 bg-white p-3 shadow-lg dark:border-slate-700 dark:bg-slate-800"
          >
            <label class="mb-2 block text-xs font-semibold text-slate-500 dark:text-slate-400">날짜 선택</label>
            <input
              type="date"
              class="w-full rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700 focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200"
              :value="selectedDate"
              @input="setDate($event.target.value)"
            />
            <div class="mt-3 flex justify-end gap-2">
              <button
                type="button"
                class="rounded-md border border-slate-200 bg-white px-3 py-1 text-xs font-semibold text-slate-600 transition hover:bg-slate-100 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-300"
                @click="jumpToToday"
              >
                오늘
              </button>
              <button
                type="button"
                class="rounded-md bg-primary px-3 py-1 text-xs font-bold text-white transition hover:opacity-90"
                @click="closeDatePicker"
              >
                닫기
              </button>
            </div>
          </div>
        </div>
      </header>

      <div
        v-if="mealsError"
        class="flex items-center justify-between gap-3 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 dark:border-red-800/80 dark:bg-red-900/30 dark:text-red-200"
      >
        <span>{{ mealsError }}</span>
        <button
          type="button"
          class="rounded-md border border-red-200 bg-white px-3 py-1 text-xs font-semibold text-red-700 transition hover:bg-red-100 dark:border-red-700 dark:bg-red-800/60 dark:text-red-100 dark:hover:bg-red-700/60"
          @click="fetchMeals"
        >
          다시 불러오기
        </button>
      </div>

      <section class="rounded-xl border border-slate-200 bg-white p-6 shadow-sm dark:border-slate-800 dark:bg-slate-900">
        <h2 class="pb-4 text-[22px] font-bold leading-tight tracking-[-0.015em] text-slate-900 dark:text-slate-100">오늘의 영양 요약</h2>
        <div class="grid grid-cols-3 gap-6">
          <div v-for="stat in nutrientSummaryBars" :key="stat.key" class="flex flex-col gap-3">
            <div class="flex items-baseline justify-between gap-6">
              <p class="text-base font-medium text-slate-800 dark:text-slate-200">{{ stat.label }}</p>
              <p class="text-sm text-slate-500 dark:text-slate-400">
                {{ formatNumber(stat.current) }} / {{ formatNumber(stat.target) }} {{ stat.unit }}
              </p>
            </div>
            <div class="h-2.5 rounded-full bg-slate-200 dark:bg-slate-700">
              <div class="h-2.5 rounded-full" :class="stat.colorClass" :style="{ width: `${stat.percent}%` }" />
            </div>
          </div>
        </div>
      </section>

      <section class="rounded-xl border border-slate-200 bg-white shadow-sm dark:border-slate-800 dark:bg-slate-900">
          <div class="flex flex-nowrap items-center justify-between gap-4 border-b border-slate-100 bg-gradient-to-r from-slate-50/50 to-white p-6 dark:border-slate-800/50 dark:from-slate-800/20 dark:to-slate-900">
            <div>
              <h2 class="flex items-center gap-2 text-[22px] font-bold leading-tight tracking-[-0.015em] text-slate-900 dark:text-slate-100">
                <span class="material-symbols-outlined text-primary">psychology</span>
                종합 식단 평가
              </h2>
              <p class="mt-1 text-sm text-slate-500 dark:text-slate-400 whitespace-nowrap">AI가 분석한 오늘의 식단 리포트입니다.</p>
            </div>
          <button
            class="flex items-center gap-2 rounded-lg px-5 py-2.5 text-sm font-bold text-white shadow-md shadow-primary/20 transition-all hover:bg-primary/90 disabled:cursor-not-allowed"
            :class="reportRequesting ? 'bg-primary/40 text-white/70 shadow-none' : 'bg-primary'"
            :disabled="reportRequesting"
            @click="requestDailyReport"
          >
            <span class="material-symbols-outlined text-[18px]">refresh</span>
            {{ reportRequesting ? '평가 중...' : '재평가 받기' }}
          </button>
        </div>
        <div class="p-6">
          <div v-if="reportError" class="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 dark:border-red-800/80 dark:bg-red-900/30 dark:text-red-200">
            {{ reportError }}
          </div>

          <div class="flex flex-nowrap items-start gap-8">
            <div class="flex w-[360px] shrink-0 flex-col gap-6">
              <div class="relative flex flex-col items-center justify-center rounded-2xl border border-slate-100 bg-white p-6 shadow-sm dark:border-slate-700 dark:bg-slate-800">
                <div class="absolute left-0 top-0 h-1 w-full bg-gradient-to-r from-primary/50 via-primary to-primary/50"></div>
                <h3 class="mb-4 text-sm font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400">AI-Score</h3>
                <div class="relative mb-2 size-40">
                  <svg class="size-full rotate-[135deg]" viewBox="0 0 100 100">
                    <circle class="text-slate-100 dark:text-slate-700" cx="50" cy="50" r="42" fill="transparent" stroke="currentColor" stroke-width="8" stroke-dasharray="200" stroke-linecap="round" />
                    <circle
                      :class="gradeTheme.circleClass"
                      cx="50"
                      cy="50"
                      r="42"
                      fill="transparent"
                      stroke="currentColor"
                      stroke-width="8"
                      stroke-linecap="round"
                      stroke-dasharray="200"
                      :stroke-dashoffset="aiScoreOffset"
                    />
                  </svg>
                  <div class="absolute inset-0 flex flex-col items-center justify-center pt-2">
                    <span class="text-5xl font-black tracking-tight text-slate-900 dark:text-slate-100">{{ displayScore }}</span>
                    <span class="mt-1 text-sm font-medium text-slate-400 dark:text-slate-500">/ 100</span>
                  </div>
                </div>
                <div class="mt-2 text-center">
                  <div class="inline-flex items-center gap-2 rounded-full border px-3 py-1 text-sm font-bold" :class="gradeTheme.badgeClass">
                    <span class="flex h-2 w-2 rounded-full" :class="gradeTheme.dotClass"></span>
                    <span class="material-symbols-outlined text-[16px]" :class="gradeTheme.iconClass">{{ gradeTheme.icon }}</span>
                    {{ dailyReport?.grade || '평가 없음' }}
                  </div>
                  <p v-if="reportUpdatedAtText" class="mt-2 text-xs text-slate-400 dark:text-slate-500">업데이트 {{ reportUpdatedAtText }}</p>
                </div>
              </div>

              <div class="relative overflow-hidden rounded-2xl bg-slate-800 p-6 text-white shadow-lg dark:bg-slate-700">
                <div class="absolute -right-10 -top-10 h-32 w-32 rounded-full bg-primary/20 blur-2xl"></div>
                <div class="relative z-10">
                  <div class="flex items-center gap-2 text-xs font-semibold uppercase tracking-widest text-slate-300">
                    <span class="material-symbols-outlined text-primary">summarize</span>
                    요약
                  </div>
                  <p class="mt-3 text-sm leading-relaxed text-slate-100">
                    {{ reportSummary }}
                  </p>
                </div>
              </div>
            </div>

            <div class="flex min-w-[560px] flex-1 flex-col gap-6">
              <div class="rounded-2xl border border-emerald-100 bg-emerald-50/80 p-5 shadow-sm dark:border-emerald-500/30 dark:bg-emerald-500/10">
                <div class="mb-3 flex items-center gap-2">
                  <span class="material-symbols-outlined text-primary">thumb_up</span>
                  <h4 class="text-base font-bold text-slate-900 dark:text-slate-100">잘한 점</h4>
                </div>
                <ul class="space-y-2 text-sm text-slate-600 dark:text-slate-300">
                  <li v-for="(item, index) in reportHighlights" :key="`highlight-${index}`">{{ item }}</li>
                  <li v-if="!reportHighlights.length" class="text-slate-400">아직 평가 항목이 없습니다.</li>
                </ul>
              </div>

              <div class="rounded-2xl border border-amber-100 bg-amber-50/80 p-5 shadow-sm dark:border-amber-500/30 dark:bg-amber-500/10">
                <div class="mb-3 flex items-center gap-2">
                  <span class="material-symbols-outlined text-amber-500">lightbulb</span>
                  <h4 class="text-base font-bold text-slate-900 dark:text-slate-100">개선 제안</h4>
                </div>
                <ul class="space-y-2 text-sm text-slate-600 dark:text-slate-300">
                  <li v-for="(item, index) in reportImprovements" :key="`improve-${index}`">{{ item }}</li>
                  <li v-if="!reportImprovements.length" class="text-slate-400">아직 개선 제안이 없습니다.</li>
                </ul>
              </div>

              <div class="rounded-2xl border border-sky-100 bg-sky-50/80 p-5 shadow-sm dark:border-sky-500/30 dark:bg-sky-500/10">
                <div class="mb-3 flex items-center gap-2">
                  <span class="material-symbols-outlined text-sky-500">check_circle</span>
                  <h4 class="text-base font-bold text-slate-900 dark:text-slate-100">추천 행동</h4>
                </div>
                <ul class="space-y-2 text-sm text-slate-600 dark:text-slate-300">
                  <li v-for="(item, index) in reportRecommendations" :key="`recommend-${index}`">{{ item }}</li>
                  <li v-if="!reportRecommendations.length" class="text-slate-400">아직 추천이 없습니다.</li>
                </ul>
              </div>
              <p v-if="reportLoading" class="text-xs text-slate-400">평가 정보를 불러오는 중입니다...</p>
            </div>
          </div>
        </div>
      </section>

      <section class="grid grid-cols-2 gap-6">
        <div
          v-for="meal in mealCards"
          :key="meal.type"
          class="flex flex-col gap-4 rounded-xl border border-slate-200 bg-white p-6 shadow-sm dark:border-slate-800 dark:bg-slate-900"
        >
          <div class="flex items-center justify-between">
            <h3 class="text-xl font-bold text-slate-900 dark:text-slate-100">{{ meal.label }}</h3>
            <button
              class="rounded-lg border border-slate-200 px-3 py-2 text-xs font-semibold text-slate-600 transition hover:bg-slate-100 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800"
              @click="handleOpenAdd(meal.label)"
            >
              음식 추가
            </button>
          </div>

          <div class="relative">
            <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">search</span>
            <input
              class="h-12 w-full rounded-lg border border-slate-300 bg-slate-50 pl-10 pr-4 text-slate-800 focus:border-primary focus:ring-2 focus:ring-primary/30 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-200"
              placeholder="음식 이름을 검색하여 추가하세요"
              type="text"
              readonly
              @focus="handleOpenAdd(meal.label)"
            />
          </div>

          <div class="rounded-lg border border-slate-200 bg-slate-50/70 p-2 dark:border-slate-700 dark:bg-slate-800/60">
            <div class="h-56 overflow-y-auto pr-2">
              <ul v-if="meal.items.length" class="space-y-3">
                <li
                  v-for="item in meal.items"
                  :key="item.mealFoodId"
                  class="flex items-center justify-between rounded-lg bg-white p-3 shadow-sm dark:bg-slate-900"
                >
                  <div>
                    <p class="font-medium text-slate-800 dark:text-slate-200">{{ item.name }}</p>
                    <p class="text-sm text-slate-500 dark:text-slate-400">{{ item.detail }}</p>
                  </div>
                  <div class="flex gap-2">
                    <button class="text-slate-500 hover:text-primary dark:text-slate-400" @click="handleOpenEdit({ label: meal.label, item })">
                      <span class="material-symbols-outlined">edit</span>
                    </button>
                    <button class="text-slate-500 hover:text-red-500 dark:text-slate-400" @click="handleDeleteItem({ item })">
                      <span class="material-symbols-outlined">delete</span>
                    </button>
                  </div>
                </li>
              </ul>

              <div v-else class="flex h-full items-center justify-center text-center text-slate-500 dark:text-slate-400">
                <p>{{ meal.emptyMessage }}</p>
              </div>
            </div>
          </div>

          <div
            class="mt-4 border-t border-slate-200 pt-5 dark:border-slate-700"
            :class="{ 'opacity-50': !meal.items.length }"
          >
            <h4 class="mb-4 flex items-center gap-2 text-base font-bold text-slate-800 dark:text-slate-200">
              <span class="material-symbols-outlined text-primary">analytics</span>
              {{ meal.label }} 영양 분석
            </h4>
            <div class="flex flex-col gap-4">
              <div class="flex items-center justify-between rounded-xl border border-primary/10 bg-primary/5 p-4 dark:bg-primary/10">
                <div>
                  <span class="text-sm font-medium text-slate-500 dark:text-slate-400">총 섭취 칼로리</span>
                  <div class="mt-1 text-2xl font-black text-slate-900 dark:text-slate-100">
                    {{ formatOneDecimal(meal.stats.kcal) }}
                    <span class="text-sm font-normal text-slate-500">kcal</span>
                  </div>
                </div>
                <div class="flex size-10 items-center justify-center rounded-full bg-primary/20 text-primary">
                  <span class="material-symbols-outlined">local_fire_department</span>
                </div>
              </div>

              <div class="grid grid-cols-3 gap-3">
                <div v-for="macro in macroCircleData(meal.stats)" :key="macro.key" class="flex flex-col items-center gap-1 rounded-xl border border-slate-100 bg-slate-50 p-3 text-center dark:border-slate-700 dark:bg-slate-800">
                  <div class="mb-1 text-xs font-medium text-slate-500 dark:text-slate-400">{{ macro.label }}</div>
                  <div class="relative h-20 w-20">
                    <svg class="size-full -rotate-90" viewBox="0 0 36 36">
                      <path
                        class="text-slate-200 dark:text-slate-700"
                        d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="3"
                      ></path>
                      <path
                        :class="macro.colorClass"
                        d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="3"
                        :stroke-dasharray="`${macro.percent}, 100`"
                      ></path>
                    </svg>
                    <div class="absolute inset-0 flex flex-col items-center justify-center">
                      <span class="text-base font-bold text-slate-800 dark:text-slate-200">{{ formatOneDecimal(macro.value) }}</span>
                      <span class="text-[10px] leading-none text-slate-500">{{ macro.unit }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="grid grid-cols-3 gap-2">
                <div v-for="detail in microStats(meal.stats)" :key="detail.key" class="flex flex-col rounded-lg bg-slate-50 p-2.5 dark:bg-slate-800/50">
                  <span class="text-[11px] text-slate-500 dark:text-slate-400">{{ detail.label }}</span>
                  <span class="text-sm font-semibold text-slate-700 dark:text-slate-300">{{ formatOneDecimal(detail.value) }}{{ detail.unit }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>

  <AddMealFoodModal
    v-if="showAddModal"
    :meal-label="selectedMealLabel"
    :mode="modalMode"
    :initial-food="editTarget?.food || null"
    :initial-quantity="editTarget?.intake ?? ''"
    @close="handleCloseModal"
    @submit="handleSubmitMeal"
  />

  <div v-if="showDeleteConfirm" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4">
    <div class="w-full max-w-md rounded-xl border border-slate-200 bg-white p-6 shadow-2xl dark:border-slate-700 dark:bg-slate-900">
      <h3 class="text-lg font-bold text-slate-900 dark:text-slate-100">음식 삭제</h3>
      <p class="mt-2 text-sm text-slate-500 dark:text-slate-400">
        {{ deleteTarget?.name || '선택한 음식' }}을(를) 삭제할까요? 삭제하면 되돌릴 수 없습니다.
      </p>
      <div class="mt-6 flex justify-end gap-3">
        <button
          type="button"
          class="h-11 rounded-lg border border-slate-200 bg-white px-5 text-sm font-semibold text-slate-600 transition hover:bg-slate-100 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200"
          @click="closeDeleteConfirm"
        >
          취소
        </button>
        <button
          type="button"
          class="h-11 rounded-lg bg-red-500 px-5 text-sm font-bold text-white transition hover:opacity-90 disabled:opacity-60"
          :disabled="deleteLoading"
          @click="confirmDelete"
        >
          {{ deleteLoading ? '삭제 중...' : '삭제' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue';
import { addDays, format, parseISO } from 'date-fns';
import AddMealFoodModal from '@/components/AddMealFoodModal.vue';
import { addMealFood, deleteMealFood, getMealsByDate, updateMealFood } from '@/services/mealService';
import { getDailyTargets } from '@/services/memberNutrientService';
import { createDailyReport, getDailyReport } from '@/services/reportService';

const selectedDate = ref(format(new Date(), 'yyyy-MM-dd'));
const showDatePicker = ref(false);
const dateControl = ref(null);
const loadingMeals = ref(false);
const mealsError = ref('');
const reportLoading = ref(false);
const reportError = ref('');
const reportRequesting = ref(false);
const dailyReport = ref(null);

const nutrientTargets = reactive({
  kcal: 0,
  carbohydrates: 0,
  protein: 0,
  fat: 0,
  saturatedFat: 0,
  transFat: 0,
  sugar: 0,
  natrium: 0,
  cholesterol: 0,
});

const makeEmptyStats = () => ({
  kcal: 0,
  carbohydrates: 0,
  protein: 0,
  fat: 0,
  saturatedFat: 0,
  transFat: 0,
  sugar: 0,
  natrium: 0,
  cholesterol: 0,
});

const meals = reactive({
  BREAKFAST: { label: '아침', items: [], stats: makeEmptyStats(), emptyMessage: '아침 식사 기록이 없습니다.' },
  LUNCH: { label: '점심', items: [], stats: makeEmptyStats(), emptyMessage: '점심 식사 기록이 없습니다.' },
  DINNER: { label: '저녁', items: [], stats: makeEmptyStats(), emptyMessage: '저녁 식사 기록이 없습니다.' },
  SNACK: { label: '간식', items: [], stats: makeEmptyStats(), emptyMessage: '간식 기록이 없습니다.' },
});

const hydrateMeals = (mealList) => {
  Object.keys(meals).forEach((key) => {
    meals[key].items = [];
    meals[key].stats = makeEmptyStats();
  });

  mealList.forEach((meal) => {
    const type = meal.mealType;
    if (!meals[type]) return;
    meals[type].stats = meal.nutrition
      ? { ...makeEmptyStats(), ...meal.nutrition }
      : meal.foods?.reduce((acc, food) => {
          Object.entries(makeEmptyStats()).forEach(([k]) => {
            acc[k] = (acc[k] || 0) + (food[k] || 0);
          });
          return acc;
        }, makeEmptyStats());
    meals[type].items = (meal.foods || []).map((food) => ({
      mealFoodId: food.mealFoodId,
      mealId: food.mealId,
      foodId: food.foodId,
      name: food.name,
      detail: `${food.intake}g${food.kcal ? `, ${food.kcal} kcal` : ''}`,
      intake: food.intake,
      unit: food.unit || 'g',
      vendor: food.vendor,
      standard: food.standard,
      foodWeight: food.foodWeight,
      food: {
        id: food.foodId,
        name: food.name,
        vendor: food.vendor,
        standard: food.standard,
        foodWeight: food.foodWeight,
        kcal: food.kcal,
        protein: food.protein,
        fat: food.fat,
        saturatedFat: food.saturatedFat,
        transFat: food.transFat,
        carbohydrates: food.carbohydrates,
        sugar: food.sugar,
        natrium: food.natrium,
        cholesterol: food.cholesterol,
      },
      nutrition: {
        kcal: food.kcal,
        carbohydrates: food.carbohydrates,
        protein: food.protein,
        fat: food.fat,
        saturatedFat: food.saturatedFat,
        transFat: food.transFat,
        sugar: food.sugar,
        natrium: food.natrium,
        cholesterol: food.cholesterol,
      },
    }));
  });
};

const totalStats = computed(() => {
  const sum = makeEmptyStats();
  Object.values(meals).forEach((meal) => {
    Object.entries(meal.stats || {}).forEach(([k, v]) => {
      sum[k] = (sum[k] || 0) + (v || 0);
    });
  });
  return sum;
});

const formatOneDecimal = (val) => Number(Number(val || 0).toFixed(1));

const nutrientSummaryBars = computed(() => [
  {
    key: 'kcal',
    label: '총 칼로리',
    current: formatOneDecimal(totalStats.value.kcal),
    target: nutrientTargets.kcal,
    unit: 'kcal',
    colorClass: 'bg-primary',
  },
  {
    key: 'carbohydrates',
    label: '탄수화물',
    current: formatOneDecimal(totalStats.value.carbohydrates),
    target: nutrientTargets.carbohydrates,
    unit: 'g',
    colorClass: 'bg-amber-400',
  },
  {
    key: 'protein',
    label: '단백질',
    current: formatOneDecimal(totalStats.value.protein),
    target: nutrientTargets.protein,
    unit: 'g',
    colorClass: 'bg-red-500',
  },
  {
    key: 'fat',
    label: '지방',
    current: formatOneDecimal(totalStats.value.fat),
    target: nutrientTargets.fat,
    unit: 'g',
    colorClass: 'bg-blue-500',
  },
  {
    key: 'saturatedFat',
    label: '포화 지방',
    current: formatOneDecimal(totalStats.value.saturatedFat),
    target: nutrientTargets.saturatedFat,
    unit: 'g',
    colorClass: 'bg-sky-500',
  },
  {
    key: 'transFat',
    label: '트랜스 지방',
    current: formatOneDecimal(totalStats.value.transFat),
    target: nutrientTargets.transFat,
    unit: 'g',
    colorClass: 'bg-blue-300',
  },
  {
    key: 'sugar',
    label: '당류',
    current: formatOneDecimal(totalStats.value.sugar),
    target: nutrientTargets.sugar,
    unit: 'g',
    colorClass: 'bg-yellow-500',
  },
  {
    key: 'natrium',
    label: '나트륨',
    current: formatOneDecimal(totalStats.value.natrium),
    target: nutrientTargets.natrium,
    unit: 'mg',
    colorClass: 'bg-purple-500',
  },
  {
    key: 'cholesterol',
    label: '콜레스테롤',
    current: formatOneDecimal(totalStats.value.cholesterol),
    target: nutrientTargets.cholesterol,
    unit: 'mg',
    colorClass: 'bg-orange-500',
  },
].map((stat) => ({
  ...stat,
  percent: stat.target ? Math.min(100, Math.round((stat.current / stat.target) * 100)) : 0,
})));

const displayScore = computed(() => {
  if (dailyReport.value?.score !== undefined && dailyReport.value?.score !== null) {
    return dailyReport.value.score;
  }
  return 0;
});

const aiScoreOffset = computed(() => {
  const clamped = Math.min(100, Math.max(0, displayScore.value));
  const total = 200;
  return total - (total * clamped) / 100;
});

const reportSummary = computed(() => dailyReport.value?.summary || '아직 평가가 없습니다.');
const reportHighlights = computed(() => dailyReport.value?.highlights || []);
const reportImprovements = computed(() => dailyReport.value?.improvements || []);
const reportRecommendations = computed(() => dailyReport.value?.recommendations || []);
const reportUpdatedAtText = computed(() => {
  if (!dailyReport.value?.updatedAt) return '';
  try {
    return format(parseISO(dailyReport.value.updatedAt), 'MM.dd HH:mm');
  } catch (error) {
    return dailyReport.value.updatedAt;
  }
});

const gradeTheme = computed(() => {
  const grade = dailyReport.value?.grade;
  switch (grade) {
    case '매우 우수':
      return {
        circleClass: 'text-emerald-500',
        badgeClass: 'border-emerald-200 bg-emerald-50 text-emerald-700',
        dotClass: 'bg-emerald-500',
        icon: 'workspace_premium',
        iconClass: 'text-emerald-600',
      };
    case '우수':
      return {
        circleClass: 'text-green-500',
        badgeClass: 'border-green-200 bg-green-50 text-green-700',
        dotClass: 'bg-green-500',
        icon: 'verified',
        iconClass: 'text-green-600',
      };
    case '보통':
      return {
        circleClass: 'text-amber-500',
        badgeClass: 'border-amber-200 bg-amber-50 text-amber-700',
        dotClass: 'bg-amber-500',
        icon: 'insights',
        iconClass: 'text-amber-600',
      };
    case '개선 필요':
      return {
        circleClass: 'text-orange-500',
        badgeClass: 'border-orange-200 bg-orange-50 text-orange-700',
        dotClass: 'bg-orange-500',
        icon: 'warning',
        iconClass: 'text-orange-600',
      };
    case '집중 개선 필요':
      return {
        circleClass: 'text-rose-500',
        badgeClass: 'border-rose-200 bg-rose-50 text-rose-700',
        dotClass: 'bg-rose-500',
        icon: 'error',
        iconClass: 'text-rose-600',
      };
    default:
      return {
        circleClass: 'text-primary',
        badgeClass: 'border-primary/20 bg-primary/10 text-primary',
        dotClass: 'bg-primary',
        icon: 'check_circle',
        iconClass: 'text-primary',
      };
  }
});

const showAddModal = ref(false);
const modalMode = ref('add');
const editTarget = ref(null);
const showDeleteConfirm = ref(false);
const deleteTarget = ref(null);
const deleteLoading = ref(false);
const selectedMealLabel = ref('');
const selectedMealType = ref('');

const handleOpenAdd = (label) => {
  selectedMealLabel.value = label;
  selectedMealType.value = mealLabelToType(label);
  modalMode.value = 'add';
  editTarget.value = null;
  showAddModal.value = true;
};

const handleOpenEdit = ({ label, item }) => {
  selectedMealLabel.value = label;
  selectedMealType.value = mealLabelToType(label);
  modalMode.value = 'edit';
  editTarget.value = item;
  showAddModal.value = true;
};

const handleCloseModal = () => {
  showAddModal.value = false;
  modalMode.value = 'add';
  editTarget.value = null;
};

const handleSubmitMeal = async (payload) => {
  if (!selectedMealType.value) return;
  try {
    if (modalMode.value === 'edit' && editTarget.value) {
      await updateMealFood(editTarget.value.mealFoodId, {
        mealId: editTarget.value.mealId,
        foodId: payload.foodId,
        intake: payload.intake,
        unit: payload.unit,
      });
    } else {
      await addMealFood({
        mealType: selectedMealType.value,
        mealDate: selectedDate.value,
        foodId: payload.foodId,
        intake: payload.intake,
        unit: payload.unit,
      });
    }
    await fetchMeals();
    handleCloseModal();
  } catch (error) {
    console.error('음식 저장 실패', error);
    alert('음식 저장에 실패했습니다.');
  }
};

const handleDeleteItem = async ({ item }) => {
  if (!item?.mealFoodId) {
    alert('삭제할 수 없는 항목입니다.');
    return;
  }
  deleteTarget.value = item;
  showDeleteConfirm.value = true;
};

const closeDeleteConfirm = () => {
  showDeleteConfirm.value = false;
  deleteTarget.value = null;
  deleteLoading.value = false;
};

const confirmDelete = async () => {
  if (!deleteTarget.value?.mealFoodId || deleteLoading.value) {
    return;
  }
  deleteLoading.value = true;
  try {
    await deleteMealFood(deleteTarget.value.mealFoodId);
    await fetchMeals();
    closeDeleteConfirm();
  } catch (error) {
    console.error('음식 삭제 실패', error);
    alert('음식 삭제에 실패했습니다.');
    deleteLoading.value = false;
  }
};

const mealLabelToType = (label) => {
  switch (label) {
    case '아침':
      return 'BREAKFAST';
    case '점심':
      return 'LUNCH';
    case '저녁':
      return 'DINNER';
    case '간식':
      return 'SNACK';
    default:
      return '';
  }
};

const displayDate = computed(() => {
  try {
    return format(parseISO(selectedDate.value), 'yyyy년 MM월 dd일');
  } catch (error) {
    return selectedDate.value;
  }
});

const changeDateBy = async (delta) => {
  try {
    const next = addDays(parseISO(selectedDate.value), delta);
    selectedDate.value = format(next, 'yyyy-MM-dd');
  } catch (error) {
    console.error('날짜 변경 실패', error);
  }
};

const goPrevDate = () => changeDateBy(-1);
const goNextDate = () => changeDateBy(1);

const jumpToToday = () => {
  selectedDate.value = format(new Date(), 'yyyy-MM-dd');
};

const setDate = (value) => {
  if (!value) return;
  try {
    selectedDate.value = format(parseISO(value), 'yyyy-MM-dd');
    showDatePicker.value = false;
  } catch (error) {
    console.error('날짜 파싱 실패', error);
  }
};

const toggleDatePicker = () => {
  showDatePicker.value = !showDatePicker.value;
};

const closeDatePicker = () => {
  showDatePicker.value = false;
};

const handleOutsideClick = (event) => {
  if (!showDatePicker.value) return;
  if (dateControl.value && !dateControl.value.contains(event.target)) {
    showDatePicker.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleOutsideClick);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleOutsideClick);
});

const fetchMeals = async () => {
  loadingMeals.value = true;
  mealsError.value = '';
  try {
    const res = await getMealsByDate(selectedDate.value);
    hydrateMeals(res.data || []);
  } catch (error) {
    console.error('식단 조회 실패', error);
    mealsError.value = '식단 정보를 불러오지 못했습니다.';
    hydrateMeals([]);
  } finally {
    loadingMeals.value = false;
  }
};

const fetchTargets = async () => {
  try {
    const res = await getDailyTargets(selectedDate.value);
    Object.assign(nutrientTargets, res.data || {});
  } catch (error) {
    console.error('영양 목표 조회 실패', error);
  }
};

const fetchDailyReport = async () => {
  reportLoading.value = true;
  reportError.value = '';
  try {
    const res = await getDailyReport(selectedDate.value);
    if (res?.status === 204) {
      dailyReport.value = null;
      return;
    }
    dailyReport.value = res?.data || null;
  } catch (error) {
    console.error('일간 평가 조회 실패', error);
    reportError.value = '평가 정보를 불러오지 못했습니다.';
    dailyReport.value = null;
  } finally {
    reportLoading.value = false;
  }
};

const requestDailyReport = async () => {
  if (reportRequesting.value) return;
  reportRequesting.value = true;
  reportError.value = '';
  try {
    const res = await createDailyReport(selectedDate.value);
    dailyReport.value = res?.data || null;
  } catch (error) {
    console.error('일간 평가 생성 실패', error);
    reportError.value = '평가 요청에 실패했습니다.';
  } finally {
    reportRequesting.value = false;
  }
};

const mealCards = computed(() => [
  { type: 'BREAKFAST', ...meals.BREAKFAST },
  { type: 'LUNCH', ...meals.LUNCH },
  { type: 'DINNER', ...meals.DINNER },
  { type: 'SNACK', ...meals.SNACK },
]);

const macroCircleData = (stats) => [
  {
    key: 'carbohydrates',
    label: '탄수화물',
    value: stats.carbohydrates,
    unit: 'g',
    percent: calcPercent(stats.carbohydrates, nutrientTargets.carbohydrates),
    colorClass: 'text-amber-400',
  },
  {
    key: 'protein',
    label: '단백질',
    value: stats.protein,
    unit: 'g',
    percent: calcPercent(stats.protein, nutrientTargets.protein),
    colorClass: 'text-red-500',
  },
  {
    key: 'fat',
    label: '지방',
    value: stats.fat,
    unit: 'g',
    percent: calcPercent(stats.fat, nutrientTargets.fat),
    colorClass: 'text-blue-500',
  },
];

const microStats = (stats) => [
  { key: 'saturatedFat', label: '포화지방', value: stats.saturatedFat, unit: 'g' },
  { key: 'transFat', label: '트랜스지방', value: stats.transFat, unit: 'g' },
  { key: 'sugar', label: '당류', value: stats.sugar, unit: 'g' },
  { key: 'natrium', label: '나트륨', value: stats.natrium, unit: 'mg' },
  { key: 'cholesterol', label: '콜레스테롤', value: stats.cholesterol, unit: 'mg' },
];

const calcPercent = (value, target) => {
  if (!target) return 0;
  return Math.min(100, Math.round((Number(value || 0) / Number(target || 1)) * 100));
};

const formatNumber = (value) => {
  const numeric = Number(value || 0);
  if (Number.isNaN(numeric)) return '0.0';
  return numeric.toFixed(1);
};

watch(
  selectedDate,
  async () => {
    await Promise.all([fetchMeals(), fetchTargets(), fetchDailyReport()]);
  },
  { immediate: true },
);
</script>
