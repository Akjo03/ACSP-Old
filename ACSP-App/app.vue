<template>
    <div :class="{
            'theme-light': !darkMode,
            'theme-dark': darkMode
        }" class="flex flex-col min-h-screen min-w-fit bg-gradient-to-br from-themeBackgroundGradientStart to-themeBackgroundGradientEnd">
        <AppHeader />
        <p class="text-themeText">{{sessionStatus}}</p>
        <RouterView />
        <AppFooter />
    </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";

import { useTheme } from "./composables/useTheme";
import { useSession } from "./composables/useSession";

import AppHeader from "./components/global/AppHeader.vue";
import AppFooter from "./components/global/AppFooter.vue";

const {darkMode, initializeTheme} = useTheme();
const {sessionStatus, getSessionStatus} = useSession();

onMounted(async () => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }

    await getSessionStatus();
});
</script>