<template>
  <div :class="{
    'theme-light': !darkMode,
    'theme-dark': darkMode
  }" class="flex flex-col items-center justify-center h-screen bg-themeBackground">
    <p class="pt-4 px-8 text-center text-2xl text-themeText break-all">{{ userEmail }}</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useCookie, useRuntimeConfig } from "nuxt/app";
import { getFromApi } from "../composables/fetch";
import { useTheme } from "../composables/useTheme";

const config = useRuntimeConfig();
const { darkMode, initializeTheme } = useTheme();

const sessionIdCookie = useCookie("session_id");
const sessionId = ref("");
const userEmail = ref("");

onMounted(() => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }

    sessionId.value = sessionIdCookie.value || "";

    getFromApi("/user/@me", sessionId.value).then((response) => {
        userEmail.value = response.user.email;
    });
});
</script>