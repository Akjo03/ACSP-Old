<template>
    <button @click="onClicked()" :class="`flex px-4 py-2 rounded-md shadow-sm ${getButton} ${getButtonHover} ${getButtonActive} transition-all`">
        <span class="text-themeButtonText">{{ text }}</span>
    </button>
</template>

<script lang="ts">
import {Vue3Lottie} from "vue3-lottie";

export default {
    props: {
        text: String,
        type: {
            type: String,
            default: "secondary"
        },
        showIcon: {
            type: Boolean,
            default: false
        },
        onClicked: Function
    }
}
</script>

<script setup lang="ts">
import {computed} from "vue";
import {Vue3Lottie} from "vue3-lottie";

interface ButtonType {
    name: string;
    colorThemeKey: string;
    icon: Vue3Lottie;
}

const buttonTypes: ButtonType[] = [
    {
        name: "primary",
        colorThemeKey: "bg-themeButtonPrimary",
        icon: null
    },
    {
        name: "secondary",
        colorThemeKey: "bg-themeButtonSecondary",
        icon: null
    }
]

const {text, type, onClicked} = defineProps<{
    text: string;
    type: string;
    onClicked: () => void;
}>();

const getButton = computed(() => {
    const buttonType = buttonTypes.find((buttonType) => buttonType.name === type);
    return buttonType ? buttonType.colorThemeKey : "bg-themeButtonSecondary";
});

const getButtonHover = computed(() => {
    const buttonType = buttonTypes.find((buttonType) => buttonType.name === type);
    return buttonType ? `hover:${buttonType.colorThemeKey}Hover` : "bg-themeButtonSecondaryHover";
});

const getButtonActive = computed(() => {
    const buttonType = buttonTypes.find((buttonType) => buttonType.name === type);
    return buttonType ? `active:${buttonType.colorThemeKey}Active` : "bg-themeButtonSecondaryActive";
});
</script>