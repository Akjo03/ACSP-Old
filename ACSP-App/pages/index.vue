<template>
  <div :class="{
    'theme-light': !darkMode,
    'theme-dark': darkMode
  }" class="min-h-screen bg-themeBackground">
    <HeroSection />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import HeroSection from "../components/index/HeroSection.vue";
import { useTheme } from "../composables/useTheme";

const { darkMode, initializeTheme } = useTheme();

onMounted(() => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }
});
</script>