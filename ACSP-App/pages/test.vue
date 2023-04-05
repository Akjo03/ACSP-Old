<template>
    <div>
        <div :class="{
            'theme-light': !darkMode,
            'theme-dark': darkMode
        }" class="flex flex-col min-h-screen bg-gradient-to-br from-themeBackgroundGradientStart to-themeBackgroundGradientEnd">
            <ButtonComponent type="discord" :icon="faDiscord" text="Get started" />
        </div>
    </div>
</template>

<script setup lang="ts">
import {onMounted} from "vue";
import {useTheme} from "../composables/useTheme";
import { faDiscord } from "@fortawesome/free-brands-svg-icons";

import ButtonComponent from "../components/lib/ButtonComponent.vue";

const {darkMode, initializeTheme} = useTheme();

onMounted(() => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }
});
</script>