<template>
  <div class="min-h-screen bg-background-light px-8 py-10 text-text-light dark:bg-background-dark dark:text-text-dark">
    <div class="mx-auto flex w-[1280px] flex-col gap-6">
      <header class="flex flex-col gap-2">
        <h1 class="text-3xl font-black leading-tight tracking-[-0.02em]">마이 페이지</h1>
        <p class="text-text-secondary-light dark:text-text-secondary-dark">회원 정보를 확인하고 비밀번호를 변경할 수 있습니다.</p>
      </header>

      <section v-if="!isVerified" class="rounded-xl border border-border-light bg-card-light p-6 shadow-sm dark:border-border-dark dark:bg-card-dark">
        <h2 class="text-xl font-bold text-text-light dark:text-text-dark">비밀번호 확인</h2>
        <p class="mt-1 text-sm text-text-secondary-light dark:text-text-secondary-dark">마이 페이지 진입을 위해 비밀번호를 한 번 더 확인합니다.</p>
        <form class="mt-4 flex items-end gap-4" @submit.prevent="handlePasswordCheck">
          <div class="flex-1">
            <label class="mb-1 block text-sm font-medium text-text-light dark:text-text-dark" for="verifyPassword">비밀번호</label>
            <input
              id="verifyPassword"
              v-model="verifyPassword"
              type="password"
              required
              class="h-12 w-full rounded-lg border border-border-light bg-background-light px-3 py-2 text-text-light focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
              placeholder="비밀번호를 입력하세요"
            />
          </div>
          <button
            type="submit"
            class="h-12 rounded-lg bg-primary px-5 text-sm font-bold text-white transition hover:opacity-90 disabled:opacity-60"
            :disabled="loadingVerify"
          >
            {{ loadingVerify ? '확인 중...' : '확인' }}
          </button>
        </form>
      </section>

      <template v-else>
        <section class="grid grid-cols-2 gap-6">
          <div class="rounded-xl border border-border-light bg-card-light p-6 shadow-sm dark:border-border-dark dark:bg-card-dark">
            <h2 class="mb-4 text-xl font-bold text-text-light dark:text-text-dark">회원 정보</h2>
            <div class="grid grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark">이메일</label>
                <input
                  class="h-11 rounded-lg border border-border-light bg-background-light px-3 text-sm text-text-secondary-light dark:border-border-dark dark:bg-background-dark dark:text-text-secondary-dark"
                  :value="profileForm.email || '—'"
                  readonly
                />
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark">이름</label>
                <input
                  class="h-11 rounded-lg border border-border-light bg-background-light px-3 text-sm text-text-light dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                  v-model="profileForm.name"
                  :readonly="!isEditMode"
                />
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark">성별</label>
                <select
                  v-model="profileForm.gender"
                  :disabled="!isEditMode"
                  class="h-11 rounded-lg border border-border-light bg-background-light px-3 text-sm text-text-light focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                >
                  <option value="" disabled>선택</option>
                  <option value="MALE">남성</option>
                  <option value="FEMALE">여성</option>
                </select>
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark">생년월일</label>
                <input
                  class="h-11 rounded-lg border border-border-light bg-background-light px-3 text-sm text-text-light dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                  v-model="profileForm.birthDate"
                  :readonly="!isEditMode"
                  type="date"
                />
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark">신장 (cm)</label>
                <input
                  class="h-11 rounded-lg border border-border-light bg-background-light px-3 text-sm text-text-light dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                  v-model="profileForm.heightCm"
                  :readonly="!isEditMode"
                  type="number"
                  step="0.1"
                />
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-sm font-medium text-text-secondary-light dark:text-text-secondary-dark">체중 (kg)</label>
                <input
                  class="h-11 rounded-lg border border-border-light bg-background-light px-3 text-sm text-text-light dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                  v-model="profileForm.weightKg"
                  :readonly="!isEditMode"
                  type="number"
                  step="0.1"
                />
              </div>
            </div>
            <div class="mt-4 flex justify-end gap-3">
              <button
                v-if="!isEditMode"
                type="button"
                class="rounded-lg border border-border-light bg-background-light px-4 py-2 text-sm font-semibold text-text-light transition hover:bg-border-light/60 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                @click="startEdit"
              >
                회원 정보 수정
              </button>
              <template v-else>
                <button
                  type="button"
                  class="rounded-lg border border-border-light bg-background-light px-4 py-2 text-sm font-semibold text-text-light transition hover:bg-border-light/60 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                  @click="cancelEdit"
                >
                  취소
                </button>
                <button
                  type="button"
                  class="rounded-lg bg-primary px-4 py-2 text-sm font-bold text-white transition hover:opacity-90 disabled:opacity-60"
                  :disabled="saveLoading"
                  @click="handleSaveProfile"
                >
                  {{ saveLoading ? '저장 중...' : '완료' }}
                </button>
              </template>
            </div>
          </div>

          <div class="rounded-xl border border-border-light bg-card-light p-6 shadow-sm dark:border-border-dark dark:bg-card-dark">
            <h2 class="mb-4 text-xl font-bold text-text-light dark:text-text-dark">비밀번호 변경</h2>
            <form class="space-y-4" @submit.prevent="handlePasswordChange">
              <div>
                <label class="mb-1 block text-sm font-medium text-text-light dark:text-text-dark" for="currentPassword">현재 비밀번호</label>
                <input
                  id="currentPassword"
                  v-model="passwordForm.currentPassword"
                  type="password"
                  required
                  class="h-11 w-full rounded-lg border border-border-light bg-background-light px-3 text-sm text-text-light focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                />
              </div>
              <div>
                <label class="mb-1 block text-sm font-medium text-text-light dark:text-text-dark" for="newPassword">새 비밀번호</label>
                <input
                  id="newPassword"
                  v-model="passwordForm.newPassword"
                  type="password"
                  required
                  minlength="8"
                  class="h-11 w-full rounded-lg border border-border-light bg-background-light px-3 text-sm text-text-light focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                />
              </div>
              <div>
                <label class="mb-1 block text-sm font-medium text-text-light dark:text-text-dark" for="confirmPassword">새 비밀번호 확인</label>
                <input
                  id="confirmPassword"
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  required
                  minlength="8"
                  class="h-11 w-full rounded-lg border border-border-light bg-background-light px-3 text-sm text-text-light focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                />
              </div>
              <button
                type="submit"
                class="flex w-full items-center justify-center rounded-lg bg-primary px-4 py-3 text-sm font-bold text-white transition hover:opacity-90 disabled:opacity-60"
                :disabled="loadingChange"
              >
                {{ loadingChange ? '변경 중...' : '비밀번호 변경하기' }}
              </button>
            </form>
          </div>
        </section>

        <section class="rounded-xl border border-border-light bg-card-light p-6 shadow-sm dark:border-border-dark dark:bg-card-dark">
          <div class="flex items-center justify-between">
            <div>
              <h3 class="text-lg font-bold text-text-light dark:text-text-dark">일일 권장 섭취량</h3>
              <p class="text-sm text-text-secondary-light dark:text-text-secondary-dark">
                신체 정보를 기반으로 계산된 권장 섭취량입니다.
              </p>
            </div>
            <div class="flex gap-2" v-if="!missingMetrics">
              <button
                type="button"
                class="rounded-md bg-primary px-3 py-2 text-xs font-bold text-white shadow-sm transition hover:opacity-90"
                @click="recalculateTargets"
              >
                권장 섭취량으로 설정
              </button>
              <button
                type="button"
                class="rounded-md border border-border-light bg-background-light px-3 py-2 text-xs font-semibold text-text-light transition hover:bg-border-light/60 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                @click="startNutrientEdit"
              >
                직접 설정하기
              </button>
            </div>
          </div>

          <div v-if="missingMetrics" class="mt-4 rounded-lg border border-dashed border-border-light bg-background-light px-4 py-6 text-sm text-text-secondary-light dark:border-border-dark dark:bg-background-dark dark:text-text-secondary-dark">
            신장과 체중을 입력하면 일일 권장 섭취량을 자동 계산해 보여드려요.
            <button
              type="button"
              class="ml-2 rounded-md border border-border-light bg-card-light px-3 py-1 text-xs font-semibold text-text-light transition hover:bg-border-light/60 dark:border-border-dark dark:bg-card-dark dark:text-text-dark"
              @click="startEdit"
            >
              신체 정보 입력하기
            </button>
          </div>

          <div v-else>
            <div v-if="recommendedLoading" class="mt-6 rounded-lg border border-border-light bg-background-light px-4 py-6 text-sm text-text-secondary-light dark:border-border-dark dark:bg-background-dark dark:text-text-secondary-dark">
              권장 섭취량을 계산하는 중입니다...
            </div>
            <div v-else-if="recommendedError" class="mt-6 rounded-lg border border-border-light bg-background-light px-4 py-6 text-sm text-red-500 dark:border-border-dark dark:bg-background-dark">
              {{ recommendedError }}
            </div>
            <div v-else-if="recommendedList.length === 0" class="mt-6 rounded-lg border border-border-light bg-background-light px-4 py-6 text-sm text-text-secondary-light dark:border-border-dark dark:bg-background-dark dark:text-text-secondary-dark">
              아직 권장 섭취량 데이터가 없습니다. 권장 섭취량으로 설정 버튼을 눌러주세요.
            </div>
            <div v-else class="mt-6 grid grid-cols-3 gap-4">
              <div
                v-for="nutrient in recommendedList"
                :key="nutrient.key"
                class="flex flex-col gap-2 rounded-lg border border-border-light bg-background-light px-4 py-3 text-sm shadow-sm dark:border-border-dark dark:bg-background-dark"
              >
                <div class="flex items-center justify-between">
                  <p class="font-semibold text-text-light dark:text-text-dark">{{ nutrient.label }}</p>
                  <span class="text-[11px] font-medium text-primary/80">{{ nutrient.tag }}</span>
                </div>
                <div v-if="nutrientEditMode">
                  <input
                    v-model="nutrientForm[nutrient.key]"
                    type="number"
                    step="0.1"
                    class="h-11 w-full rounded-lg border border-border-light bg-card-light px-3 text-sm font-semibold text-text-light placeholder:text-text-secondary-light focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 dark:border-border-dark dark:bg-card-dark dark:text-text-dark dark:placeholder:text-text-secondary-dark"
                  />
                </div>
                <p v-else class="text-lg font-bold text-text-light dark:text-text-dark">
                  {{ nutrient.value }} {{ nutrient.unit }}
                </p>
                <p class="text-xs text-text-secondary-light dark:text-text-secondary-dark">{{ nutrient.note }}</p>
              </div>
            </div>
            <div v-if="nutrientEditMode" class="mt-4 flex justify-end gap-2">
              <button
                type="button"
                class="rounded-lg border border-border-light bg-background-light px-4 py-2 text-sm font-semibold text-text-light transition hover:bg-border-light/60 dark:border-border-dark dark:bg-background-dark dark:text-text-dark"
                @click="cancelNutrientEdit"
              >
                취소
              </button>
              <button
                type="button"
                class="rounded-lg bg-primary px-4 py-2 text-sm font-bold text-white transition hover:opacity-90 disabled:opacity-60"
                :disabled="nutrientSaveLoading"
                @click="saveNutrientEdit"
              >
                {{ nutrientSaveLoading ? '저장 중...' : '완료' }}
              </button>
            </div>
          </div>
        </section>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue';
