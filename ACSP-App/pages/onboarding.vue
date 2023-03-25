<template>
  <p class="pt-4 text-center text-2xl">{{ userId }}</p>
</template>

<script setup lang="ts">
import {useRuntimeConfig} from "nuxt/app";
import {useRoute} from "vue-router";

const route = useRoute();
  const config = useRuntimeConfig();

  const userId = route.query.userId;
</script>