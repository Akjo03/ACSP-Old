<template>
    <h1 class="text-3xl font-semibold text-themeText text-center pt-4">Onboarding</h1>
</template>

<script setup lang="ts">
import {useRuntimeConfig} from "nuxt/app";

import {useFooter} from "../composables/useFooter";

const config = useRuntimeConfig();
const { setFooterLinks } = useFooter();

setFooterLinks([
    {
        index: 0,
        name: "Abort Onboarding",
        url: config.public.backendUrl + "/proxy/user/onboarding/abort",
        danger: true,
    }
])
</script>