import { useUserStore } from '@/stores/userStore';
import {
  getMemberNutrients,
  passwordChange,
  passwordCheck,
  recalculateMemberNutrients,
  updateMember, updateMemberNutrients,
} from '@/services/memberService';

const userStore = useUserStore();
const member = computed(() => userStore.member || {});

const verifyPassword = ref('');
const isVerified = ref(false);
const loadingVerify = ref(false);

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});
const loadingChange = ref(false);

const isEditMode = ref(false);
const saveLoading = ref(false);
const profileForm = reactive({
  email: '',
  name: '',
  gender: '',
  birthDate: '',
  heightCm: '',
  weightKg: '',
});
const recommendedLoading = ref(false);
const recommendedError = ref('');
const recommendedTargets = ref(null);
const nutrientEditMode = ref(false);
const nutrientSaveLoading = ref(false);
const nutrientForm = reactive({
  kcal: null,
  carbohydrates: null,
  protein: null,
  fat: null,
  saturatedFat: null,
  transFat: null,
  sugar: null,
  natrium: null,
  cholesterol: null,
});

const hydrateProfileForm = () => {
  profileForm.email = member.value.email || '';
  profileForm.name = member.value.name || '';
  profileForm.gender = member.value.gender || '';
  profileForm.birthDate = member.value.birthDate || '';
  profileForm.heightCm = member.value.heightCm ?? '';
  profileForm.weightKg = member.value.weightKg ?? '';
};

