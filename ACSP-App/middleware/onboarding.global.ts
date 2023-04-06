import {defineNuxtRouteMiddleware, navigateTo} from "nuxt/app";
import {useSession} from "../composables/useSession";

// noinspection JSUnusedGlobalSymbols
export default defineNuxtRouteMiddleware(async (to, from) => {
    const {getSessionStatus, sessionStatus} = useSession();
    await getSessionStatus();

    if (to.path !== "/onboarding") {
        if (sessionStatus.value === "onboarding") {
            return navigateTo("/onboarding");
        }
    }

    if (to.path === "/onboarding") {
        if (sessionStatus.value !== "onboarding") {
            return navigateTo("/");
        }
    }
});