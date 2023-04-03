<template>
  <footer class="border-t-2 border-themeBorder">
    <div class="mx-auto flex flex-row items-center justify-between py-4 px-6 lg:px-12">
      <div class="hidden sm:flex flex-row gap-4">
        <a href="#" class="text-sm text-themeTextLink hover:text-themeTextLinkHover">Imprint</a>
        <p class="text-xs lg:text-sm text-themeText">•</p>
        <a href="#" class="text-sm text-themeTextLink hover:text-themeTextLinkHover">Privacy Policy</a>
      </div>
      <div v-if="props.links.length > 0" class="hidden sm:flex flex-row pl-4 pr-4">
        <div v-for="link in links" :key="link.name" class="flex flex-row gap-4">
          <p class="text-xs lg:text-sm text-themeText">–</p>
          <a :href="link.url" :class="{
            'text-xs lg:text-sm text-themeTextLink hover:text-themeTextLinkHover': !link.danger,
            'text-xs lg:text-sm text-themeTextDangerLink hover:text-themeTextDangerLinkHover': link.danger
          }">{{link.name}}</a>
        </div>
      </div>
      <div class="hidden sm:flex flex-grow"></div>
      <div class="flex flex-row flex-grow sm:flex-grow-0 justify-center sm:justify-end items-center">
        <p class="text-xs lg:text-sm text-themeText">AkjoStudios © {{copyrightYear}}</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import {defineProps, ref} from "vue";
import {copyrightYear} from "../../composables/globals";

const props = defineProps<{
    links: {
        name: string,
        url: string,
        danger?: boolean
    }[]
}>();

const links = ref(props.links);
</script>