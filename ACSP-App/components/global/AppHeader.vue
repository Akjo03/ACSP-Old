<template>
	<header class="flex flex-row p-4 border-b-2 border-white/5">
		<div class="flex flex-row w-full">
            <div class="flex flex-row justify-center items-center">
                <NuxtLink class="p-2" href="/">
                    <img src="~/assets/img/logo_512_transparent.png" alt="ACSP Logo" class="w-10 h-10">
                </NuxtLink>
            </div>
            <div class="flex flex-1 flex-row justify-center"></div>
            <div class="flex flex-row justify-center items-center">
                <img v-if="isUserLoggedIn && sessionUserAvatar" :src="sessionUserAvatar" class="w-10 h-10 rounded-full"  alt="User Avatar"/>
				<ButtonComponent v-if="!isUserLoggedIn" text="Log in" :icon="faArrowRight" icon-placement="right" :onClicked="login" :disabled="isUserLoggedIn" />
            </div>
		</div>
	</header>
</template>

<script setup lang="ts">
import {navigateTo, useRuntimeConfig, useRoute} from "nuxt/app";
import {faArrowRight} from "@fortawesome/pro-regular-svg-icons";

import {useSession} from "../../composables/useSession";

import ButtonComponent from "../lib/ButtonComponent.vue";

const config = useRuntimeConfig();
const {sessionUserAvatar, isUserLoggedIn} = useSession();

const loginUrl = config.public.backendUrl + "/api/auth/login";
const login = () => {
    navigateTo(loginUrl, {external: true});
};
</script>