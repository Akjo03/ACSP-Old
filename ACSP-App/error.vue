<template>
    <div>
        <div :class="{
        'theme-light': !darkMode,
        'theme-dark': darkMode
        }" class="flex flex-col min-h-screen bg-gradient-to-br from-themeBackgroundGradientStart to-themeBackgroundGradientEnd">
            <div class="flex flex-col flex-grow justify-center items-center pt-8 gap-4 px-5 mx-auto my-8">
                <div class="max-w-lg text-center">
                    <h2 class="mb-8 font-extrabold text-9xl text-themeText">
                        <span class="sr-only">Error</span>{{props.error.statusCode}}
                    </h2>
                    <p class="text-2xl font-semibold md:text-3xl text-themeText">Sorry, we couldn't find this page.</p>
                    <p class="mt-4 mb-8 text-themeText">The journey took a wrong path. Please go back to the homepage.</p>
                    <ButtonComponent text="Back to Homepage" type="primary" :onClicked="handleError" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import {onMounted} from "vue";
import {useTheme} from "./composables/useTheme";
import {clearError} from "nuxt/app";

import ButtonComponent from "./components/lib/ButtonComponent.vue";

const {darkMode, initializeTheme} = useTheme();

const props = defineProps({
    error: Object
})

onMounted(() => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }
});

const handleError = () => clearError({ redirect: '/' })
</script>