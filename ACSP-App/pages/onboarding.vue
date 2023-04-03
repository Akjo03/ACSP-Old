<template>
  <div :class="{
    'theme-light': !darkMode,
    'theme-dark': darkMode
  }" class="flex flex-col min-h-screen bg-themeBackground">
    <AppHeader />
    <OnboardingWizard />
    <AppFooter v-bind:links="[
      {
        name: 'Abort Onboarding',
        url: abortOnboardingUrl,
        danger: true
      }
    ]" />
  </div>
</template>

<script setup lang="ts">
import AppHeader from "../components/app/AppHeader.vue";
import OnboardingWizard from "../components/onboarding/OnboardingWizard.vue";
import AppFooter from "../components/app/AppFooter.vue";

import {onMounted} from "vue";
import {useRuntimeConfig} from "nuxt/app";
import {useTheme} from "../composables/useTheme";

const config = useRuntimeConfig();
const { darkMode, initializeTheme } = useTheme();

const abortOnboardingUrl = config.public.backendUrl + "/proxy/user/onboarding/abort";

onMounted(() => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }
});
</script>