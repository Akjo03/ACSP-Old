<template>
  <div class="flex flex-col items-center justify-center h-screen bg-themeBackground">
    <p class="pt-4 px-8 text-center text-2xl text-themeText break-all"><span class="font-bold">Session ID:</span> {{ sessionId }}</p>
    <p class="pt-4 px-8 text-center text-2xl text-themeText break-all"><span class="font-bold">Session Token:</span> {{ sessionToken }}</p>
    <p class="pt-4 px-8 text-center text-2xl text-themeText break-all"><span class="font-bold">User Email:</span> {{ userEmail }}</p>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useCookie, useRuntimeConfig} from 'nuxt/app';

const config = useRuntimeConfig();
const sessionIdCookie = useCookie('session');
const sessionTokenCookie = useCookie('session_token');
const sessionId = ref('');
const sessionToken = ref('');
const userEmail = ref('');

onMounted(() => {
  sessionId.value = sessionIdCookie.value || '';
  sessionToken.value = sessionTokenCookie.value || '';
});

// TODO: Figure out how to make an authenticated request to the API to get the user's email
</script>