<template>
    <div :class="{
            'theme-light': !darkMode,
            'theme-dark': darkMode
        }" class="flex flex-col min-h-screen bg-gradient-to-br from-themeBackgroundGradientStart to-themeBackgroundGradientEnd">
        <AppHeader />
        <RouterView />
        <AppFooter />
    </div>
</template>

<script setup lang="ts">
import {onMounted} from "vue";
import {useTheme} from "./composables/useTheme";

import AppHeader from "./components/global/AppHeader.vue";
import AppFooter from "./components/global/AppFooter.vue";

const {darkMode, initializeTheme} = useTheme();

onMounted(() => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }
});
</script>