export interface UserSessionStatusDto {
    userId: string;
    sessionId: string;
    sessionStatus: "active" | "inactive" | "onboarding"
}