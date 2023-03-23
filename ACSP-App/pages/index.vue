<template>
  <div :class="{
    'theme-light': !darkMode,
    'theme-dark': darkMode
  }"
  class="min-h-screen bg-themeBackground">
    <HeroSection />
  </div>
</template>


<script setup lang="ts">
import {useState} from "nuxt/app";
import {onMounted, watch} from "vue";
import HeroSection from "../components/index/HeroSection.vue";

type Theme = 'light' | 'dark';

  const LOCAL_STORAGE_THEME_KEY = 'theme';

  const darkMode = useState('theme', () => true);

  const setTheme = (newTheme: Theme) => {
    localStorage.setItem(LOCAL_STORAGE_THEME_KEY, newTheme);
    darkMode.value = newTheme === 'dark';
  }

  onMounted(() => {
    const isDarkModePreferred = window.matchMedia('(prefers-color-scheme: dark)').matches;

    const themeFromLocalStorage = localStorage.getItem(LOCAL_STORAGE_THEME_KEY);

    if (themeFromLocalStorage) {
      setTheme(themeFromLocalStorage as Theme);
    } else {
      setTheme(isDarkModePreferred ? 'dark' : 'light');
    }
  });

  watch(darkMode, (selected) => {
    setTheme(selected ? 'dark' : 'light');
  });
</script>