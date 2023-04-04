<template>
    <div class="flex flex-col py-4 items-center">
        <div class="flex flex-row w-[50%] h-6 md:h-8 rounded-2xl shadow-2xl bg-white/5 ring-1 ring-white/10">

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
    id: string,
    component: any,
}

const pages: Page[] = [
    {id: 'languageSelect', component: OnboardingLanguagePage}
]

const currentPage = ref('languageSelect')
const currentPageComponent = computed(() => {
    const page = pages.find(page => page.id === currentPage.value)
    return page?.component
})
</script>