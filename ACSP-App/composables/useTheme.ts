import { ref } from "vue";

type Theme = "light" | "dark";
const LOCAL_STORAGE_THEME_KEY = "theme";

const darkMode = ref(false);

const setTheme = (newTheme: Theme) => {
    if (typeof window !== "undefined") {
        localStorage.setItem(LOCAL_STORAGE_THEME_KEY, newTheme);
    }
    darkMode.value = newTheme === "dark";
};

const initializeTheme = (isDarkModePreferred: boolean) => {
    const themeFromLocalStorage = typeof window !== "undefined" ? localStorage.getItem(LOCAL_STORAGE_THEME_KEY) : null;

    if (themeFromLocalStorage) {
        setTheme(themeFromLocalStorage as Theme);
    } else {
        setTheme(isDarkModePreferred ? "dark" : "light");
    }
};

export const useTheme = () => {
    return {
        darkMode,
        setTheme,
        initializeTheme,
    };
};