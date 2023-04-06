<template>
    <div>
        <div :class="{
        'theme-light': !darkMode,
        'theme-dark': darkMode
        }" class="flex flex-col min-h-screen bg-gradient-to-br from-themeBackgroundGradientStart to-themeBackgroundGradientEnd">
            <div class="flex flex-col flex-grow justify-center items-center pt-8 gap-4 px-5 mx-auto my-8">
                <div class="max-w-xl text-center">
                    <h2 class="mb-8 font-extrabold text-9xl text-themeText">
                        <span class="sr-only">Error</span>{{props.error.statusCode}}
                    </h2>
                    <p class="text-2xl md:text-3xl font-semibold text-themeText">{{subtitle}}</p>
                    <p class="mt-4 mb-8 text-md md:text-lg text-themeText">{{text}}</p>
                    <ButtonComponent text="Back to Homepage" type="primary" :onClicked="handleError" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import {useTheme} from "./composables/useTheme";
import {clearError} from "nuxt/app";

import ButtonComponent from "./components/lib/ButtonComponent.vue";
import {computed, onMounted} from "vue";

const {darkMode, initializeTheme} = useTheme();

onMounted(() => {
    if (typeof window !== "undefined") {
        const isDarkModePreferred = window.matchMedia("(prefers-color-scheme: dark)").matches;
        initializeTheme(isDarkModePreferred);
    }
});

const props = defineProps({
    error: Object
})

const handleError = () => clearError({ redirect: '/' })

const subtitle = computed(() => {
    if (props.error.statusCode === 404) {
        return "Sorry, we couldn't find this page."
    } else {
        return "Sorry, something went wrong."
    }
});

const text = computed(() => {
    if (props.error.statusCode === 404) {
        return "The journey took a wrong path. Maybe start over?"
    } else {
        return "This was supposed to be a smooth journey. Maybe try again?"
    }
});
</script>