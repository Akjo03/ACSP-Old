import {ref} from 'vue';
import {getDirectFromApi} from './fetch';
import {UserSessionStatusDto} from '../types/dto/user/UserSessionStatusDto';

const sessionStatus = ref("unknown" as "unknown" | "active" | "inactive" | "onboarding");

const getSessionStatus = async () => {
    try {
        await getDirectFromApi<UserSessionStatusDto>(`/user/session/status`).then((res) => {
            sessionStatus.value = res.sessionStatus;
        });
    } catch (ignored) { console.error("No user found or user id invalid.") }
}

export const useSession = () => {
    return {sessionStatus, getSessionStatus}
}