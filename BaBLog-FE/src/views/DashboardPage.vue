<template>
  <div class="min-h-screen bg-background-light px-8 py-10 text-text-light dark:bg-background-dark dark:text-text-dark">
    <div class="mx-auto flex w-[1280px] flex-col gap-8">
      <header class="flex flex-col gap-2">
        <h1 class="text-3xl font-black leading-tight tracking-[-0.02em]">대시보드</h1>
        <p class="text-text-secondary-light dark:text-text-secondary-dark">
          오늘의 식단과 목표 달성 현황을 확인하세요.
        </p>
      </header>

      <div class="grid grid-cols-[3fr_1fr] gap-6">
        <section class="flex flex-col gap-6">
          <div class="rounded-xl border border-border-light bg-card-light p-6 shadow-sm dark:border-border-dark dark:bg-card-dark">
            <div class="mb-4 flex items-center justify-between">
              <h2 class="text-[22px] font-bold leading-tight tracking-[-0.015em]">오늘의 식단 기록 요약</h2>
              <router-link
                to="/meal-log"
                class="inline-flex items-center gap-1 text-sm font-semibold text-primary hover:opacity-80"
              >
                식단 기록하기
              </router-link>
            </div>
            <div class="grid grid-cols-2 gap-6">
              <div class="flex flex-col gap-3">
                <h3 class="text-sm font-semibold text-text-secondary-light dark:text-text-secondary-dark">
                  오늘의 식사
                </h3>
                <ul class="flex flex-col gap-3">
                  <li
                    v-for="meal in mealSummaries"
                    :key="meal.mealType"
                    class="flex items-center justify-between rounded-lg border border-border-light bg-background-light px-4 py-3 dark:border-border-dark dark:bg-background-dark"
                    :class="meal.foodCount === 0 ? 'opacity-60' : ''"
                  >
                    <div class="flex items-center gap-3">
                      <img class="w-8 h-8" :src="meal.icon" :alt="meal.label" />
                      <div>
                        <p class="font-medium">{{ meal.label }}</p>
                        <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark">
                          {{ meal.displayName }}
                        </p>
                      </div>
                    </div>
                    <p v-if="meal.kcal > 0" class="font-semibold text-text-light dark:text-text-dark">{{ formatNumber(meal.kcal) }} kcal</p>
                  </li>
                </ul>
              </div>
              <div class="flex flex-col gap-3">
                <h3 class="text-sm font-semibold text-text-secondary-light dark:text-text-secondary-dark">
                  총 섭취 현황
                </h3>
                <div class="flex h-full flex-col rounded-lg border border-border-light bg-background-light p-4 dark:border-border-dark dark:bg-background-dark">
                  <div class="flex items-baseline gap-2 justify-between">
                    <p class="text-3xl font-bold text-primary">{{ formatNumber(totalKcal) }} kcal</p>
                    <p class="text-text-secondary-light dark:text-text-secondary-dark">오늘 총 섭취</p>
                  </div>
                  <div class="mt-4 border-t border-border-light pt-4 text-sm dark:border-border-dark">
                    <div class="grid grid-cols-2 gap-4">
                      <div
                        v-for="macro in macroSummaries"
                        :key="macro.label"
                        class="flex flex-col gap-2"
                      >
                        <div class="flex items-center justify-between">
                          <span class="text-text-secondary-light dark:text-text-secondary-dark">{{ macro.label }} ({{ macro.unit }})</span>
                          <span class="text-xs text-text-secondary-light dark:text-text-secondary-dark">
                            {{ formatNumber(macro.current) }} / {{ formatNumber(macro.target) }}
                          </span>
                        </div>
                        <div class="h-2 w-full rounded-full bg-border-light dark:bg-border-dark">
                          <div class="h-full rounded-full" :class="macro.colorClass" :style="{ width: `${macro.percent}%` }" />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="rounded-xl border border-slate-200 bg-white shadow-sm dark:border-slate-800 dark:bg-slate-900">
            <div class="flex flex-nowrap items-center justify-between gap-4 border-b border-slate-100 bg-gradient-to-r from-slate-50/50 to-white p-6 dark:border-slate-800/50 dark:from-slate-800/20 dark:to-slate-900">
              <div>
                <h2 class="flex items-center gap-2 text-[22px] font-bold leading-tight tracking-[-0.015em] text-slate-900 dark:text-slate-100">
                  <span class="material-symbols-outlined text-primary">psychology</span>
                  어제의 식단 평가
                </h2>
                <p class="mt-1 text-sm text-slate-500 dark:text-slate-400 whitespace-nowrap">어제 기록된 식단을 기반으로 한 AI 평가입니다.</p>
              </div>
              <router-link
                to="/reports"
                class="inline-flex items-center gap-1 text-sm font-semibold text-primary hover:opacity-80"
              >
                자세히 보기
              </router-link>
            </div>
            <div class="p-6">
              <div
                v-if="yesterdayReportError"
                class="mb-4 flex items-center justify-between gap-3 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 dark:border-red-800/80 dark:bg-red-900/30 dark:text-red-200"
              >
                <span>{{ yesterdayReportError }}</span>
                <button
                  type="button"
                  class="rounded-md border border-red-200 bg-white px-3 py-1 text-xs font-semibold text-red-700 transition hover:bg-red-100 dark:border-red-700 dark:bg-red-800/60 dark:text-red-100 dark:hover:bg-red-700/60"
                  @click="fetchYesterdayReport"
                >
                  다시 불러오기
                </button>
              </div>

              <div v-if="yesterdayReport" class="flex flex-nowrap items-start gap-8">
                <div class="flex w-[360px] shrink-0 flex-col gap-6">
                  <div class="relative flex flex-col items-center justify-center rounded-2xl border border-slate-100 bg-white p-6 shadow-sm dark:border-slate-700 dark:bg-slate-800">
                    <div class="absolute left-0 top-0 h-1 w-full bg-gradient-to-r from-primary/50 via-primary to-primary/50"></div>
                    <h3 class="mb-4 text-sm font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400">AI-Score</h3>
                    <div class="relative mb-2 size-40">
                      <svg class="size-full rotate-[135deg]" viewBox="0 0 100 100">
                        <circle class="text-slate-100 dark:text-slate-700" cx="50" cy="50" r="42" fill="transparent" stroke="currentColor" stroke-width="8" stroke-dasharray="200" stroke-linecap="round" />
                        <circle
                          :class="yesterdayGradeTheme.circleClass"
                          cx="50"
                          cy="50"
                          r="42"
                          fill="transparent"
                          stroke="currentColor"
                          stroke-width="8"
                          stroke-linecap="round"
                          stroke-dasharray="200"
                          :stroke-dashoffset="yesterdayScoreOffset"
                        />
                      </svg>
                      <div class="absolute inset-0 flex flex-col items-center justify-center pt-2">
                        <span class="text-5xl font-black tracking-tight text-slate-900 dark:text-slate-100">{{ yesterdayScore }}</span>
                        <span class="mt-1 text-sm font-medium text-slate-400 dark:text-slate-500">/ 100</span>
                      </div>
                    </div>
                    <div class="mt-2 text-center">
                      <div class="inline-flex items-center gap-2 rounded-full border px-3 py-1 text-sm font-bold" :class="yesterdayGradeTheme.badgeClass">
                        <span class="flex h-2 w-2 rounded-full" :class="yesterdayGradeTheme.dotClass"></span>
                        <span class="material-symbols-outlined text-[16px]" :class="yesterdayGradeTheme.iconClass">{{ yesterdayGradeTheme.icon }}</span>
                        {{ yesterdayReport.grade || '평가 없음' }}
                      </div>
                      <p v-if="yesterdayUpdatedAtText" class="mt-2 text-xs text-slate-400 dark:text-slate-500">업데이트 {{ yesterdayUpdatedAtText }}</p>
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
                        {{ yesterdayReport.summary || '요약이 없습니다.' }}
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
                      <li v-for="(item, index) in (yesterdayReport.highlights || [])" :key="`yesterday-highlight-${index}`">{{ item }}</li>
                      <li v-if="!(yesterdayReport.highlights || []).length" class="text-slate-400">아직 평가 항목이 없습니다.</li>
                    </ul>
                  </div>

                  <div class="rounded-2xl border border-amber-100 bg-amber-50/80 p-5 shadow-sm dark:border-amber-500/30 dark:bg-amber-500/10">
                    <div class="mb-3 flex items-center gap-2">
                      <span class="material-symbols-outlined text-amber-500">lightbulb</span>
                      <h4 class="text-base font-bold text-slate-900 dark:text-slate-100">개선 제안</h4>
                    </div>
                    <ul class="space-y-2 text-sm text-slate-600 dark:text-slate-300">
                      <li v-for="(item, index) in (yesterdayReport.improvements || [])" :key="`yesterday-improve-${index}`">{{ item }}</li>
                      <li v-if="!(yesterdayReport.improvements || []).length" class="text-slate-400">아직 개선 제안이 없습니다.</li>
                    </ul>
                  </div>

                  <div class="rounded-2xl border border-sky-100 bg-sky-50/80 p-5 shadow-sm dark:border-sky-500/30 dark:bg-sky-500/10">
                    <div class="mb-3 flex items-center gap-2">
                      <span class="material-symbols-outlined text-sky-500">check_circle</span>
                      <h4 class="text-base font-bold text-slate-900 dark:text-slate-100">추천 행동</h4>
                    </div>
                    <ul class="space-y-2 text-sm text-slate-600 dark:text-slate-300">
                      <li v-for="(item, index) in (yesterdayReport.recommendations || [])" :key="`yesterday-recommend-${index}`">{{ item }}</li>
                      <li v-if="!(yesterdayReport.recommendations || []).length" class="text-slate-400">아직 추천이 없습니다.</li>
                    </ul>
                  </div>
                  <p v-if="yesterdayReportLoading" class="text-xs text-slate-400">평가 정보를 불러오는 중입니다...</p>
                </div>
              </div>

              <div v-else-if="!yesterdayReportLoading" class="text-sm text-slate-500 dark:text-slate-400">
                아직 어제 평가가 없습니다.
              </div>
            </div>
          </div>

          <div class="rounded-3xl border border-border-light bg-card-light p-5 shadow-sm dark:border-border-dark dark:bg-card-dark">
            <div class="flex items-end justify-between gap-4">
              <div>
                <h2 class="flex items-center gap-3 text-2xl font-black tracking-tight text-text-light dark:text-text-dark">
                  <span class="material-symbols-outlined text-primary text-3xl">restaurant_menu</span>
                  이번 주 인사이트
                </h2>
                <p class="mt-1 text-xs font-medium text-text-secondary-light dark:text-text-secondary-dark">{{ weeklyPeriodText || '이번 주 요약' }}</p>
              </div>
              <router-link to="/reports" class="inline-flex items-center gap-1 text-xs font-bold text-primary hover:opacity-80">
                리포트 자세히
                <span class="material-symbols-outlined text-base">arrow_forward</span>
              </router-link>
            </div>

            <div v-if="weeklyReportError" class="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 dark:border-red-800/80 dark:bg-red-900/30 dark:text-red-200">
              {{ weeklyReportError }}
            </div>

            <div v-if="weeklyReport" class="mt-5 flex flex-col gap-5">
              <div class="grid grid-cols-[360px_1fr] gap-5">
                <div class="flex flex-col gap-4">
                  <div class="rounded-2xl border-l-4 border-primary bg-background-light p-4 shadow-sm dark:bg-background-dark">
                    <h3 class="text-xs font-bold uppercase tracking-wider text-text-secondary-light dark:text-text-secondary-dark">AI 점수</h3>
                    <div class="mt-3 flex items-baseline gap-2">
                      <span class="text-4xl font-black text-text-light dark:text-text-dark">{{ weeklyReport.score }}</span>
                      <span class="text-lg font-bold text-text-secondary-light dark:text-text-secondary-dark">/ 100</span>
                    </div>
                    <div class="mt-3 inline-flex items-center gap-2 rounded-full bg-red-50 px-3 py-1 text-xs font-bold text-red-500 dark:bg-red-900/30 dark:text-red-300">
                      <span class="material-symbols-outlined text-sm">warning</span>
                      {{ weeklyReport.grade }}
                    </div>
                  </div>
                  <div class="rounded-2xl bg-background-light p-4 shadow-sm dark:bg-background-dark">
                    <h3 class="text-xs font-bold uppercase tracking-wider text-text-secondary-light dark:text-text-secondary-dark">일관성</h3>
                    <div class="mt-3 flex items-end justify-between">
                      <div class="flex items-baseline gap-1">
                        <span class="text-3xl font-black text-text-light dark:text-text-dark">{{ weeklyReport.consistencyScore }}</span>
                        <span class="text-sm font-bold text-text-secondary-light dark:text-text-secondary-dark">점</span>
                      </div>
                      <span class="text-xs font-medium text-text-secondary-light dark:text-text-secondary-dark">꾸준함 지표</span>
                    </div>
                    <div class="mt-3 flex gap-2">
                      <div
                        v-for="(filled, index) in weeklyConsistencyBlocks"
                        :key="`consistency-${index}`"
                        class="h-2 w-full rounded-full"
                        :class="filled ? 'bg-primary shadow-[0_0_8px_rgba(76,175,80,0.25)]' : 'bg-border-light dark:bg-border-dark'"
                      ></div>
                    </div>
                  </div>
                  <div class="rounded-2xl bg-background-light p-4 shadow-sm dark:bg-background-dark">
                    <h3 class="text-xs font-bold uppercase tracking-wider text-text-secondary-light dark:text-text-secondary-dark flex items-center gap-2">
                      <span class="material-symbols-outlined text-sm">psychology</span>
                      패턴 요약
                    </h3>
                    <p class="mt-2 text-xs leading-relaxed text-text-secondary-light dark:text-text-secondary-dark">
                      {{ weeklyPatternSummary }}
                    </p>
                  </div>
                </div>

                <section class="rounded-2xl bg-background-light p-4 shadow-sm dark:bg-background-dark">
                  <div class="flex items-end justify-between gap-4">
                    <div>
                      <h3 class="text-base font-bold text-text-light dark:text-text-dark flex items-center gap-2">
                        <span class="material-symbols-outlined text-text-secondary-light text-base">bar_chart</span>
                        주간 흐름
                      </h3>
                      <p class="mt-1 text-xs text-text-secondary-light dark:text-text-secondary-dark">이번 주 식단 건강도 변화</p>
                    </div>
                    <div class="flex gap-4 text-[11px] font-bold text-text-secondary-light dark:text-text-secondary-dark">
                      <div class="flex items-center gap-1.5">
                        <span class="h-2.5 w-2.5 rounded-sm bg-border-light dark:bg-border-dark"></span> 보통
                      </div>
                      <div class="flex items-center gap-1.5">
                        <span class="h-2.5 w-2.5 rounded-sm bg-primary"></span> Best
                      </div>
                      <div class="flex items-center gap-1.5">
                        <span class="h-2.5 w-2.5 rounded-sm bg-red-500"></span> Worst
                      </div>
                    </div>
                  </div>
                  <div class="relative mt-4 h-56">
                    <div class="absolute inset-0 flex flex-col justify-between pb-8 pl-7 pr-4 text-[9px] text-text-secondary-light/70 dark:text-text-secondary-dark/70">
                      <div class="border-b border-border-light/60 dark:border-border-dark/50">100</div>
                      <div class="border-b border-border-light/60 dark:border-border-dark/50">75</div>
                      <div class="border-b border-border-light/60 dark:border-border-dark/50">50</div>
                      <div class="border-b border-border-light/60 dark:border-border-dark/50">25</div>
                      <div class="border-b border-border-light/60 dark:border-border-dark/50">0</div>
                    </div>
                    <div class="absolute inset-0 flex items-end justify-between gap-3 pb-8 pl-7 pr-4">
                      <div v-for="item in weeklyChartItems" :key="item.dateText" class="flex h-full w-full flex-col items-center justify-end">
                        <div
                          class="w-full max-w-[36px] rounded-t-lg transition-all"
                          :class="item.isBest ? 'bg-primary shadow-[0_0_10px_rgba(76,175,80,0.3)]' : item.isWorst ? 'bg-red-500 shadow-[0_0_10px_rgba(239,68,68,0.3)]' : 'bg-border-light dark:bg-border-dark'"
                          :style="{ height: `${item.height}%` }"
                          :title="item.isBest ? 'Best' : item.isWorst ? 'Worst' : '보통'"
                        ></div>
                      </div>
                    </div>
                    <div class="absolute bottom-0 flex w-full justify-between pl-7 pr-4 text-center text-[10px] text-text-secondary-light dark:text-text-secondary-dark">
                      <div v-for="item in weeklyChartItems" :key="`${item.dateText}-label`" class="w-full">
                        <span :class="item.isBest ? 'font-bold text-primary' : item.isWorst ? 'font-bold text-red-500' : ''">{{ item.label }}</span>
                        <div :class="item.isBest ? 'text-primary/80' : item.isWorst ? 'text-red-400' : ''">{{ item.dateText }}</div>
                      </div>
                    </div>
                  </div>
                </section>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div class="rounded-2xl border-2 border-primary/20 bg-emerald-50/60 p-4 dark:bg-emerald-900/20">
                  <div class="flex items-center gap-2">
                    <span class="rounded-md bg-primary p-1 text-white">
                      <span class="material-symbols-outlined text-sm block">thumb_up</span>
                    </span>
                    <h3 class="text-sm font-bold text-primary">Best Day · {{ weeklyBestDayText }}</h3>
                  </div>
                  <p class="mt-2 text-xs font-medium text-text-secondary-light dark:text-text-secondary-dark">
                    {{ weeklyBestReasonText }}
                  </p>
                </div>
                <div class="rounded-2xl border-2 border-red-200 bg-red-50/60 p-4 dark:bg-red-900/20 dark:border-red-900/40">
                  <div class="flex items-center gap-2">
                    <span class="rounded-md bg-red-500 p-1 text-white">
                      <span class="material-symbols-outlined text-sm block">warning</span>
                    </span>
                    <h3 class="text-sm font-bold text-red-500">Worst Day · {{ weeklyWorstDayText }}</h3>
                  </div>
                  <p class="mt-2 text-xs font-medium text-text-secondary-light dark:text-text-secondary-dark">
                    {{ weeklyWorstReasonText }}
                  </p>
                </div>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div class="rounded-2xl border border-border-light/70 bg-slate-50/70 p-4 shadow-sm dark:border-border-dark/70 dark:bg-slate-800/40">
                  <h3 class="mb-2 flex items-center gap-2 text-sm font-bold text-text-light dark:text-text-dark">
                    <span class="material-symbols-outlined text-text-secondary-light text-sm">summarize</span>
                    주간 요약
                  </h3>
                  <p class="text-xs leading-relaxed text-text-secondary-light dark:text-text-secondary-dark">
                    {{ weeklySummaryText }}
                  </p>
                </div>
                <div class="rounded-2xl border-t-4 border-border-light bg-card-light p-4 shadow-sm dark:border-border-dark dark:bg-card-dark">
                  <h3 class="mb-2 flex items-center gap-2 text-sm font-bold text-text-light dark:text-text-dark">
                    <span class="material-symbols-outlined text-text-secondary-light text-sm">center_focus_strong</span>
                    다음 주 포커스
                  </h3>
                  <p class="text-xs leading-relaxed text-text-secondary-light dark:text-text-secondary-dark">
                    {{ weeklyNextFocusText }}
                  </p>
                </div>
              </div>

              <div class="grid grid-cols-3 gap-4">
                <div class="rounded-2xl border border-emerald-100 bg-emerald-50/50 p-4 dark:border-emerald-800 dark:bg-emerald-900/10">
                  <h4 class="mb-2 flex items-center gap-2 text-xs font-bold text-emerald-700 dark:text-emerald-400">
                    <span class="material-symbols-outlined text-xs">check_circle</span>
                    이번 주 잘한 점
                  </h4>
                  <p class="text-xs leading-relaxed text-text-secondary-light dark:text-text-secondary-dark">{{ weeklyHighlightText }}</p>
                </div>
                <div class="rounded-2xl border border-amber-100 bg-amber-50/50 p-4 dark:border-amber-800 dark:bg-amber-900/10">
                  <h4 class="mb-2 flex items-center gap-2 text-xs font-bold text-amber-700 dark:text-amber-400">
                    <span class="material-symbols-outlined text-xs">build</span>
                    개선 포인트
                  </h4>
                  <p class="text-xs leading-relaxed text-text-secondary-light dark:text-text-secondary-dark">{{ weeklyImproveText }}</p>
                </div>
                <div class="rounded-2xl border border-blue-100 bg-blue-50/50 p-4 dark:border-blue-800 dark:bg-blue-900/10">
                  <h4 class="mb-2 flex items-center gap-2 text-xs font-bold text-blue-700 dark:text-blue-400">
                    <span class="material-symbols-outlined text-xs">next_plan</span>
                    다음 주 행동
                  </h4>
                  <p class="text-xs leading-relaxed text-text-secondary-light dark:text-text-secondary-dark">{{ weeklyActionText }}</p>
                </div>
              </div>
            </div>

            <div v-else-if="!weeklyReportLoading" class="mt-6 text-sm text-text-secondary-light dark:text-text-secondary-dark">
              아직 주간 요약이 없습니다.
            </div>
            <p v-if="weeklyReportLoading" class="mt-4 text-xs text-text-secondary-light dark:text-text-secondary-dark">
              주간 요약을 불러오는 중입니다...
            </p>
          </div>

        </section>

        <aside class="flex flex-col gap-6">
            <div class="rounded-xl border border-border-light bg-card-light p-6 shadow-sm dark:border-border-dark dark:bg-card-dark">
              <div class="mb-4 flex items-center justify-between">
                <h3 class="text-lg font-bold">일일 목표</h3>
                <router-link to="/goals" class="inline-flex items-center gap-1 text-sm font-semibold text-primary hover:opacity-80">
                  목표 설정
                </router-link>
              </div>
            <div class="flex flex-col gap-4">
              <div v-for="goal in dailyGoals" :key="goal.label" class="flex flex-col gap-2">
                <div class="flex items-center justify-between">
                  <p class="text-sm font-medium">{{ goal.label }}</p>
                  <p class="text-sm font-semibold" :class="goal.progress === 100 ? 'text-primary' : ''">{{ goal.progress }}%</p>
                </div>
                <div class="h-2 w-full rounded-full bg-border-light dark:bg-border-dark">
                  <div class="h-full rounded-full bg-primary" :style="{ width: goal.progress + '%' }" />
                </div>
              </div>
            </div>
          </div>

          <div class="rounded-xl border border-border-light bg-card-light p-6 shadow-sm dark:border-border-dark dark:bg-card-dark">
            <h3 class="text-lg font-bold">주간 목표</h3>
            <div class="mt-4 flex flex-col gap-4">
              <div v-for="goal in weeklyGoals" :key="goal.label" class="flex flex-col gap-2">
                <div class="flex items-center justify-between">
                  <p class="text-sm font-medium">{{ goal.label }}</p>
                  <p class="text-sm font-semibold">{{ goal.progress }}%</p>
                </div>
                <div class="h-2 w-full rounded-full bg-border-light dark:bg-border-dark">
                  <div class="h-full rounded-full bg-primary" :style="{ width: goal.progress + '%' }" />
                </div>
              </div>
            </div>
          </div>

          <div class="rounded-xl border border-border-light bg-card-light p-6 shadow-sm dark:border-border-dark dark:bg-card-dark">
            <h3 class="text-lg font-bold">기간 목표</h3>
            <div class="mt-4 flex flex-col gap-4">
              <div v-for="goal in longTermGoals" :key="goal.label" class="flex flex-col gap-2">
                <div class="flex items-center justify-between">
                  <p class="text-sm font-medium">{{ goal.label }}</p>
                  <p class="text-sm font-semibold">{{ goal.progress }}%</p>
                </div>
                <div class="h-2 w-full rounded-full bg-border-light dark:bg-border-dark">
                  <div class="h-full rounded-full bg-primary" :style="{ width: goal.progress + '%' }" />
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { addDays, format, parseISO } from 'date-fns';
import { getDailyMealSummary } from '@/services/mealService';
import { getDailyReport, getWeeklyReport } from '@/services/reportService';
import breakfastIcon from '@/assets/breakfast.png';
import lunchIcon from '@/assets/lunch.png';
import dinnerIcon from '@/assets/dinner.png';
import snackIcon from '@/assets/bablog_logo.png';

