<template>
  <div class="flex flex-1">
    <component :is="currentPageComponent" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import OnboardingLanguagePage from './pages/OnboardingLanguagePage.vue'

type Page = {
  id: string,
  component: any,
}

const pages: Page[] = [
  { id: 'languageSelect', component: OnboardingLanguagePage }
]

const currentPage = ref('languageSelect')
const currentPageComponent = computed(() => {
  const page = pages.find(page => page.id === currentPage.value)
  return page?.component
})
</script>