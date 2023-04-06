<template>
    <div class="relative isolate pt-14">
        <div class="py-24 lg:pb-40">
            <div class="mx-auto max-w-7xl px-6 lg:px-8">
                <div class="mx-auto max-w-2xl text-center">
                    <h1 class="text-4xl font-bold tracking-tight text-themeText md:text-6xl">Akjo's Computer Science Program</h1>
                    <p class="mt-6 text-lg leading-8 text-themeTextSubtle">Anim aute id magna aliqua ad ad non deserunt sunt. Qui irure qui lorem cupidatat commodo. Elit sunt amet fugiat veniam occaecat fugiat aliqua.</p>
                    <div class="mt-10 flex items-center justify-center gap-x-6">
                        <ButtonComponent text="Get Started" type="discord" :icon="faDiscord" :onClicked="goToDiscord" />
                        <ButtonComponent text="Learn More" :icon="faArrowRight" icon-placement="right" />
                    </div>
                </div>
                <img src="" alt="App Screenshot" class="aspect-w-16 aspect-h-9 mt-16 rounded-md bg-white/5 shadow-2xl ring-1 ring-white/10 sm:mt-24" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useRuntimeConfig, navigateTo } from "nuxt/app";

import { faDiscord } from "@fortawesome/free-brands-svg-icons";
import { faArrowRight } from "@fortawesome/pro-regular-svg-icons";

import ButtonComponent from "../lib/ButtonComponent.vue";

const config = useRuntimeConfig();

const discordUrl = config.public.discordServerLink;
const goToDiscord = () => {
    navigateTo(discordUrl, { external: true });
};
</script>