const today = format(new Date(), 'yyyy-MM-dd');
const yesterday = format(addDays(new Date(), -1), 'yyyy-MM-dd');
const summary = ref({
  date: today,
  totals: {
    kcal: 0,
    carbohydrates: 0,
    protein: 0,
    fat: 0,
    saturatedFat: 0,
    transFat: 0,
    sugar: 0,
    natrium: 0,
    cholesterol: 0,
  },
  targets: {
    kcal: 0,
    carbohydrates: 0,
    protein: 0,
    fat: 0,
    saturatedFat: 0,
    transFat: 0,
    sugar: 0,
    natrium: 0,
    cholesterol: 0,
  },
  meals: [],
});
const loadingSummary = ref(false);
const summaryError = ref('');
const yesterdayReport = ref(null);
const yesterdayReportLoading = ref(false);
const yesterdayReportError = ref('');
const weeklyReport = ref(null);
const weeklyReportLoading = ref(false);
const weeklyReportError = ref('');

const mealMeta = {
  BREAKFAST: { label: '아침', icon: breakfastIcon },
  LUNCH: { label: '점심', icon: lunchIcon },
  DINNER: { label: '저녁', icon: dinnerIcon },
  SNACK: { label: '간식', icon: snackIcon },
};

const mealOrder = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];

