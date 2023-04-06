<template>
    <div :class="{
            'theme-light': !darkMode,
            'theme-dark': darkMode
        }" class="flex flex-col min-h-screen min-w-fit bg-gradient-to-br from-themeBackgroundGradientStart to-themeBackgroundGradientEnd">
        <AppHeader />
        <div class="flex flex-col flex-grow">
            <RouterView />
        </div>
        <AppFooter />
    </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";

import { useTheme } from "./composables/useTheme";
import { useSession } from "./composables/useSession";
import { useFooter } from "./composables/useFooter";

import AppHeader from "./components/global/AppHeader.vue";
import AppFooter from "./components/global/AppFooter.vue";


const {darkMode, initializeTheme} = useTheme();
const {getSessionStatus, getSessionUser} = useSession();

onMounted(async () => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }

    await getSessionStatus();
    await getSessionUser();
});
</script>