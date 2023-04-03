<template>
  <div class="flex flex-col items-center justify-center h-screen bg-themeBackground">
    <p class="pt-4 px-8 text-center text-2xl text-themeText break-all"><span class="font-bold">Session ID:</span> {{ sessionId }}</p>
    <p class="pt-4 px-8 text-center text-2xl text-themeText break-all"><span class="font-bold">E-Mail:</span> {{ userEmail }}</p>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useCookie, useRuntimeConfig} from 'nuxt/app';
import {getFromApi} from "../composables/fetch";

const config = useRuntimeConfig();
const sessionIdCookie = useCookie('session_id');
const sessionId = ref('');
const userEmail = ref('');

onMounted(() => {
  sessionId.value = sessionIdCookie.value || '';

  getFromApi('/user/@me', sessionId.value).then((response) => {
    userEmail.value = response.user.email;
  });
});
</script>