import {useRuntimeConfig} from "nuxt/app";

export const getFromApi = async <T>(endpoint: string, sessionId: string, headers: any = {}): Promise<T> => {
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

export const getDirectFromApi = async <T>(endpoint: string, headers: any = {}): Promise<T> => {
    const config = useRuntimeConfig();

    return await $fetch(`${config.public.backendUrl}/api${endpoint}`, {
        headers: {
            "Content-Type": "application/json",
            ...headers,
        },
        method: "GET",
        credentials: "include",
    });
}

export const postToApi = async <T>(endpoint: string, sessionId: string, body: any, headers: any = {}): Promise<T> => {
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

export const postDirectToApi = async <T>(endpoint: string, body: any, headers: any = {}): Promise<T> => {
    const config = useRuntimeConfig();

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