<template>
  <div class="min-h-screen bg-background-light dark:bg-background-dark">
    <PublicHeader @open-login="showLoginModal = true" @open-signup="showSignUpModal = true" />
    <router-view />
    <PublicFooter />

    <!-- Public 전용 모달 -->
    <LoginModal v-if="showLoginModal" @close="showLoginModal = false" />
    <SignUpModal v-if="showSignUpModal" @close="showSignUpModal = false" />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import PublicHeader from '@/components/PublicHeader.vue';
import PublicFooter from '@/components/PublicFooter.vue';
import LoginModal from '@/components/LoginModal.vue';
import SignUpModal from '@/components/SignUpModal.vue';

const showLoginModal = ref(false);
const showSignUpModal = ref(false);
</script>
