import {useRuntimeConfig} from "nuxt/app";

export const getFromApi = async (endpoint: string, sessionId: string, headers: any = {}) => {
    const config = useRuntimeConfig();

    return await $fetch(`${config.public.backendUrl}/proxy${endpoint}`, {
        headers: {
            "Content-Type": "application/json",
            "X-Session-ID": sessionId,
            ...headers,
        },
        method: "GET",
        credentials: "include",
    });
}

export const postToApi = async (endpoint: string, sessionId: string, body: any, headers: any = {}) => {
    const config = useRuntimeConfig();

    return $fetch(`${config.public.backendUrl}/proxy${endpoint}`, {
        headers: {
            "Content-Type": "application/json",
            "X-Session-ID": sessionId,
            ...headers,
        },
        method: "POST",
        body: JSON.stringify(body),
        credentials: "include",
    });
}