watch(
  [member, isVerified],
  () => {
    hydrateProfileForm();
    if (!isVerified.value) {
      recommendedTargets.value = null;
      return;
    }
    fetchNutrientsIfPossible();
  },
  { immediate: true }
);

const handlePasswordCheck = async () => {
  if (loadingVerify.value) return;
  loadingVerify.value = true;
  try {
    const res = await passwordCheck({ password: verifyPassword.value });
    if (res.data === true) {
      isVerified.value = true;
    } else {
      alert('비밀번호가 일치하지 않습니다.');
    }
  } catch (error) {
    console.error('비밀번호 확인 실패', error);
    alert('비밀번호 확인 중 오류가 발생했습니다.');
  } finally {
    loadingVerify.value = false;
    verifyPassword.value = '';
  }
};

const handlePasswordChange = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    alert('새 비밀번호가 일치하지 않습니다.');
    return;
  }
  loadingChange.value = true;
  try {
    await passwordChange({
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
    });
    alert('비밀번호가 변경되었습니다.');
    passwordForm.currentPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
  } catch (error) {
    console.error('비밀번호 변경 실패', error);
    alert('비밀번호 변경에 실패했습니다. 현재 비밀번호를 확인해주세요.');
  } finally {
    loadingChange.value = false;
  }
};

