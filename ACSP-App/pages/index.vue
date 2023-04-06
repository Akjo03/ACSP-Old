<template>
	<div>
		<p class="text-themeText">Session status: {{ sessionStatus }}</p>
	</div>
</template>

<script setup lang="ts">
import {ref} from "vue";
import {useCookie} from "nuxt/app";
import {getDirectFromApi} from "../composables/fetch";
import {UserSessionStatusDto} from "../types/dto/user/UserSessionStatusDto";
import {onMounted} from "vue";

const sessionStatus = ref("unknown")

onMounted(async () => {
    const userId = useCookie('user_id');

    try {
        await getDirectFromApi<UserSessionStatusDto>(`/user/${userId.value}/session/status`).then((res) => {
			sessionStatus.value = res.sessionStatus;
		});
    } catch (e) {
        console.log(e);
    }
})
</script>