<template>
    <div>
        <div :class="{
            'theme-light': !darkMode,
            'theme-dark': darkMode
        }" class="flex flex-col min-h-screen bg-gradient-to-br from-themeBackgroundGradientStart to-themeBackgroundGradientEnd">
        </div>
    </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {useTheme} from "../composables/useTheme";

import ButtonComponent from "../components/lib/ButtonComponent.vue";

const {darkMode, initializeTheme, setTheme} = useTheme();

onMounted(() => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }
});
</script>