const formatNumber = (value) => {
  const numeric = Number(value || 0);
  if (Number.isNaN(numeric)) return '0';
  return numeric % 1 === 0 ? numeric.toLocaleString() : numeric.toFixed(1);
};

const yesterdayScore = computed(() => {
  if (yesterdayReport.value?.score !== undefined && yesterdayReport.value?.score !== null) {
    return yesterdayReport.value.score;
  }
  return 0;
});

const yesterdayScoreOffset = computed(() => {
  const clamped = Math.min(100, Math.max(0, yesterdayScore.value));
  const total = 200;
  return total - (total * clamped) / 100;
});

const yesterdayUpdatedAtText = computed(() => {
  const updatedAt = yesterdayReport.value?.updatedAt;
  if (!updatedAt) return '';
  try {
    return format(parseISO(updatedAt), 'MM.dd HH:mm');
  } catch (error) {
    return updatedAt;
  }
});

const weeklyPeriodText = computed(() => {
  if (!weeklyReport.value?.startDate || !weeklyReport.value?.endDate) {
    return '';
  }
  try {
    return `${format(parseISO(weeklyReport.value.startDate), 'MM.dd')} ~ ${format(parseISO(weeklyReport.value.endDate), 'MM.dd')}`;
  } catch (error) {
    return `${weeklyReport.value.startDate} ~ ${weeklyReport.value.endDate}`;
  }
});

