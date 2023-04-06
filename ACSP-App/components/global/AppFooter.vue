<template>
    <footer class="border-t-2 border-white/5">
        <div class="mx-auto flex flex-row items-center justify-between py-4 px-6 lg:px-12">
            <div class="hidden sm:flex flex-row gap-4">
                <a href="#" class="text-xs lg:text-sm text-themeTextLink hover:text-themeTextLinkHover select-none">Imprint</a>
                <p class="text-xs lg:text-sm text-themeText select-none">•</p>
                <a href="#" class="text-xs lg:text-sm text-themeTextLink hover:text-themeTextLinkHover select-none">Privacy Policy</a>
            </div>
            <div v-if="footerLinks.length > 0" class="hidden sm:flex flex-row pl-4 pr-4 gap-4">
                <p class="text-xs lg:text-sm text-themeText select-none">–</p>
                <div v-for="link in footerLinks" :key="link.index" class="flex flex-row gap-4">
                    <NuxtLink :href="link.url" :class="{
                        'text-xs lg:text-sm text-themeTextLink hover:text-themeTextLinkHover select-none': !link.danger,
                        'text-xs lg:text-sm text-themeTextLinkDanger hover:text-themeTextLinkDangerHover select-none': link.danger
                    }">{{ link.name }}</NuxtLink>
                    <p v-if="link.index < footerLinks.length - 1" class="text-xs lg:text-sm text-themeText select-none">•</p>
                </div>
            </div>
            <div class="hidden sm:flex flex-grow"></div>
            <div class="flex flex-row flex-grow sm:flex-grow-0 justify-center sm:justify-end items-center">
                <p class="text-xs lg:text-sm text-themeText">AkjoStudios © {{ copyrightYear }}</p>
            </div>
        </div>
    </footer>
</template>

<script setup lang="ts">
import {copyrightYear} from "../../composables/globals";
import {useFooter} from "../../composables/useFooter";

const {footerLinks} = useFooter();
</script>