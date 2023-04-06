import {defineNuxtRouteMiddleware, navigateTo} from "nuxt/app";
import {useSession} from "../composables/useSession";

// noinspection JSUnusedGlobalSymbols
export default defineNuxtRouteMiddleware(async (to, from) => {
    const {getSessionStatus, sessionStatus} = useSession();
    await getSessionStatus();

    if (to.path !== "/dashboard") {
        if (sessionStatus.value === "active") {
            return navigateTo("/dashboard");
        }
    }

    if (to.path === "/dashboard") {
        if (sessionStatus.value === "onboarding") {
            return navigateTo("/onboarding");
        } else if (sessionStatus.value !== "active") {
            return navigateTo("/");
        }
    }
});