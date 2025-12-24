<template>
  <main class="flex flex-col items-center gap-12 bg-background-light dark:bg-background-dark py-12">
    <!-- Hero Section -->
    <section class="flex w-[1200px] items-center gap-10">
      <div class="flex w-[520px] shrink-0 flex-col gap-6">
        <div class="flex flex-col gap-3 text-left">
          <h1 class="text-text-light dark:text-text-dark text-4xl font-black leading-tight tracking-[-0.033em]">
            당신만의 건강 식단을 시작하세요
          </h1>
          <p class="text-text-secondary-light dark:text-text-secondary-dark text-lg leading-normal">
            개인 맞춤형 식단 계획으로 건강 목표를 쉽게 달성하세요.
          </p>
        </div>
        <router-link
          to="/dashboard"
          class="inline-flex h-12 w-[260px] items-center justify-center rounded-lg bg-primary px-6 text-base font-bold text-white transition-opacity hover:opacity-90"
        >
          식단 관리 시작하기
        </router-link>
      </div>
      <div
        class="shrink-0 w-[640px] h-[360px] rounded-xl bg-cover bg-center bg-no-repeat"
        style='background-image: url("https://lh3.googleusercontent.com/aida-public/AB6AXuAKyWt88AddjCtz1XuGnYMCj_hnLR5HQw22-vt2I_T2aDlULBl4tA68xd3FivMQXtvRJEAGR0CTj_VsiFcYJyCicrFdfRLBHf7tz0jjdPrhhXno_acqW7cpz6RHO_i0_sqqtOEvUiLyRC2kFXQfiDeL46Uh_bUAoCB7lTYo4wxHp7TE-J2GbY0ZuSqNphe90i0AM6aDtUZheFb2f4L4BZxmLlqgb_LAwcYUSdvgD6MZrckRKVg-fFiSQdiL6Gzz9d1o9cDPqlw76CU");'
        data-alt="A vibrant and colorful bowl of fresh salad with various vegetables."
      />
    </section>

    <!-- Feature Section -->
    <section class="flex w-[1200px] flex-col gap-8">
      <div class="flex flex-col gap-3 text-center">
        <h2 class="text-text-light dark:text-text-dark text-3xl font-black leading-tight">
          주요 기능
        </h2>
        <p class="text-text-secondary-light dark:text-text-secondary-dark text-base">
          저희 서비스는 여러분의 건강한 삶을 위해 다음과 같은 기능들을 제공합니다.
        </p>
      </div>
      <div class="grid grid-cols-3 gap-6">
        <div class="flex h-[220px] flex-col items-center justify-center gap-4 rounded-xl border border-border-light bg-card-light p-6 dark:border-border-dark dark:bg-card-dark">
          <div class="flex h-12 w-12 items-center justify-center rounded-full bg-primary/20 text-primary">
            <span class="material-symbols-outlined">restaurant_menu</span>
          </div>
          <h3 class="text-text-light dark:text-text-dark text-lg font-bold leading-tight">맞춤형 식단</h3>
          <p class="text-text-secondary-light dark:text-text-secondary-dark text-sm text-center">
            개인의 목표와 취향에 맞는 식단을 추천받으세요.
          </p>
        </div>
        <div class="flex h-[220px] flex-col items-center justify-center gap-4 rounded-xl border border-border-light bg-card-light p-6 dark:border-border-dark dark:bg-card-dark">
          <div class="flex h-12 w-12 items-center justify-center rounded-full bg-primary/20 text-primary">
            <span class="material-symbols-outlined">edit_calendar</span>
          </div>
          <h3 class="text-text-light dark:text-text-dark text-lg font-bold leading-tight">간편한 기록</h3>
          <p class="text-text-secondary-light dark:text-text-secondary-dark text-sm text-center">
            매일의 식사를 손쉽게 기록하고 칼로리를 추적하세요.
          </p>
        </div>
        <div class="flex h-[220px] flex-col items-center justify-center gap-4 rounded-xl border border-border-light bg-card-light p-6 dark:border-border-dark dark:bg-card-dark">
          <div class="flex h-12 w-12 items-center justify-center rounded-full bg-primary/20 text-primary">
            <span class="material-symbols-outlined">support_agent</span>
          </div>
          <h3 class="text-text-light dark:text-text-dark text-lg font-bold leading-tight">전문가 조언</h3>
          <p class="text-text-secondary-light dark:text-text-secondary-dark text-sm text-center">
            영양 전문가의 조언을 통해 더 건강한 선택을 하세요.
          </p>
        </div>
      </div>
    </section>
  </main>
</template>
