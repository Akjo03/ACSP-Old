<template>
	<div>
        <div :class="{
            'theme-light': !darkMode,
            'theme-dark': darkMode
        }" class="flex flex-col min-h-screen bg-gradient-to-br from-themeBackgroundGradientStart to-themeBackgroundGradientEnd">
            <AppHeader />
            <RouterView></RouterView>
        </div>
	</div>
</template>

<script setup lang="ts">
import {onMounted} from "vue";
import {useTheme} from "./composables/useTheme";

const {darkMode, initializeTheme} = useTheme();

onMounted(() => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }
});
</script>