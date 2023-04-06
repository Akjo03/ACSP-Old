import {ref} from 'vue';
import {getDirectFromApi} from './fetch';
import {UserSessionStatusDto} from '../types/dto/user/UserSessionStatusDto';

export const useSession = () => {
    const status = ref<UserSessionStatusDto | null>(null);

    const getSessionStatus = async (userId: string | null) => {
        if (userId === null || userId === undefined) {
            status.value = null;
            return;
        }
        try {
            status.value = await getDirectFromApi<UserSessionStatusDto>(`/user/${userId}/session/status`);
        } catch (e) {
            console.error(e);
            status.value = null;
        }
    };

    return {
        userSessionStatus: status,
        getSessionStatus,
    };
};