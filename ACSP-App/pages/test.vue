<template>
    <div :class="{
        'theme-light': !darkMode,
        'theme-dark': darkMode
    }" class="flex flex-col min-h-screen bg-gradient-to-br from-themeBackgroundGradientStart to-themeBackgroundGradientEnd">
        <div class="flex flex-col flex-grow justify-center items-center pt-8">
			<ButtonComponent text="Change Theme" type="primary" :onClicked="changeTheme" />
		</div>
    </div>
</template>

<script setup lang="ts">
import {onMounted} from "vue";
import {useTheme} from "../composables/useTheme";
import ButtonComponent from "../components/lib/ButtonComponent.vue";

const {darkMode, initializeTheme, setTheme} = useTheme();

onMounted(() => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }
});

function changeTheme() {
	setTheme(darkMode.value ? "light" : "dark");
}
</script>