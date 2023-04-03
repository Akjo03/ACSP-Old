<template>
  <header class="border-b-2 border-themeBorder">
    <nav class="mx-auto flex items-center justify-between py-4 px-6 lg:px-12" aria-label="Global">
      <div class="flex lg:flex-1">

      </div>
      <a href="/" class="flex flex-row items-center -m-1.5 p-1.5">
          <span class="sr-only">ACSP Logo</span>
          <img class="h-11" src="~/assets/img/logo_512_transparent.png" alt="" />
      </a>
      <div class="flex flex-1 justify-end">
          <img class="inline-block h-11 w-11 rounded-full" :src="userAvatar" :style="{
              'display': userAvatar ? 'block' : 'none'
          }" alt="Avatar" />
      </div>
    </nav>
  </header>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {useCookie} from "nuxt/app";
import {getFromApi} from "../../composables/fetch";

const sessionIdCookie = useCookie("session_id");
const sessionId = ref("");

const userAvatar = ref("https://ui-avatars.com/api");

onMounted(() => {
    sessionId.value = sessionIdCookie.value || "";

    getFromApi("/user/@me/avatar", sessionId.value).then((response) => {
      userAvatar.value = response;
    })
});
</script>