const startEdit = () => {
  hydrateProfileForm();
  isEditMode.value = true;
};

const cancelEdit = () => {
  hydrateProfileForm();
  isEditMode.value = false;
};

const handleSaveProfile = async () => {
  saveLoading.value = true;
  try {
    const payload = {
      name: profileForm.name,
      gender: profileForm.gender,
      birthDate: profileForm.birthDate,
      heightCm: profileForm.heightCm,
      weightKg: profileForm.weightKg,
    };
    const res = await updateMember(payload);
    // 백엔드에서 갱신된 member 정보를 반환한다고 가정
    const updatedMember = res.data?.member || { ...member.value, ...payload };
    userStore.saveUser({
      tokenType: userStore.tokenType,
      accessToken: userStore.accessToken,
      member: updatedMember,
    });
    isEditMode.value = false;
    if (!missingMetrics.value) {
      await fetchNutrientsIfPossible();
    } else {
      recommendedTargets.value = null;
    }
    alert('회원 정보가 수정되었습니다.');
  } catch (error) {
    console.error('회원 정보 수정 실패', error);
    alert('회원 정보 수정에 실패했습니다.');
  } finally {
    saveLoading.value = false;
  }
};

// 권장 섭취량 계산/조회
const missingMetrics = computed(() => !profileForm.heightCm || !profileForm.weightKg);

