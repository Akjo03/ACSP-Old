<template>
    <div class="flex flex-col py-4 items-center">
        <div class="hidden md:flex flex-row gap-20 max-w-2xl h-6 md:h-8 rounded-2xl shadow-2xl bg-white/5 ring-1 ring-white/10">
            <div v-for="page in pages" :key="page.id" class="flex flex-row flex-1 items-center justify-center px-4 my-4">
                <div class="flex flex-col justify-center">
                    <div class="flex flex-col">
                        <div v-if="!page.completed" class="flex flex-col justify-center items-center w-4 h-4 md:w-6 md:h-6 rounded-full bg-white/5 ring-1 ring-white/10">
                            <span class="text-xs md:text-sm text-center font-semibold text-themeText">{{page.index}}</span>
                        </div>
                        <div v-else class="hidden md:flex flex-col justify-center items-center w-4 h-4 md:w-6 md:h-6 rounded-full bg-white/5 ring-1 ring-white/10">
                            <Vue3Lottie
                                :animation-data="checkIconIn"
                                :auto-play="true"
                                :loop="false"
                                class="w-4 h-4 md:w-6 md:h-6"
                            />
                        </div>
                    </div>
                </div>
                <div class="flex flex-col pl-4">
                    <p class="text-xs md:text-sm font-semibold text-themeText">{{page.title}}</p>
                </div>
            </div>
        </div>
    </div>
    <div class="flex flex-1">
        <component :is="currentPageComponent" />
    </div>
    <div class="flex flex-row items-center justify-center">
        <div v-for="button in currentPageButtons" class="flex flex-row justify-center items-center">
            <button @click="button.onClick()" :class="getButtonClass(button.type)">
                <span class="text-sm md:text-md font-semibold text-themeButtonText">{{button.text}}</span>
            </button>
        </div>
    </div>

</template>

<script setup lang="ts">
import {ref, computed} from "vue"
import {Vue3Lottie} from "vue3-lottie";

import OnboardingLanguagePage from "./pages/OnboardingLanguagePage.vue"
import OnboardingWelcomePage from "./pages/OnboardingWelcomePage.vue"

import checkIconIn from "../../assets/lottie/check/check-in.json";

interface Button {
    text: string,
    type: "submit" | "cancel" | "primary" | "secondary",
    onClick: () => void
}

interface Page {
    index: number,
    id: string,
    title: string,
    component: any,
    buttons: Button[],
    completed?: boolean
}

const pages: Page[] = [
    {
        index: 1,
        id: "languageSelect",
        title: "Language",
        component: OnboardingLanguagePage,
        buttons: [
            {
                text: "Continue",
                type: "primary",
                onClick: () => {
                    getPage(currentPage.value).completed = true
                    nextPage()
                }
            }
        ]
    },
    {
        index: 2,
        id: "welcome",
        title: "Welcome",
        component: OnboardingWelcomePage,
        buttons: []
    }
]

const currentPage = ref("languageSelect")

function getPage(id: string): Page {
    return pages.find(page => page.id === id)
}

function nextPage() {
    const currentPageIndex = pages.findIndex(page => page.id === currentPage.value)
    if (currentPageIndex < pages.length - 1) {
        currentPage.value = pages[currentPageIndex + 1].id
    }
}

const currentPageComponent = computed(() => {
    const page = getPage(currentPage.value)
    return page?.component
})

const currentPageButtons = computed(() => {
    const page = getPage(currentPage.value)
    return page ? page.buttons : [];
});

function getButtonClass(type: "submit" | "cancel" | "primary" | "secondary") {
    let base = "rounded-md px-3 py-2 m-4 text-sm font-semibold shadow-sm focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2"
    switch (type) {
        case "submit":
            return base + " bg-themeButtonSuccess text-themeButtonText hover:bg-themeButtonSuccessHover transition-all focus-visible:outline-themeButtonSuccess"
        case "cancel":
            return base + " bg-themeButtonDanger text-themeButtonText hover:bg-themeButtonDangerHover transition-all focus-visible:outline-themeButtonDanger"
        case "primary":
            return base + " bg-themeButtonPrimary text-themeButtonText hover:bg-themeButtonPrimaryHover transition-all focus-visible:outline-themeButtonPrimary"
        case "secondary":
            return base + " bg-themeButton text-themeButtonText hover:bg-themeButtonHover transition-all focus-visible:outline-themeButton"
    }
}
</script>