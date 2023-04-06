<template>

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