const recommendedList = computed(() => {
  if (!recommendedTargets.value) return [];
  const t = recommendedTargets.value;
  return [
    { key: 'kcal', label: '칼로리', unit: 'kcal', value: t.kcal, tag: '활동 에너지', note: '체중 유지 및 활동에 필요한 총 에너지입니다.' },
    { key: 'carbohydrates', label: '탄수화물', unit: 'g', value: t.carbohydrates, tag: '주요 에너지원', note: '하루 열량의 약 50%를 권장합니다.' },
    { key: 'protein', label: '단백질', unit: 'g', value: t.protein, tag: '근육 형성', note: '근육 유지와 포만감을 돕습니다.' },
    { key: 'fat', label: '지방', unit: 'g', value: t.fat, tag: '에너지 저장', note: '세포막 구성과 호르몬 생성에 필요합니다.' },
    { key: 'saturatedFat', label: '포화 지방', unit: 'g', value: t.saturatedFat, tag: '섭취 주의', note: '과다 섭취를 피하세요.' },
    { key: 'transFat', label: '트랜스 지방', unit: 'g', value: t.transFat, tag: '섭취 제한', note: '가능한 섭취를 피하는 것이 좋습니다.' },
    { key: 'sugar', label: '당류', unit: 'g', value: t.sugar, tag: '적정량', note: '첨가당은 하루 50g 이하로 권장합니다.' },
    { key: 'natrium', label: '나트륨', unit: 'mg', value: t.natrium, tag: '혈압 관리', note: 'WHO 기준 하루 2000mg 미만 권장.' },
    { key: 'cholesterol', label: '콜레스테롤', unit: 'mg', value: t.cholesterol, tag: '혈관 건강', note: '성인 권장 섭취량 300mg 이하.' },
  ];
});

const fetchNutrientsIfPossible = async () => {
  if (missingMetrics.value) {
    recommendedTargets.value = null;
    return;
  }
  recommendedLoading.value = true;
  recommendedError.value = '';
  try {
    const res = await getMemberNutrients();
    recommendedTargets.value = res.data;
  } catch (error) {
    console.error('권장 섭취량 조회 실패', error);
    recommendedError.value = '권장 섭취량을 불러오지 못했습니다. 나중에 다시 시도해주세요.';
    recommendedTargets.value = null;
  } finally {
    recommendedLoading.value = false;
  }
};

const recalculateTargets = async () => {
  if (missingMetrics.value) {
    alert('신장과 체중을 먼저 입력하고 저장해주세요.');
    return;
  }
  recommendedLoading.value = true;
  recommendedError.value = '';
  try {
    await recalculateMemberNutrients();
    await fetchNutrientsIfPossible();
  } catch (error) {
    console.error('권장 섭취량 재계산 실패', error);
    recommendedError.value = '재계산에 실패했습니다. 다시 시도해주세요.';
  } finally {
    recommendedLoading.value = false;
  }
};

const startNutrientEdit = () => {
  if (!recommendedTargets.value) {
    alert('먼저 권장 섭취량을 가져와 주세요.');
    return;
  }
  Object.assign(nutrientForm, recommendedTargets.value);
  nutrientEditMode.value = true;
};

const cancelNutrientEdit = () => {
  nutrientEditMode.value = false;
};

const saveNutrientEdit = async () => {
  nutrientSaveLoading.value = true;
  try {
    // nullableFields 모두 지원. 빈 값(null/undefined)은 제외하고 보냅니다.
    const payload = {};
    Object.entries(nutrientForm).forEach(([key, val]) => {
      if (val !== null && val !== undefined && val !== '') {
        payload[key] = Number(val);
      }
    });
    if (Object.keys(payload).length === 0) {
      alert('최소 한 가지 값은 입력해야 합니다.');
      return;
    }
    await updateMemberNutrients(payload);
    await fetchNutrientsIfPossible();
    nutrientEditMode.value = false;
    alert('권장 섭취량이 수정되었습니다.');
  } catch (error) {
    console.error('권장 섭취량 수정 실패', error);
    alert('권장 섭취량 수정에 실패했습니다.');
  } finally {
    nutrientSaveLoading.value = false;
  }
};
</script>
