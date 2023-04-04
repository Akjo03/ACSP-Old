<template>
    <div class="flex flex-col py-4 items-center">
        <div class="flex flex-row w-[50%] max-w-2xl h-6 md:h-8 rounded-2xl shadow-2xl bg-white/5 ring-1 ring-white/10">
            <div v-for="page in pages" :key="page.id" class="flex flex-row flex-1 px-4 my-4">
                <div class="flex flex-col items-center justify-center flex-1">

                </div>
            </div>
        </div>
    </div>
    <div class="flex flex-1">
        <component :is="currentPageComponent"/>
    </div>
</template>

<script setup lang="ts">
import {ref, computed} from 'vue'
import OnboardingLanguagePage from './pages/OnboardingLanguagePage.vue'

type Page = {
    index: number,
    id: string,
    component: any,
    completed?: boolean
}

const pages: Page[] = [
    {index: 1, id: 'languageSelect', component: OnboardingLanguagePage}
]

const currentPage = ref('languageSelect')
const currentPageComponent = computed(() => {
    const page = pages.find(page => page.id === currentPage.value)
    return page?.component
})
</script>