const weeklyPatternSummary = computed(() => weeklyReport.value?.patternSummary || '이번 주 패턴 요약이 없습니다.');
const weeklySummaryText = computed(() => weeklyReport.value?.summary || '이번 주 요약이 아직 없습니다.');

const weeklyHighlightText = computed(() => weeklyReport.value?.highlights?.[0] || '아직 기록된 항목이 없습니다.');
const weeklyImproveText = computed(() => weeklyReport.value?.improvements?.[0] || '아직 기록된 항목이 없습니다.');
const weeklyActionText = computed(() => weeklyReport.value?.recommendations?.[0] || '아직 기록된 항목이 없습니다.');

const weeklyBestDayText = computed(() => {
  const bestDay = weeklyReport.value?.bestDay;
  if (!bestDay) return '-';
  try {
    return format(parseISO(bestDay), 'MM.dd');
  } catch (error) {
    return bestDay;
  }
});
const weeklyWorstDayText = computed(() => {
  const worstDay = weeklyReport.value?.worstDay;
  if (!worstDay) return '-';
  try {
    return format(parseISO(worstDay), 'MM.dd');
  } catch (error) {
    return worstDay;
  }
});
const weeklyNextFocusText = computed(() => weeklyReport.value?.nextWeekFocus || '다음 주 포커스가 아직 없습니다.');

