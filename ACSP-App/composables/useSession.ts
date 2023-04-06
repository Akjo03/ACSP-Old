import {computed, ref} from "vue";
import {getDirectFromApi, getFromApi} from "./fetch";
import {UserSessionStatusDto} from "../types/dto/user/UserSessionStatusDto";
import {AcspUser} from "../types/model/AcspUser";
import {UserDto} from "../types/dto/user/UserDto";

const sessionId = ref(null as string | null);
const sessionStatus = ref("unknown" as "unknown" | "active" | "inactive" | "onboarding");
const sessionUser = ref(null as AcspUser | null);
const sessionUserAvatar = ref(null as string | null);
const isUserLoggedIn = computed(() => sessionStatus.value !== "unknown" && sessionStatus.value !== "inactive");

const getSessionStatus = async () => {
    try {
        await getDirectFromApi<UserSessionStatusDto>(`/user/session/status`).then((res) => {
            sessionId.value = res.sessionId;
            sessionStatus.value = res.sessionStatus;
        });
    } catch (ignored) { console.error("No user found or user id invalid.") }
}

const getSessionUser = async () => {
    try {
        await getFromApi("/user/@me", sessionId.value).then((res: UserDto) => {
            sessionUser.value = res.user;
        })
    } catch (ignored) { console.error("No user found or user id invalid.") }

    try {
        await getFromApi("/user/@me/avatar", sessionId.value).then((res: string) => {
            sessionUserAvatar.value = res;
        })
    } catch (ignored) { console.error("No user avatar found or user id invalid.") }
}

export const useSession = () => {
    return {sessionId, sessionStatus, sessionUser, sessionUserAvatar, isUserLoggedIn, getSessionStatus, getSessionUser}
}