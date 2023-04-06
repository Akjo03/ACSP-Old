import {useRuntimeConfig} from "nuxt/app";

export const getFromApi = async (endpoint: string, sessionId: string, headers: any = {}) => {
    const config = useRuntimeConfig();

    console.log("Making GET request to: " + `${config.public.backendUrl}/proxy${endpoint}`);
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

export const getDirectFromApi = async (endpoint: string, headers: any = {}) => {
    const config = useRuntimeConfig();

    console.log("Making GET request to: " + `${config.public.backendUrl}/api${endpoint}`);
    return await $fetch(`${config.public.backendUrl}/api${endpoint}`, {
        headers: {
            "Content-Type": "application/json",
            ...headers,
        },
        method: "GET",
        credentials: "include",
    });
}

export const postToApi = async (endpoint: string, sessionId: string, body: any, headers: any = {}) => {
    const config = useRuntimeConfig();

    console.log("Making POST request to: " + `${config.public.backendUrl}/proxy${endpoint}`);
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

export const postDirectToApi = async (endpoint: string, body: any, headers: any = {}) => {
    const config = useRuntimeConfig();

    console.log("Making POST request to: " + `${config.public.backendUrl}/api${endpoint}`);
    return $fetch(`${config.public.backendUrl}/api${endpoint}`, {
        headers: {
            "Content-Type": "application/json",
            ...headers,
        },
        method: "POST",
        body: JSON.stringify(body),
        credentials: "include",
    });
}