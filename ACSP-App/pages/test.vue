<template>
    <div :class="{
        'theme-light': !darkMode,
        'theme-dark': darkMode
    }" class="flex flex-col min-h-screen bg-gradient-to-br from-themeBackgroundGradientStart to-themeBackgroundGradientEnd">
        <div class="flex flex-col flex-grow justify-center items-center pt-8 gap-4">
			<ButtonComponent :icon="icon" icon-size="lg" text="Switch Theme" :onClicked="switchTheme" />
		</div>
    </div>
</template>

<script setup lang="ts">
import {useTheme} from "../composables/useTheme";
import {onMounted, computed} from "vue";
import ButtonComponent from "../components/lib/ButtonComponent.vue";

import { faMoon, faSunBright } from "@fortawesome/pro-regular-svg-icons";

const {darkMode, initializeTheme, setTheme} = useTheme();

onMounted(() => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }
});

function switchTheme() {
	setTheme(!darkMode.value ? "dark" : "light");
}

const icon = computed(() => {
	return darkMode.value ? faSunBright : faMoon;
});
</script>