const weeklyBestReasonText = computed(() => weeklyReport.value?.bestReason || weeklyHighlightText.value);
const weeklyWorstReasonText = computed(() => weeklyReport.value?.worstReason || weeklyImproveText.value);

const weeklyConsistencyBlocks = computed(() => {
  const raw = Number(weeklyReport.value?.consistencyScore ?? 0);
  if (Number.isNaN(raw)) return Array.from({ length: 5 }, () => false);
  const filled = Math.min(5, Math.max(0, Math.round(raw / 20)));
  return Array.from({ length: 5 }, (_, index) => index < filled);
});

const weeklyChartItems = computed(() => {
  const startDate = weeklyReport.value?.startDate;
  if (!startDate) return [];
  let start;
  try {
    start = parseISO(startDate);
  } catch (error) {
    return [];
  }
  const labels = ['월', '화', '수', '목', '금', '토', '일'];
  const scores = Array.isArray(weeklyReport.value?.dailyScores) ? weeklyReport.value.dailyScores : [];
  const scoreMap = new Map(scores.map((item) => [item.date, item.score]));
  const bestDay = weeklyReport.value?.bestDay;
  const worstDay = weeklyReport.value?.worstDay;
  return Array.from({ length: 7 }, (_, index) => {
    const date = addDays(start, index);
    const iso = format(date, 'yyyy-MM-dd');
    const score = scoreMap.has(iso) ? Number(scoreMap.get(iso)) : null;
    const normalized = Number.isFinite(score) ? Math.min(100, Math.max(0, score)) : null;
    const height = normalized === null ? 6 : Math.max(6, normalized);
    const isBest = bestDay === iso && normalized !== null;
    const isWorst = worstDay === iso && normalized !== null;
    return {
      label: labels[index],
      dateText: format(date, 'MM.dd'),
      height,
      isBest,
      isWorst,
      isMissing: normalized === null,
    };
  });
});

