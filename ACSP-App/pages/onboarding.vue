<template>
  <p class="pt-4 text-center text-2xl"><span class="font-bold">Session ID:</span> {{ sessionId }}</p>
  <p class="pt-4 text-center text-2xl"><span class="font-bold">Session Token:</span> {{ sessionToken }}</p>
  <p class="pt-4 text-center text-2xl"><span class="font-bold">User Email:</span> {{ userEmail }}</p>
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
</script>