const yesterdayGradeTheme = computed(() => {
  const grade = yesterdayReport.value?.grade;
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

const mealSummaries = computed(() =>
  mealOrder.map((mealType) => {
    const meta = mealMeta[mealType];
    const base = summary.value.meals.find((meal) => meal.mealType === mealType) || {};
    const foodCount = Number(base.foodCount || 0);
    const representativeFoodName = base.representativeFoodName || '';
    let displayName = '기록되지 않음';
    if (foodCount > 0) {
      if (representativeFoodName) {
        displayName = foodCount > 1
          ? `${representativeFoodName} 외 ${foodCount - 1}종`
          : representativeFoodName;
      } else {
        displayName = `음식 ${foodCount}종`;
      }
    }
    return {
      mealType,
      label: meta.label,
      icon: meta.icon,
      foodCount,
      displayName,
      kcal: Number(base.kcal || 0),
    };
  })
);

const totalKcal = computed(() => summary.value.totals?.kcal ?? 0);

const nutrientDefinitions = [
  { key: 'carbohydrates', label: '탄수화물', unit: 'g' },
  { key: 'protein', label: '단백질', unit: 'g' },
  { key: 'fat', label: '지방', unit: 'g' },
  { key: 'saturatedFat', label: '포화 지방', unit: 'g' },
  { key: 'transFat', label: '트랜스 지방', unit: 'g' },
  { key: 'sugar', label: '당류', unit: 'g' },
  { key: 'natrium', label: '나트륨', unit: 'mg' },
  { key: 'cholesterol', label: '콜레스테롤', unit: 'mg' },
];

const macroSummaries = computed(() =>
  nutrientDefinitions.map((nutrient) => {
    const current = Number(summary.value.totals?.[nutrient.key] || 0);
    const target = Number(summary.value.targets?.[nutrient.key] || 0);
    const percent = target > 0 ? Math.min(100, Math.round((current / target) * 100)) : 0;
    const colorClass = getProgressColorClass(current, target);
    return {
      label: nutrient.label,
      unit: nutrient.unit,
      current,
      target,
      percent,
      colorClass,
    };
  })
);

const getProgressColorClass = (current, target) => {
  if (!target || target <= 0) {
    return 'bg-border-light dark:bg-border-dark';
  }
  const ratio = current / target;
  if (ratio < 0.6) {
    return 'bg-amber-400';
  }
  if (ratio <= 1.1) {
    return 'bg-primary';
  }
  return 'bg-red-500';
};

const normalizeSummary = (data) => ({
  date: data?.date || today,
  totals: {
    kcal: Number(data?.totals?.kcal || 0),
    carbohydrates: Number(data?.totals?.carbohydrates || 0),
    protein: Number(data?.totals?.protein || 0),
    fat: Number(data?.totals?.fat || 0),
    saturatedFat: Number(data?.totals?.saturatedFat || 0),
    transFat: Number(data?.totals?.transFat || 0),
    sugar: Number(data?.totals?.sugar || 0),
    natrium: Number(data?.totals?.natrium || 0),
    cholesterol: Number(data?.totals?.cholesterol || 0),
  },
  targets: {
    kcal: Number(data?.targets?.kcal || 0),
    carbohydrates: Number(data?.targets?.carbohydrates || 0),
    protein: Number(data?.targets?.protein || 0),
    fat: Number(data?.targets?.fat || 0),
    saturatedFat: Number(data?.targets?.saturatedFat || 0),
    transFat: Number(data?.targets?.transFat || 0),
    sugar: Number(data?.targets?.sugar || 0),
    natrium: Number(data?.targets?.natrium || 0),
    cholesterol: Number(data?.targets?.cholesterol || 0),
  },
  meals: Array.isArray(data?.meals) ? data.meals : [],
});

const fetchSummary = async () => {
  loadingSummary.value = true;
  summaryError.value = '';
  try {
    const res = await getDailyMealSummary(today);
    summary.value = normalizeSummary(res.data);
  } catch (error) {
    console.error('대시보드 요약 조회 실패', error);
    summaryError.value = '대시보드 요약을 불러오지 못했습니다.';
    summary.value = normalizeSummary(null);
  } finally {
    loadingSummary.value = false;
  }
};

const fetchYesterdayReport = async () => {
  yesterdayReportLoading.value = true;
  yesterdayReportError.value = '';
  try {
    const res = await getDailyReport(yesterday);
    if (res?.status === 204) {
      yesterdayReport.value = null;
      return;
    }
    yesterdayReport.value = res?.data || null;
  } catch (error) {
    console.error('어제 평가 조회 실패', error);
    yesterdayReportError.value = '어제 평가를 불러오지 못했습니다.';
    yesterdayReport.value = null;
  } finally {
    yesterdayReportLoading.value = false;
  }
};

const fetchWeeklyReport = async () => {
  weeklyReportLoading.value = true;
  weeklyReportError.value = '';
  try {
    const res = await getWeeklyReport(today);
    if (res?.status === 204) {
      weeklyReport.value = null;
      return;
    }
    weeklyReport.value = res?.data || null;
  } catch (error) {
    console.error('주간 평가 조회 실패', error);
    weeklyReportError.value = '주간 평가를 불러오지 못했습니다.';
    weeklyReport.value = null;
  } finally {
    weeklyReportLoading.value = false;
  }
};

onMounted(() => {
  fetchSummary();
  fetchYesterdayReport();
  fetchWeeklyReport();
});

const dailyGoals = [
  { label: '물 2L 마시기', progress: 100 },
  { label: '점심 후 15분 산책', progress: 0 },
];

const weeklyGoals = [
  { label: '운동 3회 이상 하기', progress: 66 },
  { label: '채소 5종류 먹기', progress: 80 },
];

const longTermGoals = [
  { label: '체지방 2kg 감량', progress: 45 },
  { label: '3개월 식단 기록 유지', progress: 75 },
];
</script>
